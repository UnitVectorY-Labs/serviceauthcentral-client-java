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

import org.junit.jupiter.api.Test;

/**
 * Test class for CachingCredentialsProviderDecorator class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class CachingCredentialsProviderDecoratorTest {

    @Test
    void getCredentialsTest() {
        CredentialsProvider credentialsProvider = new CredentialsProvider() {

            // Counting the number of times this is called used to determine if the response
            // was cached
            private int count = 0;

            @Override
            public SACCredentials getCredentials() {
                count++;
                return ClientCredentials.builder()
                        .clientId("testClientId").clientSecret(count + "")
                        .build();
            }
        };

        CachingCredentialsProviderDecorator cachingProvider = CachingCredentialsProviderDecorator.builder()
                .provider(credentialsProvider)
                .build();

        // First call should call the CredentialsProvider, the access key id will be "1"
        assertEquals("1", ((ClientCredentials) credentialsProvider.getCredentials()).getClientSecret());

        // Now we can get a cached credentials, it will be "2"
        assertEquals("2", ((ClientCredentials) cachingProvider.getCredentials()).getClientSecret());

        // Now we can get a cached credentials, it will be "2"
        assertEquals("2", ((ClientCredentials) cachingProvider.getCredentials()).getClientSecret());

        // If we get new credentials, it will be "3"
        assertEquals("3", ((ClientCredentials) credentialsProvider.getCredentials()).getClientSecret());

        // But the cached credentials will still be "2"
        assertEquals("2", ((ClientCredentials) cachingProvider.getCredentials()).getClientSecret());
    }
}
