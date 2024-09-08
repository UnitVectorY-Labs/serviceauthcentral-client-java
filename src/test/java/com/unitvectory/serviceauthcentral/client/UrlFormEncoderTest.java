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
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class UrlFormEncoderTest {

    @Test
    public void testEncodeFormParams() {
        // Create a map of parameters
        Map<String, String> params = new LinkedHashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        params.put("param3", "value3");

        // Encode the parameters
        String encodedParams = UrlFormEncoder.encodeFormParams(params);

        // Verify the encoded string
        assertEquals("param1=value1&param2=value2&param3=value3", encodedParams);
    }
}
