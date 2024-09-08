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

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;

/**
 * The CachingSACClientDecorator class provides a way to cache the results of a
 * SACClient.
 * 
 * This uses an in memory map to cache the tokens in memory. This cache is not
 * shared and is not cleared.
 * 
 * The
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class CachingSACClientDecorator implements SACClient {

    /**
     * The SACClient we are decorating with caching.
     */
    private final SACClient client;

    /**
     * The cached tokens.
     */
    private Map<TokenRequest, TokenResponse> tokenCache = new HashMap<>();

    /**
     * Create a new caching SAC client decorator.
     * 
     * @param client the SAC client to decorate.
     */
    @Builder
    private CachingSACClientDecorator(SACClient client) {
        this.client = client;
    }

    /**
     * Clear the cache of tokens.
     */
    public void clearCache() {
        this.tokenCache.clear();
    }

    @Override
    public TokenResponse getToken(TokenRequest request) {

        synchronized (this) {
            TokenResponse cachedResponse = this.tokenCache.get(request);

            if (cachedResponse != null) {
                // Token is cached, we need to decide how to handle it.

                if (!cachedResponse.isHalfwayExpired()) {
                    // Token is valid and not halfway expired, use it.
                    return cachedResponse;
                }

                // Token is in the latter half of its life.
                try {
                    // Try to request a new token
                    TokenResponse newResponse = this.client.getToken(request);

                    // Successfully retrieved a new token, cache and return it.
                    this.tokenCache.put(request, newResponse);
                    return newResponse;

                } catch (Exception e) {
                    // If token request fails, fall back to the non-expired cached token.
                    if (!cachedResponse.isExpired()) {
                        return cachedResponse;
                    }
                    // If it is fully expired, we will request a new token outside this block.
                }
            }

            // Either no cached token or the cached token is expired.
            TokenResponse response = this.client.getToken(request);

            // Cache and return the new token.
            this.tokenCache.put(request, response);

            return response;
        }
    }
}
