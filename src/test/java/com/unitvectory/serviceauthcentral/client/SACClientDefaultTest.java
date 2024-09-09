/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test class for SACClientDefault class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class SACClientDefaultTest {

    private SACClientDefault sacClient;
    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);

        StaticClientCredentialsProvider credentialsProvider = StaticClientCredentialsProvider.builder()
                .clientId("testClientId")
                .clientSecret("testSecret")
                .build();

        sacClient = SACClientDefault.builder()
                .httpClient(httpClient)
                .issuer("https://issuer.example.com")
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getTokenTest() throws IOException, InterruptedException {

        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body())
                .thenReturn("{\"access_token\":\"testAccessToken\",\"token_type\":\"Bearer\",\"expires_in\":3600}");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

        TokenResponse response = sacClient
                .getToken(TokenRequest.builder().audience("https:///audience.example.com").build());

        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest capturedRequest = requestCaptor.getValue();

        // Capture and decode the form body from the BodyPublisher
        String requestBody = extractBody(capturedRequest.bodyPublisher().get());

        // Validate the JSON body of the request
        assertEquals(
                "audience=https%3A%2F%2F%2Faudience.example.com&grant_type=client_credentials&client_secret=testSecret&client_id=testClientId",
                requestBody);

        // Validate the response
        assertEquals("testAccessToken", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());
    }

    /**
     * Helper method to extract the body from the HttpRequest.BodyPublisher.
     * 
     * @param bodyPublisher the BodyPublisher to read
     * @return the body content as a String
     */
    private String extractBody(HttpRequest.BodyPublisher bodyPublisher) {
        CompletableFuture<String> future = new CompletableFuture<>();
        bodyPublisher.subscribe(new Subscriber<ByteBuffer>() {
            private final StringBuilder body = new StringBuilder();

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                body.append(StandardCharsets.UTF_8.decode(item).toString());
            }

            @Override
            public void onError(Throwable throwable) {
                future.completeExceptionally(throwable);
            }

            @Override
            public void onComplete() {
                future.complete(body.toString());
            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract body", e);
        }
    }
}