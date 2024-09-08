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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * The ServiceAuthCentral token response.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
public class TokenResponse {

    /**
     * The access_token field
     */
    private final String accessToken;

    /**
     * The token_type field
     */
    private final String tokenType;

    /**
     * The expires_in field
     */
    private final long expiresIn;

    /**
     * The created field, package-private
     */
    @Getter(AccessLevel.PACKAGE)
    private final Instant created;

    @Builder
    private TokenResponse(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.created = Instant.now();
    }

    boolean isHalfwayExpired() {
        // If the token is more than halfway expired (minus 30 seconds), consider it
        // expired
        return this.created.plusSeconds(this.expiresIn / 2).minusSeconds(30).isBefore(Instant.now());
    }

    boolean isExpired() {
        // Treating as expired 30 seconds before it actually expires
        return this.created.plusSeconds(this.expiresIn).minusSeconds(30).isBefore(Instant.now());
    }
}
