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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for ClientCredentials class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class ClientCredentialsTest {

    @Test
    void testCredentialsMap() {
        ClientCredentials credentials = ClientCredentials.builder()
                .clientId("foo")
                .clientSecret("secret")
                .build();

        Map<String, String> map = credentials.credentialsMap();
        assertEquals(3, map.size());
        assertEquals("client_credentials", map.get("grant_type"));
        assertEquals("foo", map.get("client_id"));
        assertEquals("secret", map.get("client_secret"));
    }

    @Test
    void testIsExpired() {
        ClientCredentials credentials = ClientCredentials.builder()
                .clientId("foo")
                .clientSecret("secret")
                .build();

        // This is set to never expire
        assertFalse(credentials.isExpired());
    }
}
