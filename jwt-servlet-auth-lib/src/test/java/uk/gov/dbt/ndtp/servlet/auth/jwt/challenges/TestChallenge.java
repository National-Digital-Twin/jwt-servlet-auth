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
import uk.gov.dbt.ndtp.servlet.auth.jwt.OAuth2Constants;

public class TestChallenge {

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = ".*cannot be null")
    public void givenNoErrorCode_whenConstructingAChallenge_thenError() {
        // Given, When and Then
        new Challenge(401, null, null);
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = ".*cannot be null")
    public void givenNoErrorDescription_whenConstructingAChallenge_thenError() {
        // Given, When and Then
        new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN, null);
    }

    @Test
    public void givenValidParameters_whenConstructingAChallenge_thenResultAsExpected_andToStringReflectsParameters() {
        // Given and When
        Challenge challenge = new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN, "test");

        // Then
        Assert.assertEquals(challenge.statusCode(), 401);
        Assert.assertEquals(challenge.errorCode(), OAuth2Constants.ERROR_INVALID_TOKEN);
        Assert.assertEquals(challenge.errorDescription(), "test");

        // And
        String value = challenge.toString();
        Assert.assertTrue(StringUtils.contains(value, "401"));
        Assert.assertTrue(StringUtils.contains(value, OAuth2Constants.ERROR_INVALID_TOKEN));
        Assert.assertTrue(StringUtils.contains(value, "test"));
    }
}
