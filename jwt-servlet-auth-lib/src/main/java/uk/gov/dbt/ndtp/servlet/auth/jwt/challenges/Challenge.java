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

package uk.gov.dbt.ndtp.servlet.auth.jwt.challenges;

import java.util.Objects;

/**
 * Represents a pending authentication challenge to be issued
 */
public record Challenge(int statusCode, String errorCode, String errorDescription) {

    /**
     * Creates a new challenge
     *
     * @param statusCode       Status Code
     * @param errorCode        Error Code
     * @param errorDescription Error description
     */
    public Challenge(int statusCode, String errorCode, String errorDescription) {
        this.statusCode = statusCode;
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode cannot be null");
        this.errorDescription = Objects.requireNonNull(errorDescription, "errorDescription cannot be null");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Challenge{statusCode=")
               .append(statusCode)
               .append(", errorCode=")
               .append(errorCode)
               .append(", errorDescription=")
               .append(errorDescription)
               .append("}");
        return builder.toString();
    }
}
