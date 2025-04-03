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

package uk.gov.dbt.ndtp.servlet.auth.jwt.configuration;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

/**
 * A provider of {@link JwtVerifier} automatic configuration that will be {@link java.util.ServiceLoader} discovered via
 * {@link VerificationFactory}
 */
public interface VerificationProvider extends ConfigurationProvider {

    /**
     * Attempts to configure a {@link JwtVerifier}
     *
     * @param paramSupplier    Parameter supplier
     * @param verifierConsumer Verifier consumer
     * @return Indicates whether configuration occurred.  If {@code true} then the provider had enough configuration to
     * do its job and a verifier was configured, if {@code false} there was insufficient/bad configuration.
     */
    boolean configure(UnaryOperator<String> paramSupplier, Consumer<JwtVerifier> verifierConsumer);
}
