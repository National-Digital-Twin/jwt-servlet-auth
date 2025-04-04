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

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtHttpConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.HeaderSource;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.TokenSource;

public class TestTokenCandidate {

    private static final HeaderSource SOURCE =
            new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER);

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = ".* cannot be null")
    public void givenNullSource_whenConstructingCandidate_thenError() {
        // Given
        TokenSource source = null;

        // When and Then
        new TokenCandidate(source, "test");
    }

    @Test
    public void givenSourceAndValue_whenConstructingCandidate_thenSuccess_andToStringIsCorrect() {
        // Given
        TokenSource source = SOURCE;
        String inputValue = JwtHttpConstants.AUTH_SCHEME_BEARER + " test";

        // When
        TokenCandidate candidate = new TokenCandidate(source, inputValue);

        // Then
        Assert.assertEquals(candidate.source(), source);
        Assert.assertEquals(candidate.value(), inputValue);
        Assert.assertEquals(candidate.source().getRawToken(candidate.value()), "test");

        // And
        String toStringValue = candidate.toString();
        Assert.assertTrue(StringUtils.contains(toStringValue, TokenCandidate.class.getSimpleName()));
        Assert.assertTrue(StringUtils.contains(toStringValue, "test"));
        Assert.assertTrue(StringUtils.contains(toStringValue, source.toString()));
    }

    @Test
    public void givenSourceAndNullValue_whenConstructingCandidate_thenNullTokenIsReturned() {
        // Given
        TokenSource source = SOURCE;
        String inputValue = null;

        // When
        TokenCandidate candidate = new TokenCandidate(source, inputValue);
        String rawToken = candidate.source().getRawToken(candidate.value());

        // Then
        Assert.assertEquals(candidate.source(), source);
        Assert.assertNull(candidate.value());
        Assert.assertNull(rawToken);
    }
}
