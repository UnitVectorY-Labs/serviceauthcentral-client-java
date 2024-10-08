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

/**
 * The CredentialsProvider interface provides a base interface for accessing
 * the credentials for authenticating to ServiceAuthCentral.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface CredentialsProvider {

    /**
     * Get the credentials for authenticating to ServiceAuthCentral.
     * 
     * @return The credentials.
     */
    SACCredentials getCredentials();
}
