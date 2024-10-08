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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Test class for SACTokenRequest class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class TokenRequestTest {

    @Test
    void testSACTokenRequestBuilder() {
        String audience = "test-audience";
        Set<String> scopes = Set.of("scope1", "scope2");

        TokenRequest tokenRequest = TokenRequest.builder()
                .audience(audience)
                .scopes(scopes)
                .build();

        assertEquals(audience, tokenRequest.getAudience());
        assertEquals(scopes.size(), tokenRequest.getScopes().size());
        assertTrue(tokenRequest.getScopes().containsAll(scopes));
    }

    @Test
    void testSACTokenRequestBuilderScope() {
        String audience = "test-audience";
        String scope = "read";

        TokenRequest tokenRequest = TokenRequest.builder()
                .audience(audience)
                .scope(scope)
                .build();

        assertEquals(audience, tokenRequest.getAudience());
        assertEquals(1, tokenRequest.getScopes().size());
        assertTrue(tokenRequest.getScopes().contains(scope));
    }
}