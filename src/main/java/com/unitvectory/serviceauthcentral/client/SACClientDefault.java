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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Builder;
import lombok.NonNull;

/**
 * The SACClientDefault class is the default implementation of the SACClient
 * calling the ServiceAuthCentral API.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class SACClientDefault implements SACClient {

    /**
     * The path to the token endpoint
     */
    private static final String TOKEN_PATH = "/v1/token";

    /**
     * The default user agent
     */
    private static final String DEFAULT_USER_AGENT = "serviceauthcentral-client-java";

    /**
     * The http client
     */
    private final HttpClient httpClient;

    /**
     * The issuer
     */
    private final String issuer;

    /**
     * The complete url for the token endpoint.
     * 
     * If this is not provided the issuer is used suffixed with "/v1/token"
     */
    private final String tokenEndpoint;

    /**
     * The credentials provider used to authenticate to ServiceAuthCentral.
     */
    private final CredentialsProvider credentialsProvider;

    /**
     * The user agent to use when making requests.
     * 
     * This can be customized to identify the client making the request.
     * 
     */
    private final String userAgent;

    @Builder
    private SACClientDefault(HttpClient httpClient,
            @NonNull String issuer,
            String tokenEndpoint,
            @NonNull CredentialsProvider credentialsProvider,
            String userAgent) {

        if (httpClient == null) {
            this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        } else {
            this.httpClient = httpClient;
        }

        this.issuer = issuer;

        if (tokenEndpoint == null) {
            this.tokenEndpoint = this.issuer + TOKEN_PATH;
        } else {
            this.tokenEndpoint = tokenEndpoint;
        }

        this.credentialsProvider = credentialsProvider;
        if (userAgent == null) {
            this.userAgent = DEFAULT_USER_AGENT;
        } else {
            this.userAgent = userAgent;
        }
    }

    @Override
    public TokenResponse getToken(@NonNull TokenRequest request) {

        Map<String, String> params = new HashMap<>();
        if (request.getAudience() != null) {
            params.put("audience", request.getAudience());
        } else {
            // ServiceAuthCentral is an authorization server, so it must have an audience
            throw new SACException("Audience is required");
        }

        if (request.getScopes() != null && request.getScopes().size() > 0) {
            // Scopes are optional
            params.put("scope", String.join(" ", request.getScopes()));
        }

        SACCredentials credentials = this.credentialsProvider.getCredentials();
        if (credentials.isExpired(30)) {
            throw new SACException("Credentials are expired");
        }

        params.putAll(credentials.credentialsMap());

        return this.getToken(params);
    }

    private TokenResponse getToken(Map<String, String> params) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(this.tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", this.userAgent)
                .POST(HttpRequest.BodyPublishers.ofString(UrlFormEncoder.encodeFormParams(params)))
                .build();

        try {
            HttpResponse<String> response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // If the response isn't 200 then return an error
            if (response.statusCode() != 200) {
                throw new SACClientException(JsonParser.parseString(response.body())
                        .getAsJsonObject());
            }

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // Validate the expected fields exist
            if (!responseJson.has("access_token") || !responseJson.has("expires_in")
                    || !responseJson.has("token_type")) {
                throw new SACException("Unexpected token response format.");
            }

            return TokenResponse.builder()
                    .accessToken(responseJson.get("access_token").getAsString())
                    .expiresIn(responseJson.get("expires_in").getAsLong())
                    .tokenType(responseJson.get("token_type").getAsString())
                    .build();
        } catch (IOException | InterruptedException e) {
            throw new SACException("Failed to get token", e);
        }
    }

}
