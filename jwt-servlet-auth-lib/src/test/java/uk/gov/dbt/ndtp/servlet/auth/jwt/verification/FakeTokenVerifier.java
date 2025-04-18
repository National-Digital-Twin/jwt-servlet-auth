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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * A fake token verifier for testing purposes
 * <p>
 * This treats the token as just being the desired username and generates a fake {@link Jws} instance when verifying the
 * token.
 * </p>
 */
public class FakeTokenVerifier implements JwtVerifier {

    protected final SecretKey key;
    protected final JwtParser parser;
    protected final long expiresIn;
    protected final TemporalUnit unit;

    public FakeTokenVerifier() {
        this(5, ChronoUnit.MINUTES);
    }

    public FakeTokenVerifier(long expiresIn, TemporalUnit unit) {
        this.key = Jwts.SIG.HS256.key().build();
        this.parser = Jwts.parser().verifyWith(this.key).build();
        this.expiresIn = expiresIn;
        this.unit = unit;
    }

    @Override
    public Jws<Claims> verify(String rawJwt) {
        String generatedJws =
                Jwts.builder()
                    .issuer("test")
                    .subject(rawJwt)
                    .expiration(Date.from(Instant.now().plus(this.expiresIn, this.unit)))
                    .signWith(this.key)
                    .compact();
        return this.parser.parseSignedClaims(generatedJws);
    }
}
