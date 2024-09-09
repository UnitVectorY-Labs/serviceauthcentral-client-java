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

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Test class for SACClientException class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class SACClientExceptionTest {

    @Test
    void testSACClientException() {
        JsonObject json = new JsonObject();
        json.addProperty("error", "Test Error");
        json.addProperty("status", 500);
        JsonArray messages = new JsonArray();
        messages.add("Test Message");
        json.add("messages", messages);

        SACClientException exception = new SACClientException(json);

        assertEquals("Test Error", exception.getError());
        assertEquals(500, exception.getStatus());
        assertEquals(1, exception.getMessages().size());
        assertEquals("Test Message", exception.getMessages().get(0));
    }
}
