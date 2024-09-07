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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import lombok.Builder;
import lombok.Value;

/**
 * The ServiceAuthCentral token request.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public class SACTokenRequest {

    private final String audience;

    private final Set<String> scopes;

    public static class SACTokenRequestBuilder {
        private Set<String> scopes = new HashSet<>();

        public SACTokenRequestBuilder scopes(Set<String> scopes) {
            this.scopes = new HashSet<>(scopes);
            return this;
        }

        public SACTokenRequestBuilder scope(String scope) {
            this.scopes.add(scope);
            return this;
        }

        public SACTokenRequest build() {
            this.scopes = Collections.unmodifiableSet(new TreeSet<String>(this.scopes));
            return new SACTokenRequest(audience, scopes);
        }
    }
}
