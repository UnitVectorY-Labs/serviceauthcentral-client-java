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

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The LocalJwtCredentialsProvider class provides credentials in the form of a
 * private key.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class LocalJwtCredentialsProvider implements CredentialsProvider {

    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";

    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    private static final int DEFAULT_JWT_EXPIRATION = 3600;

    private final String clientId;

    private final String issuer;

    private final String keyId;

    private final String subject;

    private final String audience;

    private final int expiresIn;

    private final Algorithm algorithm;

    private final PrivateKey privateKey;

    @Builder
    private LocalJwtCredentialsProvider(
            @NonNull String clientId,
            @NonNull String issuer,
            @NonNull String keyId,
            @NonNull String subject,
            @NonNull String audience,
            Integer expiresIn,
            Algorithm algorithm,
            @NonNull String privateKeyPem) {
        this.clientId = clientId;
        this.issuer = issuer;
        this.keyId = keyId;
        this.subject = subject;
        this.audience = audience;

        if (expiresIn == null) {
            this.expiresIn = DEFAULT_JWT_EXPIRATION;
        } else {
            this.expiresIn = expiresIn;
        }

        if (algorithm == null) {
            this.algorithm = Algorithm.RS256;
        } else {
            this.algorithm = algorithm;
        }

        try {
            String privateKeyPEM = privateKeyPem.replace(BEGIN_PRIVATE_KEY, "")
                    .replace(END_PRIVATE_KEY, "").replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            this.privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new SACException("Failed to create Service Account with private key", e);
        }
    }

    @Override
    public SACCredentials getCredentials() {

        Instant currentTime = Instant.now();
        long currentTimeMillis = currentTime.toEpochMilli();

        Map<String, Object> headerMap = new TreeMap<>();
        headerMap.put("alg", algorithm.name());
        headerMap.put("typ", "JWT");
        headerMap.put("kid", this.keyId);
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(DefaultGson.getInstance().toJson(headerMap).getBytes());

        Map<String, Object> payloadMap = new TreeMap<>();
        payloadMap.put("iss", this.issuer);
        payloadMap.put("sub", this.subject);
        payloadMap.put("aud", this.audience);
        payloadMap.put("iat", currentTimeMillis / 1000);
        payloadMap.put("exp", (currentTimeMillis + this.expiresIn * 1000) / 1000);
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(DefaultGson.getInstance().toJson(payloadMap).getBytes());

        String signatureInput = header + "." + payload;
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signWithPrivateKey(signatureInput.getBytes()));

        String token = signatureInput + "." + signature;

        return JwtAssertionCredentials.builder().clientId(this.clientId).jwtAssertion(token)
                .expiration(currentTime.plusSeconds(this.expiresIn).minusSeconds(30)).build();
    }

    private byte[] signWithPrivateKey(byte[] data) {
        try {
            Signature signature = Signature.getInstance(this.algorithm.getSignature());
            signature.initSign(privateKey);
            signature.update(data);

            return signature.sign();
        } catch (Exception e) {
            throw new SACException("Failed to sign Service Account with private key", e);
        }
    }

    @Getter
    @AllArgsConstructor
    public static enum Algorithm {

        RS256("SHA256withRSA"),

        ;

        private String signature;
    }
}
