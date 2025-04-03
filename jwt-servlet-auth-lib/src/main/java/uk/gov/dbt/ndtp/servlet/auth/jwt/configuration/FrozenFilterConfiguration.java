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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.PathExclusion;
import uk.gov.dbt.ndtp.servlet.auth.jwt.errors.AuthenticationConfigurationError;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

/**
 * Holds configuration for a filter, allowing it to be set only once, and warning if an attempt to change the
 * configuration after the fact is detected
 *
 * @param <TRequest>  Request type
 * @param <TResponse> Response type
 */
public final class FrozenFilterConfiguration<TRequest, TResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrozenFilterConfiguration.class);

    // These fields are intentionally null, they will be populated once configure() has been called and tried to
    // autoconfigure these, or the first time they are successfully read from the runtime configuration if the user is
    // doing the configuration another way e.g. via a ServletContextListener
    private JwtAuthenticationEngine<TRequest, TResponse> engine = null;
    private JwtVerifier verifier = null;
    private List<PathExclusion> exclusions = null;

    /**
     * Gets the configured engine (if any)
     *
     * @return Engine
     */
    public JwtAuthenticationEngine<TRequest, TResponse> getEngine() {
        return engine;
    }

    /**
     * Gets the configured verifier (if any)
     *
     * @return Verifier
     */
    public JwtVerifier getVerifier() {
        return verifier;
    }

    /**
     * Gets the configured path exclusions (if any)
     *
     * @return Exclusions
     */
    public List<PathExclusion> getExclusions() {
        return exclusions;
    }

    /**
     * Checks whether the frozen configuration has changed relative to the runtime provided configuration
     *
     * @param attribute       Attribute
     * @param attributeGetter Attribute getter function used to detect the currently configured value
     * @param frozenConfig    Previously frozen configuration
     */
    public void warnIfModificationAttempted(String attribute,
                                            Function<String, Object> attributeGetter,
                                            Object frozenConfig) {
        // If no frozen/server config can't make this check
        if (frozenConfig == null) {
            return;
        }
        Object serverConfig = attributeGetter.apply(attribute);
        if (serverConfig == null) {
            return;
        }
        // NB - Intentionally checking for reference inequality here as the initial configuration would have been placed
        //      into the attribute so only if someone changed the attribute should the reference be different
        if (frozenConfig != serverConfig) {
            LOGGER.warn(
                    "An attempt was made to modify JWT filter configuration (via attribute {}) after the filter has been initialised, modified configuration is ignored",
                    attribute);
        }
    }

    /**
     * Prepares the JWT Engine
     *
     * @param rawEngine     Raw engine, typically obtained from a servlet context attribute
     * @param defaultEngine The default engine to use if a specific one is not configured
     * @return JWT Engine
     * @throws RuntimeException Thrown if the provided raw engine is of the wrong type, or no configured engine and no
     *                          default engine provided
     */
    private JwtAuthenticationEngine<TRequest, TResponse> prepareEngine(Object rawEngine,
                                                                       JwtAuthenticationEngine<TRequest, TResponse> defaultEngine) {
        JwtAuthenticationEngine<TRequest, TResponse> jwtAuthenticationEngine;
        if (rawEngine == null) {
            jwtAuthenticationEngine = defaultEngine;
        } else if (rawEngine instanceof JwtAuthenticationEngine) {
            jwtAuthenticationEngine = (JwtAuthenticationEngine<TRequest, TResponse>) rawEngine;
        } else {
            throw new AuthenticationConfigurationError(
                    "JwtAuthFilter not properly configured, servlet context provides an engine of the wrong type " + rawEngine.getClass()
                                                                                                                              .getCanonicalName());
        }

        if (jwtAuthenticationEngine == null) {
            throw new AuthenticationConfigurationError(
                    "JwtAuthFilter not properly configured, no authentication engine available");
        }

        return jwtAuthenticationEngine;
    }

    /**
     * Prepares the JWT verifier
     *
     * @param rawVerifier The raw verifier, typically obtained from a servlet context attribute
     * @return JWT Verifier
     * @throws RuntimeException Thrown if no verifier is provided, or it's of the wrong type
     */
    private JwtVerifier prepareVerifier(Object rawVerifier) {
        JwtVerifier jwtVerifier = null;
        if (rawVerifier != null) {
            if (rawVerifier instanceof JwtVerifier) {
                jwtVerifier = (JwtVerifier) rawVerifier;
            } else {
                throw new AuthenticationConfigurationError(
                        "JwtAuthFilter not properly configured, servlet context provides a JWT verifier of the wrong type " + rawVerifier.getClass()
                                                                                                                                         .getCanonicalName());
            }
        }
        if (jwtVerifier == null) {
            throw new AuthenticationConfigurationError(
                    "JwtAuthFilter not properly configured, servlet context does not provide a JWT verifier");
        }

        return jwtVerifier;
    }

    /**
     * Builds a list of paths to filter out.
     *
     * @param rawPathExclusions The list of exclusions.
     * @return A casting of the object to a list of exclusions.
     */
    private List<PathExclusion> preparePathExclusions(Object rawPathExclusions) {
        if (rawPathExclusions == null) {
            return Collections.emptyList();
        }
        if (rawPathExclusions instanceof List<?>) {
            return (List<PathExclusion>) rawPathExclusions;
        } else {
            throw new AuthenticationConfigurationError(
                    "JwtAuthFilter not properly configured, servlet context provides path exclusions of the wrong type " + rawPathExclusions.getClass()
                                                                                                                                            .getCanonicalName());
        }
    }

    /**
     * Tries to freeze the verifier configuration
     *
     * @param rawVerifier Raw verifier
     */
    public synchronized void tryFreezeVerifierConfiguration(Object rawVerifier) {
        if (this.verifier != null) {
            return;
        }
        this.verifier = this.prepareVerifier(rawVerifier);
    }

    /**
     * Tries to freeze the engine configuration
     *
     * @param rawEngine     Engine
     * @param defaultEngine Default engine to fallback configuration to
     */
    public synchronized void tryFreezeEngineConfiguration(Object rawEngine,
                                                          JwtAuthenticationEngine<TRequest, TResponse> defaultEngine) {
        if (this.engine != null) {
            return;
        }
        this.engine = this.prepareEngine(rawEngine, defaultEngine);
    }

    /**
     * Tries to freeze the path exclusions configuration
     *
     * @param rawPathExclusions Path exclusions
     */
    public synchronized void tryFreezeExclusionsConfiguration(Object rawPathExclusions) {
        if (this.exclusions != null) {
            return;
        }
        this.exclusions = this.preparePathExclusions(rawPathExclusions);
    }
}
