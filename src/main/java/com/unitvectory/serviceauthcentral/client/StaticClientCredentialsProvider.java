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

import lombok.NonNull;

/**
 * The StaticClientCredentialsProvider class provides credentials in the form of
 * a static client id and secret.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class StaticClientCredentialsProvider implements SACCredentialsProvider {

    /**
     * The client credentials
     */
    private final SACClientCredentials credentials;

    /**
     * Create a new StaticClientCredentialsProvider
     * @param clientId The client id
     * @param clientSecret The client secret
     */
    public StaticClientCredentialsProvider(@NonNull String clientId, @NonNull String clientSecret) {
        this.credentials = SACClientCredentials.builder().clientId(clientId).clientSecret(clientSecret).build();
    }

    @Override
    public SACCredentials getCredentials() {
        return this.credentials;
    }
}
