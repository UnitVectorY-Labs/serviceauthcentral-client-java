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

import java.time.Instant;
import java.io.IOException;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;

import lombok.Builder;
import lombok.NonNull;

/**
 * The GCPJwtCredentialsProvider class provides credentials from a GCP service
 * account.
 * 
 * This is an optional dependency and requires including
 * google-auth-library-oauth2-http as it is an optional dependency.
 * 
 * &lt;dependency&gt;
 * &lt;groupId&gt;com.google.auth&lt;/groupId&gt;
 * &lt;artifactId&gt;google-auth-library-oauth2-http&lt;/artifactId&gt;
 * &lt;/dependency&gt;
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class GCPJwtCredentialsProvider implements CredentialsProvider {

    private final String clientId;

    private final IdTokenCredentials idTokenCredentials;

    @Builder
    private GCPJwtCredentialsProvider(@NonNull String clientId, @NonNull String targetAudience,
            GoogleCredentials googleCredentials) {
        this.clientId = clientId;

        // Use the provided credentials or get the application default credentials
        GoogleCredentials credentials = googleCredentials;
        if (credentials == null) {
            // Credentials not provided, get the application default credentials
            try {
                credentials = GoogleCredentials.getApplicationDefault();
            } catch (IOException e) {
                throw new SACException("Failed to get application default credentials", e);
            }
        }

        // Check if the credentials are an IdTokenProvider, if it isn't it cannot be
        // used otherwise
        if (!(credentials instanceof IdTokenProvider)) {
            throw new SACException("GoogleCredentials is not an IdTokenProvider");
        }

        IdTokenProvider idTokenProvider = (IdTokenProvider) credentials;

        // Create IdTokenCredentials with the target audience
        this.idTokenCredentials = IdTokenCredentials.newBuilder()
                .setIdTokenProvider(idTokenProvider)
                // Setting the audience is a key part of the security model
                .setTargetAudience(targetAudience)
                .build();
    }

    @Override
    public SACCredentials getCredentials() {

        // Get the ID token, google-auth-library-java will automatically refresh the
        // token
        AccessToken accessToken;
        try {
            this.idTokenCredentials.getIdToken();
            accessToken = idTokenCredentials.refreshAccessToken();
        } catch (IOException e) {
            throw new SACException("Failed to get Google identity token", e);
        }

        String idToken = accessToken.getTokenValue();
        Instant expiration = accessToken.getExpirationTime().toInstant();

        return JwtAssertionCredentials.builder().clientId(this.clientId).jwtAssertion(idToken).expiration(expiration)
                .build();
    }

}
