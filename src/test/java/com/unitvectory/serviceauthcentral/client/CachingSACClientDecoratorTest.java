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

import org.junit.jupiter.api.Test;

/**
 * Test class for CachingSACClientDecorator class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class CachingSACClientDecoratorTest {

    @Test
    void getTokenTest() {
        SACClient sacClient = new SACClient() {

            // Counting the number of times this is called used to determine if the response
            // was cached
            private int count = 0;

            @Override
            public TokenResponse getToken(TokenRequest tokenRequest) {
                count++;
                return TokenResponse.builder()
                        .accessToken(count + "")
                        .tokenType("Bearer")
                        .expiresIn(3600)
                        .build();
            }
        };

        CachingSACClientDecorator cachingClient = CachingSACClientDecorator.builder()
                .client(sacClient)
                .build();

        TokenRequest tokenRequest = TokenRequest.builder().audience("foo").build();

        // First call should call the SACClient, the access token will be "1"
        assertEquals("1", sacClient.getToken(tokenRequest).getAccessToken());

        // Now we can get a cached token, it will be "2"
        assertEquals("2", cachingClient.getToken(tokenRequest).getAccessToken());

        // Now we can get a cached token, it will be "2"
        assertEquals("2", cachingClient.getToken(tokenRequest).getAccessToken());

        // If we get a new token, it will be "3"
        assertEquals("3", sacClient.getToken(tokenRequest).getAccessToken());

        // But the cached token will still be "2"
        assertEquals("2", cachingClient.getToken(tokenRequest).getAccessToken());
    }
}