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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

/**
 * The UrlFormEncoder class provides the means to encode parameters as x-www-form-urlencoded format.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@UtilityClass
class UrlFormEncoder {
    /**
     * Encodes the parameters as x-www-form-urlencoded format.
     * 
     * @param params A map of parameters to be encoded.
     * @return A URL-encoded string in application/x-www-form-urlencoded format.
     */
    static String encodeFormParams(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> encodeParam(entry.getKey()) + "=" + encodeParam(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * Encodes a single URL parameter.
     * 
     * @param param The parameter to be encoded.
     * @return The encoded parameter.
     */
    private static String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
}
