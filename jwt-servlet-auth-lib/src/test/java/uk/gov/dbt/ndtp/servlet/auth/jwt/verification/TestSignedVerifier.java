// SPDX-License-Identifier: Apache-2.0
// Originally developed by Telicent Ltd.; subsequently adapted, enhanced, and maintained by the National Digital Twin Programme.
/*
 *  Copyright (c) Telicent Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/*
 *  Modifications made by the National Digital Twin Programme (NDTP)
 *  © Crown Copyright 2025. This work has been developed by the National Digital Twin Programme
 *  and is legally attributed to the Department for Business and Trade (UK) as the governing entity.
 */

package uk.gov.dbt.ndtp.servlet.auth.jwt.verification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSignedVerifier {

    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final SecretKey anotherKey = Jwts.SIG.HS256.key().build();

    @Test(expectedExceptions = NullPointerException.class)
    public void givenNullParser_whenCreatingSignedVerifier_thenNullPointerException() {
        new SignedJwtVerifier((JwtParser) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenNullPublicKey_whenCreatingSignedVerifier_thenIllegalArgumentException() {
        new SignedJwtVerifier((PublicKey) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenNullSecretKey_whenCreatingSignedVerifier_thenIllegalArgumentException() {
        new SignedJwtVerifier((SecretKey) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenNullLocator_whenCreatingSignedVerifier_thenIllegalArgumentException() {
        new SignedJwtVerifier((Locator<Key>) null);
    }

    private Jws<Claims> verify(JwtParser parser, String jwt) {
        SignedJwtVerifier verifier = new SignedJwtVerifier(parser);
        return verify(verifier, jwt);
    }

    private Jws<Claims> verify(SignedJwtVerifier verifier, String jwt) {
        Jws<Claims> jws = verifier.verify(jwt);
        Assert.assertNotNull(jws);
        return jws;
    }

    private Locator<Key> mockKeyResolver(Key key) {
        Locator<Key> resolver = mock(Locator.class);
        when(resolver.locate(any())).thenReturn(key);
        return resolver;
    }

    @Test
    public void givenSecretKey_whenVerifyingSignedMinimalJwt_thenSuccess() {
        // Given
        JwtParser parser = Jwts.parser().verifyWith(this.key).build();
        String jwt = Jwts.builder().subject("test").signWith(this.key).compact();

        // When and Then
        verify(parser, jwt);
    }

    @Test
    public void givenSecretKey_whenVerifyingSignedComplexJwt_thenSuccess() {
        // Given
        JwtParser parser = Jwts.parser().verifyWith(this.key).build();
        String jwt = Jwts.builder()
                         .subject("test")
                         .issuer("test")
                         .audience()
                         .add("apis")
                         .and()
                         .header()
                         .add("foo", "bar")
                         .and()
                         .expiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                         .signWith(this.key)
                         .compact();

        // When and Then
        verify(parser, jwt);
    }

    @Test
    public void givenSecretKey_whenVerifyingSignedMinimalJwtWithOnlyKey_thenSuccess() {
        // Given
        String jwt = Jwts.builder().subject("test").signWith(this.key).compact();

        // When and Then
        verify(new SignedJwtVerifier(this.key), jwt);
    }

    @Test
    public void givenKeyResolver_whenVerifyingSignedMinimalJwt_thenSuccess() {
        // Given
        String jwt = Jwts.builder().subject("test").signWith(this.key).compact();
        Locator<Key> resolver = mockKeyResolver(this.key);

        // When and Then
        verify(new SignedJwtVerifier(resolver), jwt);
    }

    @Test(expectedExceptions = SignatureException.class)
    public void givenWrongKey_whenVerifyingSignedMinimalJwt_thenSignatureException() {
        // Given
        String jwt = Jwts.builder().subject("test").signWith(this.anotherKey).compact();
        Locator<Key> resolver = mockKeyResolver(this.key);

        // When and Then
        verify(new SignedJwtVerifier(resolver), jwt);
    }

    @Test(expectedExceptions = SignatureException.class)
    public void givenWrongKeyResolver_whenVerifyingSignedMinimalJwtWithOnlyKey_thenSignatureException() {
        // Given
        String jwt = Jwts.builder().subject("test").signWith(this.key).compact();
        Locator<Key> resolver = mockKeyResolver(this.anotherKey);

        // When and Then
        verify(new SignedJwtVerifier(resolver), jwt);
    }

    @Test(expectedExceptions = SignatureException.class)
    public void givenWrongKey_whenVerifyingSignedJwt_thenSignatureException() {
        // Given
        String jwt = Jwts.builder().subject("test").signWith(this.key).compact();
        Locator<Key> resolver = mockKeyResolver(Jwts.SIG.HS256.key().build());

        // When and Then
        verify(new SignedJwtVerifier(resolver), jwt);
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void givenExpiredJwt_whenVerifying_thenError() {
        // Given
        JwtParser parser = Jwts.parser().verifyWith(this.key).build();
        String jwt = Jwts.builder()
                         .subject("test")
                         .expiration(Date.from(Instant.now().minus(5, ChronoUnit.MINUTES)))
                         .signWith(this.key)
                         .compact();

        // When and Then
        verify(parser, jwt);
    }

    @Test
    public void givenExpiredJwt_whenVerifyingWithAllowedClockSkew_thenSuccess() {
        // Given
        JwtParser parser = Jwts.parser().verifyWith(this.key).clockSkewSeconds(10).build();
        String jwt = Jwts.builder()
                         .subject("test")
                         .expiration(Date.from(Instant.now().minus(5, ChronoUnit.SECONDS)))
                         .signWith(this.key)
                         .compact();

        // When and Then
        // Token is expired but within the clock skew allowance so should pass verification
        verify(parser, jwt);
    }

    @Test
    public void givenPublicKey_whenCreatingSignedVerifier_thenToStringContainsKeyFingerprint() {
        // Given
        PublicKey publicKey = Jwts.SIG.ES512.keyPair().build().getPublic();
        String expectedFingerprint = Jwks.builder().key(publicKey).build().thumbprint().toString();

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(publicKey);

        // Then
        Assert.assertTrue(StringUtils.contains(verifier.toString(), "verificationMethod=PublicKey"));
        Assert.assertTrue(StringUtils.contains(verifier.toString(), expectedFingerprint));
    }

    @Test
    public void givenSecretKey_whenCreatingSignedVerifier_thenToStringIndicatesSecretKeyMode() {
        // Given
        SecretKey secretKey = Jwts.SIG.HS256.key().build();

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(secretKey);

        // Then
        Assert.assertTrue(StringUtils.contains(verifier.toString(), "verificationMethod=SecretKey"));
    }

    @Test
    public void givenKeyResolver_whenCreatingSignedVerifier_thenToStringContainsLocatorMode() {
        // Given
        Locator<Key> locator = mockKeyResolver(this.key);

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(locator);

        // Then
        Assert.assertTrue(StringUtils.contains(verifier.toString(), "verificationMethod=Locator"));
    }

    @Test
    public void givenCustomParser_whenCreatingSignedVerified_thenToStringContainsCustomParserMode() {
        // Given
        JwtParser parser = Mockito.mock(JwtParser.class);

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(parser);

        // Then
        Assert.assertTrue(StringUtils.contains(verifier.toString(), "verificationMethod=CustomParser"));
    }
}
