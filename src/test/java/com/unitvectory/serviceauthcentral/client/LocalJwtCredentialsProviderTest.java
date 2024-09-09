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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unitvectory.serviceauthcentral.client.LocalJwtCredentialsProvider.LocalJwtCredentialsProviderBuilder;

/**
 * Test class for LocalJwtCredentialsProvider class.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
class LocalJwtCredentialsProviderTest {

    private static String privateKeyPem;

    private static RSAPublicKey publicKey;

    // Before tests
    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException {
        // Generate a fresh RSA key key for testing so we don't have to commit a private
        // key to the repository
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get the private key
        PrivateKey privateKey = keyPair.getPrivate();

        // Save the PEM from the private key
        privateKeyPem = privateKeyToPem(privateKey);

        // Get the public key
        publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    @Test
    void testGetCredentials() {
        // Create an instance of LocalJwtCredentialsProvider
        LocalJwtCredentialsProvider credentialsProvider = LocalJwtCredentialsProvider.builder()
                .clientId("testClientId")
                .issuer("testIssuer")
                .keyId("testKeyId")
                .subject("testSubject")
                .audience("testAudience")
                .privateKeyPem(privateKeyPem)
                .build();

        // Vend the credentials
        SACCredentials credentials = credentialsProvider.getCredentials();
        Assertions.assertNotNull(credentials);

        // Assert is instance of JwtAssertionCredentials
        Assertions.assertTrue(credentials instanceof JwtAssertionCredentials);

        // Cast to JwtAssertionCredentials
        JwtAssertionCredentials jwtAssertionCredentials = (JwtAssertionCredentials) credentials;

        String clientId = jwtAssertionCredentials.getClientId();
        assertEquals("testClientId", clientId);

        String assertion = jwtAssertionCredentials.getJwtAssertion();

        // Use Auth0 JWT library to validate the token signature
        JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null)).build();
        DecodedJWT decodedJwt = verifier.verify(assertion);

        assertEquals("testIssuer", decodedJwt.getIssuer());
        assertEquals("testKeyId", decodedJwt.getKeyId());
        assertEquals("testSubject", decodedJwt.getSubject());
        assertEquals(1, decodedJwt.getAudience().size());
        assertEquals("testAudience", decodedJwt.getAudience().get(0));

    }

    @Test
    void loadGCPServiceAccountFileTest(@TempDir Path tempDir) throws IOException {
        // Use GSON to make example JSON file from a map
        Map<String, String> fileContent = new HashMap<>();
        fileContent.put("client_email", "example@example.com");
        fileContent.put("private_key_id", "123");
        fileContent.put("private_key", privateKeyPem);

        // Encode map as JSON using GSON
        String json = DefaultGson.getInstance().toJson(fileContent);

        // Write the JSON file
        File file = tempDir.resolve(UUID.randomUUID().toString() + ".json").toFile();

        // Write string to the file
        Files.write(file.toPath(), json.getBytes());

        // Load in the configuration
        LocalJwtCredentialsProviderBuilder builder = LocalJwtCredentialsProvider.loadGCPServiceAccountFile(file);
        builder.clientId("testClientId");
        builder.audience("testAudience");
        LocalJwtCredentialsProvider provider = builder.build();

        // Vend the credentials
        SACCredentials credentials = provider.getCredentials();
        assertNotNull(credentials);

        // Cast to JwtAssertionCredentials
        JwtAssertionCredentials jwtAssertionCredentials = (JwtAssertionCredentials) credentials;

        String clientId = jwtAssertionCredentials.getClientId();
        assertEquals("testClientId", clientId);

        String assertion = jwtAssertionCredentials.getJwtAssertion();

        // Use Auth0 JWT library to validate the token signature
        JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null)).build();
        DecodedJWT decodedJwt = verifier.verify(assertion);

        assertEquals("example@example.com", decodedJwt.getIssuer());
        assertEquals("123", decodedJwt.getKeyId());
        assertEquals("example@example.com", decodedJwt.getSubject());
        assertEquals(1, decodedJwt.getAudience().size());
        assertEquals("testAudience", decodedJwt.getAudience().get(0));

    }

    private static String privateKeyToPem(PrivateKey privateKey) {
        Base64.Encoder encoder = Base64.getMimeEncoder(64, new byte[] { '\n' });
        String encodedKey = encoder.encodeToString(privateKey.getEncoded());

        String pemKey = "-----BEGIN PRIVATE KEY-----\n" +
                encodedKey +
                "\n-----END PRIVATE KEY-----";
        return pemKey;
    }
}
