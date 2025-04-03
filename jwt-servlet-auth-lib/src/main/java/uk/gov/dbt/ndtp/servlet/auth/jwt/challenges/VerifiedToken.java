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

package uk.gov.dbt.ndtp.servlet.auth.jwt.challenges;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Objects;

/**
 * A verified JSON Web Token (JWT)
 *
 * @param candidateToken The candidate JWT
 * @param verifiedToken  The parsed and verified JWT
 */
public record VerifiedToken(TokenCandidate candidateToken, Jws<Claims> verifiedToken) {
    /**
     * Creates a new verified token
     *
     * @param candidateToken The candidate token
     * @param verifiedToken  The verified token
     */
    public VerifiedToken(TokenCandidate candidateToken, Jws<Claims> verifiedToken) {
        this.candidateToken = Objects.requireNonNull(candidateToken, "candidateToken cannot be null");
        this.verifiedToken = Objects.requireNonNull(verifiedToken, "verifiedToken cannot be null");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VerifiedToken{").append("candidateToken=")
                                                                    .append(candidateToken)
                                                                    .append(", verifiedToken=")
                                                                    .append(verifiedToken)
                                                                    .append('}');
        return sb.toString();
    }
}
