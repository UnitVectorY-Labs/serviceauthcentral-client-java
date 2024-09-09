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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for JwtAssertionCredentials class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class JwtAssertionCredentialsTest {

    @Test
    void testCredentialsMap() {
        String clientId = "testClientId";
        String jwtAssertion = "testJwtAssertion";
        Instant expiration = Instant.now();
        JwtAssertionCredentials credentials = JwtAssertionCredentials.builder()
                .clientId(clientId)
                .jwtAssertion(jwtAssertion)
                .expiration(expiration)
                .build();

        Map<String, String> credentialsMap = credentials.credentialsMap();

        Assertions.assertEquals("urn:ietf:params:oauth:grant-type:jwt-bearer", credentialsMap.get("grant_type"));
        Assertions.assertEquals(clientId, credentialsMap.get("client_id"));
        Assertions.assertEquals(jwtAssertion, credentialsMap.get("assertion"));
    }

    @Test
    void testIsExpired() {
        Instant expiration = Instant.now().minusSeconds(3600);
        JwtAssertionCredentials expiredCredentials = JwtAssertionCredentials.builder()
                .expiration(expiration)
                .build();

        Assertions.assertTrue(expiredCredentials.isExpired());
    }

    @Test
    void testIsNotExpired() {
        Instant futureExpiration = Instant.now().plusSeconds(3600);
        JwtAssertionCredentials validCredentials = JwtAssertionCredentials.builder()
                .expiration(futureExpiration)
                .build();

        Assertions.assertFalse(validCredentials.isExpired());
    }
}
