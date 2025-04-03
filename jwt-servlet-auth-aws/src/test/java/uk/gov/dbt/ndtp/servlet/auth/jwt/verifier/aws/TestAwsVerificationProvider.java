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

package uk.gov.dbt.ndtp.servlet.auth.jwt.verifier.aws;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.VerificationFactory;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

public class TestAwsVerificationProvider {

    public static UnaryOperator<String> mapSupplier(Map<String, String> map) {
        return map::get;
    }

    @Test
    public void givenNoConfiguration_whenConfiguringVerifier_thenNothingIsConfigured() {
        // Given
        AtomicReference<JwtVerifier> verifier = new AtomicReference<>();

        // When
        VerificationFactory.configure(x -> null, verifier::set);

        // Then
        Assert.assertNull(verifier.get());
    }

    @Test
    public void givenAwsRegion_whenConfiguringVerifier_thenVerifierIsConfigured() {
        // Given
        AtomicReference<JwtVerifier> verifier = new AtomicReference<>();
        Map<String, String> config = Map.of(AwsVerificationProvider.PARAM_AWS_REGION, "eu-west-1");

        // When
        VerificationFactory.configure(mapSupplier(config), verifier::set);

        // Then
        Assert.assertNotNull(verifier.get());
        Assert.assertTrue(verifier.get() instanceof AwsElbJwtVerifier);
    }
}
