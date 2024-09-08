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

import lombok.Builder;
import lombok.NonNull;

/**
 * The CachingCredentialsDecorator class provides a way to cache credentials from a credentials provider.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class CachingCredentialsDecorator implements CredentialsProvider {

    /**
     * The credentials provider we are decorating with caching.
     */
    private final CredentialsProvider provider;

    /**
     * The cached credentials.
     */
    private SACCredentials cachedCredentials;

    /**
     * Create a new caching credentials decorator.
     * 
     * @param provider the credentials provider to decorate.
     */
    @Builder
    private CachingCredentialsDecorator(@NonNull CredentialsProvider provider) {
        this.provider = provider;
        this.cachedCredentials = null;
    }

    @Override
    public SACCredentials getCredentials() {
        // Synchronize to prevent multiple threads from getting the credentials
        synchronized (this) {

            // Clear the expired credentials
            if (this.cachedCredentials != null && this.cachedCredentials.isExpired()) {
                this.cachedCredentials = null;
            }

            // Get the credentials if they are not cached
            if (this.cachedCredentials == null) {
                this.cachedCredentials = this.provider.getCredentials();
            }

            return this.cachedCredentials;
        }
    }

}
