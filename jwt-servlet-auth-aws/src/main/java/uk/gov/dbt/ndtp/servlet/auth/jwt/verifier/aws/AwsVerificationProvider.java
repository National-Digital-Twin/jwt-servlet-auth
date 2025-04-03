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

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.VerificationProvider;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

/**
 * A provider of automated configuration for {@link AwsElbJwtVerifier} verifiers
 */
public class AwsVerificationProvider implements VerificationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsVerificationProvider.class);

    /**
     * Parameter used to configure the AWS region from which public keys should be obtained for JWT verification
     */
    public static final String PARAM_AWS_REGION = "jwt.aws.region";

    @Override
    public boolean configure(UnaryOperator<String> paramSupplier, Consumer<JwtVerifier> verifierConsumer) {
        String region = paramSupplier.apply(PARAM_AWS_REGION);
        if (StringUtils.isNotBlank(region)) {
            AwsElbJwtVerifier jwtVerifier = new AwsElbJwtVerifier(region);
            verifierConsumer.accept(jwtVerifier);
            LOGGER.info("Configured the AWS JWT Verifier: {}", jwtVerifier);
            return true;
        } else {
            LOGGER.info("No relevant parameters to allow AWS verifier configuration.");
        }
        return false;
    }

    @Override
    public int priority() {
        return 1;
    }
}
