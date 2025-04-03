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
import java.util.function.Function;

/**
 * Utilities relating to configuration parsing
 */
public class Utils {

    /**
     * Private constructor prevents direct instantiation
     */
    private Utils() {}

    /**
     * Parses a configuration parameter
     *
     * @param parameters   Map of parameters
     * @param param        Parameter
     * @param parser       Value parser
     * @param defaultValue Default value to use as fallback
     * @param <TRequest>          Value type
     * @return Parsed value
     */
    public static <TRequest> TRequest parseParameter(Map<String, String> parameters, String param, Function<String, TRequest> parser,
                                       TRequest defaultValue) {
        if (parameters.containsKey(param)) {
            return parseParameter(parameters.get(param), parser, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Parses a configuration parameter
     *
     * @param value        Raw parameter value
     * @param parser       Value parser
     * @param defaultValue Default value to use as fallback
     * @param <TRequest>          Value type
     * @return Parsed value
     */
    public static <TRequest> TRequest parseParameter(String value, Function<String, TRequest> parser, TRequest defaultValue) {
        try {
            return parser.apply(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
