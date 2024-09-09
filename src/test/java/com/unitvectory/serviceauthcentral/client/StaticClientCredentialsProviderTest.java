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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for StaticClientCredentialsProvider class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class StaticClientCredentialsProviderTest {

    @Test
    void testGetCredentials() {
        // Create a StaticClientCredentialsProvider with sample credentials
        StaticClientCredentialsProvider provider = StaticClientCredentialsProvider.builder()
                .clientId("sampleClientId")
                .clientSecret("sampleClientSecret")
                .build();

        // Get the credentials from the provider
        SACCredentials credentials = provider.getCredentials();

        // Assert that the credentials are not null
        Assertions.assertNotNull(credentials);

        // Assert credentials is class type ClientCredentials
        Assertions.assertTrue(credentials instanceof ClientCredentials);

        // Cast to ClientCredentials
        ClientCredentials clientCredentials = (ClientCredentials) credentials;

        // Assert that the credentials have the correct client id and secret
        Assertions.assertEquals("sampleClientId", clientCredentials.getClientId());
        Assertions.assertEquals("sampleClientSecret", clientCredentials.getClientSecret());
    }
}
