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
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * A fake token verifier that treats all tokens as valid <strong>BUT</strong> returns a JWT that contains the username
 * is a different claim than the default.  The default claim contains a random UUID to verify that the engine really is
 * consulting the correct claim.
 */
public class AlternateClaimTokenVerifier extends FakeTokenVerifier {

    private final String usernameClaim;

    /**
     * Creates a new fake verifier
     *
     * @param usernameClaim Username claim into which the username will be inserted
     */
    public AlternateClaimTokenVerifier(String usernameClaim) {
        this.usernameClaim = usernameClaim;
    }

    @Override
    public Jws<Claims> verify(String rawJwt) {
        String generatedJws = Jwts.builder()
                                  .subject(UUID.randomUUID().toString())
                                  .claims()
                                  .add(Map.of(this.usernameClaim, rawJwt))
                                  .and()
                                  .issuer("test")
                                  .expiration(Date.from(Instant.now().plus(this.expiresIn, this.unit)))
                                  .signWith(this.key)
                                  .compact();
        return this.parser.parseSignedClaims(generatedJws);
    }
}
