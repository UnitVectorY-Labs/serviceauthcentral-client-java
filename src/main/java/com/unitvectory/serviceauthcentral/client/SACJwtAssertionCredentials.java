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
import java.util.Map;

import lombok.Builder;
import lombok.Value;

/**
 * Provides the container for the jwt assertion credentials when authenticating
 * to ServiceAuthCentral.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public final class SACJwtAssertionCredentials implements SACCredentials {

    /**
     * The jwt assertion
     */
    String jwtAssertion;

    /**
     * The expiration time
     */
    @Builder.Default
    Instant expiration = Instant.now();

    @Override
    public Map<String, String> credentialsMap() {
        return Map.of("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer", "assertion", this.jwtAssertion);
    }

    @Override
    public boolean isExpired() {
        return this.expiration.isBefore(Instant.now());
    }
}
