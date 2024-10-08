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

import java.util.Map;

import lombok.Builder;
import lombok.Value;

/**
 * Provides the container for the client credentials when authenticating to
 * ServiceAuthCentral.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public final class ClientCredentials implements SACCredentials {

    /**
     * The client id
     */
    String clientId;

    /**
     * The client secret
     */
    String clientSecret;

    @Override
    public Map<String, String> credentialsMap() {
        return Map.of("grant_type", "client_credentials", "client_id", this.clientId, "client_secret",
                this.clientSecret);
    }

    @Override
    public boolean isExpired(int threshold) {
        // Static credentials never expire
        return false;
    }
}
