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

/**
 * Provides the container for the credentials when authenticating to
 * ServiceAuthCentral.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public sealed interface SACCredentials permits ClientCredentials, JwtAssertionCredentials {

    /**
     * Checks if the credentials are expired.
     * 
     * @return True if the credentials are expired.
     */
    boolean isExpired();

    /**
     * The credentials as a map used for the OAuth request.
     * 
     * @return The credentials as a map.
     */
    Map<String, String> credentialsMap();
}