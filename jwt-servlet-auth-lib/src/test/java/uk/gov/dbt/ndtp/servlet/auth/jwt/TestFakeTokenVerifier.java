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

package uk.gov.dbt.ndtp.servlet.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import java.time.temporal.ChronoUnit;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.FakeTokenVerifier;

public class TestFakeTokenVerifier {

    @Test
    public void fake_token_verifier_01() {
        FakeTokenVerifier verifier = new FakeTokenVerifier();
        Jws<Claims> jws = verifier.verify("test");
        Assert.assertEquals(jws.getPayload().getSubject(), "test");
    }

    @Test
    public void fake_token_verifier_02() {
        FakeTokenVerifier verifier = new FakeTokenVerifier();
        Jws<Claims> jws = verifier.verify("foo");
        Assert.assertEquals(jws.getPayload().getSubject(), "foo");
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void fake_token_verifier_expired_01() {
        FakeTokenVerifier verifier = new FakeTokenVerifier(-1, ChronoUnit.MINUTES);
        verifier.verify("test");
    }
}
