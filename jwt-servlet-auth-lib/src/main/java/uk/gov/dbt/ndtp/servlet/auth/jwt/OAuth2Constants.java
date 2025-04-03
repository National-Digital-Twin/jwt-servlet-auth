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

/**
 * OAuth2 related constants
 */
public class OAuth2Constants {

    private OAuth2Constants() {
    }

    /**
     * Error code for invalid tokens
     */
    public static final String ERROR_INVALID_TOKEN = "invalid_token";
    /**
     * Error code for invalid requests
     */
    public static final String ERROR_INVALID_REQUEST = "invalid_request";

    /**
     * Challenge parameter used in HTTP Bearer Auth challenges to indicate a particular error state per <a
     * href="https://datatracker.ietf.org/doc/html/rfc6750">RFC 6750</a>
     */
    public static final String CHALLENGE_PARAMETER_ERROR = "error";
    /**
     * Challenge parameter used in HTTP Bearer Auth challenges to provide a detailed error description per <a
     * href="https://datatracker.ietf.org/doc/html/rfc6750">RFC 6750</a>
     */
    public static final String CHALLENGE_PARAMETER_ERROR_DESCRIPTION = "error_description";
}
