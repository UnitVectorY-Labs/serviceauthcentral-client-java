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
 * The SACClient interface provides a way to interact with the
 * ServiceAuthCentral API.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface SACClient {

    /**
     * Get a token from the ServiceAuthCentral
     * 
     * @param request the token request
     * @return the token response
     */
    TokenResponse getToken(TokenRequest request);
}
