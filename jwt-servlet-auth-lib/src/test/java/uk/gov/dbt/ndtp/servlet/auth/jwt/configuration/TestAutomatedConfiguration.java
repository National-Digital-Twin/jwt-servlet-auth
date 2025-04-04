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

import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtServletConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.PathExclusion;
import uk.gov.dbt.ndtp.servlet.auth.jwt.fake.FakeEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.SignedJwtVerifier;

public class TestAutomatedConfiguration extends FactoryAbstract {

    private static final String EXAMPLE_JWKS_URL = "https://example.org/jwks.json";

    @Test
    public void givenNoConfig_whenAutomaticallyConfiguring_thenNothingIsConfigured() {
        // Given
        MapRuntimeConfigAdaptor config = new MapRuntimeConfigAdaptor();

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));
    }

    @Test
    public void givenConfig_whenAutomaticallyConfiguring_thenEverythingIsConfigured() {
        // Given
        MapRuntimeConfigAdaptor config = new MapRuntimeConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_JWKS_URL, EXAMPLE_JWKS_URL,
                       ConfigurationParameters.PARAM_PATH_EXCLUSIONS, "/public/*",
                       ConfigurationParameters.PARAM_HEADER_NAMES, "Authorization",
                       ConfigurationParameters.PARAM_USERNAME_CLAIMS, "email"));

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));
    }

    @Test
    public void givenPreExistingConfig_whenAutomaticallyConfiguring_thenReconfigurationIsSkipped() {
        // Given
        MapRuntimeConfigAdaptor config =
                new MapRuntimeConfigAdaptor(Map.of(ConfigurationParameters.PARAM_JWKS_URL, EXAMPLE_JWKS_URL));
        SignedJwtVerifier jwtVerifier =
                new SignedJwtVerifier(Jwts.parser().verifyWith(Jwts.SIG.HS256.key().build()).build());
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/status/*");
        JwtAuthenticationEngine<?, ?> engine = new FakeEngine();
        config.setAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER, jwtVerifier);
        config.setAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS, exclusions);
        config.setAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE, engine);

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertEquals(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER), jwtVerifier);
        Assert.assertEquals(config.getAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS), exclusions);
        Assert.assertEquals(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE), engine);
    }

    @Test
    public void givenPreExistingConfigWithMultipleConfigsAllowed_whenAutomaticallyConfiguring_thenReconfigurationOccurs() {
        // Given
        MapRuntimeConfigAdaptor config =
                new MapRuntimeConfigAdaptor(Map.of(ConfigurationParameters.PARAM_JWKS_URL, EXAMPLE_JWKS_URL,
                                                   ConfigurationParameters.PARAM_ALLOW_MULTIPLE_CONFIGS, "true",
                                                   ConfigurationParameters.PARAM_HEADER_NAMES, "X-API-Key",
                                                   ConfigurationParameters.PARAM_PATH_EXCLUSIONS, "/healthz"));
        SignedJwtVerifier jwtVerifier =
                new SignedJwtVerifier(Jwts.parser().verifyWith(Jwts.SIG.HS256.key().build()).build());
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/status/*");
        JwtAuthenticationEngine<?, ?> engine = new FakeEngine();
        config.setAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER, jwtVerifier);
        config.setAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS, exclusions);
        config.setAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE, engine);

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertNotEquals(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER), jwtVerifier);
        Assert.assertNotEquals(config.getAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS), exclusions);
        Assert.assertNotEquals(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE), engine);
    }
}
