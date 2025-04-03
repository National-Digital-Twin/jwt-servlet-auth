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

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;

public class TestEngineFactory extends FactoryAbstract {

    @Test
    public void givenNoConfig_whenConfiguringEngine_thenNothingIsConfigured() {
        // Given
        AtomicReference<JwtAuthenticationEngine<?, ?>> configured = new AtomicReference<>();

        // When
        EngineFactory.configure(NULL_PARAM_SUPPLIER, configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test
    public void givenCustomHeaderConfig_whenConfiguringEngine_thenEngineIsConfigured() {
        // Given
        AtomicReference<JwtAuthenticationEngine<?, ?>> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                                            ConfigurationParameters.PARAM_HEADER_NAMES,
                                            "X-Auth-Token,X-Token,X-ApiKey",
                                            ConfigurationParameters.PARAM_HEADER_PREFIXES, "Bearer,Bearer");

        // When
        EngineFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured);
    }

    @Test
    public void givenCustomHeaderConfigWithoutPrefixes_whenConfiguringEngine_thenEngineIsConfigured() {
        // Given
        AtomicReference<JwtAuthenticationEngine<?, ?>> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                                            ConfigurationParameters.PARAM_HEADER_NAMES,
                                            "X-Auth-Token,X-Token,X-ApiKey",
                                            ConfigurationParameters.PARAM_REALM, "Secret Squirrel HQ");

        // When
        EngineFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured);
    }

    @Test
    public void givenEngineFactory_whenQueryingAvailable_thenNonZeroValueIsReturned() {
        // Given and When
        int available = EngineFactory.available();

        // Then
        Assert.assertNotEquals(available, 0);
    }
}
