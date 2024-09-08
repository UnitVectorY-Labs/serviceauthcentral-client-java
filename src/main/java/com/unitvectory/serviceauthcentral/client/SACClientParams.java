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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * The SACClientParams class provides the parameters for the SACClient.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Getter(AccessLevel.PACKAGE)
@Builder
public class SACClientParams {

    /**
     * The issuer
     */
    String issuer;

    /**
     * The complete url for the token endpoint.
     * 
     * If this is not provided the issuer is used suffixed with "/v1/token"
     */
    String tokenEndpoint;

    /**
     * The jwt-bearer provider
     */
    SACJwtBearerProvider jwtBearerProvider;

    /**
     * The client credentials provider
     */
    SACClientCredentialsProvider clientCredentialsProvider;

    /**
     * The user agent to use when making requests.
     * 
     * This can be customized to identify the client making the request.
     * 
     * The default value if not specified will be "serviceauthcentral-client-java"
     */
    @Builder.Default
    String userAgent = "serviceauthcentral-client-java";
}
