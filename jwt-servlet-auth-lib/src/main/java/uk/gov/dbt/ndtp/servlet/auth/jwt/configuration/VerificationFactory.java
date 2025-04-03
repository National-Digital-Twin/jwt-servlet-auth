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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

/**
 * A factory for creating {@link JwtVerifier} from configuration
 */
public class VerificationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationFactory.class);

    private static final List<VerificationProvider> PROVIDERS = new ArrayList<>();

    static {
        try {
            ServiceLoader<VerificationProvider> loader = ServiceLoader.load(
                VerificationProvider.class);
            Iterator<VerificationProvider> iter = loader.iterator();
            while (iter.hasNext()) {
                PROVIDERS.add(iter.next());
            }
        } catch (ServiceConfigurationError e) {
            LOGGER.warn("Failed to load a VerificationProvider: ", e);
        } finally {
            ConfigurationProvider.sort(PROVIDERS);
        }
    }

    private VerificationFactory() {
    }

    /**
     * Configures a JWT Verifier in a runtime independent way
     *
     * @param paramSupplier    Supplier function where configuration parameters can be obtained from
     * @param verifierConsumer Consumer function that takes the configured verifier
     */
    public static void configure(UnaryOperator<String> paramSupplier, Consumer<JwtVerifier> verifierConsumer) {
        for (VerificationProvider provider : PROVIDERS) {
            if (provider.configure(paramSupplier, verifierConsumer)) {
                return;
            }
        }
        LOGGER.warn("Failed to configure any JWT verifier from the available providers");
    }

}
