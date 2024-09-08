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

import lombok.NonNull;

/**
 * The SACApiClient class provides the means to interact with the
 * ServiceAuthCentral
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class SACApiClient implements SACClient {

    /**
     * The path to the token endpoint
     */
    private static final String TOKEN_PATH = "/v1/token";

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
     */
    private final String tokenEndpoint;

    /**
     * The jwt-bearer provider
     */
    private final SACJwtBearerProvider jwtBearerProvider;

    /**
     * The client credentials provider
     */
    private final SACClientCredentialsProvider clientCredentialsProvider;

    /**
     * The user agent
     */
    private final String userAgent;

    /**
     * Create a new instance of the SACApiClient class
     * 
     * @param clientParams the client parameters
     */
    public SACApiClient(@NonNull SACClientParams clientParams) {

        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

        this.issuer = clientParams.getIssuer();
        if (clientParams.getTokenEndpoint() == null) {
            this.tokenEndpoint = this.issuer + TOKEN_PATH;
        } else {
            this.tokenEndpoint = clientParams.getTokenEndpoint();
        }

        this.jwtBearerProvider = clientParams.getJwtBearerProvider();
        this.clientCredentialsProvider = clientParams.getClientCredentialsProvider();
        this.userAgent = clientParams.getUserAgent();
    }

    @Override
    public SACTokenResponse getToken(@NonNull SACTokenRequest request) {

        Map<String, String> params = new HashMap<>();
        if (request.getAudience() != null) {
            params.put("audience", request.getAudience());
        } else {
            // ServiceAuthCentral is an authorization server, so it must have an audience
            throw new SACClientException("Audience is required");
        }

        if (request.getScopes() != null && request.getScopes().size() > 0) {
            // Scopes are optional
            params.put("scope", String.join(" ", request.getScopes()));
        }

        if (this.jwtBearerProvider != null) {
            // Always prefer jwt-bearer flow if available
            params.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
            SACJwtBearerParams jwtBearerParams = SACJwtBearerParams.builder()
                    .audience(request.getAudience())
                    .build();
            params.put("assertion", this.jwtBearerProvider.getJwtAssertion(jwtBearerParams));
        } else if (this.clientCredentialsProvider != null) {
            // Fall back to client credentials flow
            params.put("grant_type", "client_credentials");
            params.put("client_id", this.clientCredentialsProvider.getClientId());
            params.put("client_secret", this.clientCredentialsProvider.getClientSecret());
        } else {
            // No way to authenticate
            throw new SACClientException("No client credentials provider or jwt bearer provider set");
        }

        return this.getToken(params);
    }

    private SACTokenResponse getToken(Map<String, String> params) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(this.tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", this.userAgent)
                .POST(HttpRequest.BodyPublishers.ofString(UrlFormEncoder.encodeFormParams(params)))
                .build();

        try {
            HttpResponse<String> response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // if the response isn't 200 then return an error
            if (response.statusCode() != 200) {
                // TODO: Parse out the responses that ServiceAuthCentral returns and include those in the exception for better debugging
                System.out.println(response.body());
                throw new SACClientException("Failed to get token with response code " + response.statusCode());
            }

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // Validate the expected fields exist
            if (!responseJson.has("access_token") || !responseJson.has("expires_in")
                    || !responseJson.has("token_type")) {
                throw new SACClientException("Unexpected token response format: " + responseJson.toString());
            }

            return SACTokenResponse.builder()
                    .accessToken(responseJson.get("access_token").getAsString())
                    .expiresIn(responseJson.get("expires_in").getAsLong())
                    .tokenType(responseJson.get("token_type").getAsString())
                    .build();
        } catch (IOException | InterruptedException e) {
            throw new SACClientException("Failed to get token", e);
        }
    }

}
