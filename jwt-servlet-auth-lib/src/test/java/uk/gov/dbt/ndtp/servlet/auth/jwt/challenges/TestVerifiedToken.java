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
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtHttpConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.HeaderSource;

public class TestVerifiedToken {

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = ".* cannot be null")
    public void givenNullCandidate_whenConstructingVerifiedToken_thenError() {
        // Given
        TokenCandidate candidate = null;

        // When and Then
        new VerifiedToken(candidate, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void givenNullVerified_whenConstructingVerifiedToken_thenError() {
        // Given
        TokenCandidate candidate = Mockito.mock(TokenCandidate.class);
        Jws<Claims> jws = null;

        // When and Then
        new VerifiedToken(candidate, jws);
    }

    @Test
    public void givenCandidateAndVerified_whenConstructingVerifiedToken_thenSuccess_andToStringIsCorrect() {
        // Given
        TokenCandidate candidate = new TokenCandidate(new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER), "Bearer foo");
        Jws<Claims> jws = Mockito.mock(Jws.class);

        // When
        VerifiedToken token =  new VerifiedToken(candidate, jws);

        // Then
        Assert.assertNotNull(token.candidateToken());
        Assert.assertNotNull(token.verifiedToken());

        // And
        String value = token.toString();
        Assert.assertTrue(StringUtils.contains(value, VerifiedToken.class.getSimpleName()));
        Assert.assertTrue(StringUtils.contains(value, candidate.toString()));
    }
}
