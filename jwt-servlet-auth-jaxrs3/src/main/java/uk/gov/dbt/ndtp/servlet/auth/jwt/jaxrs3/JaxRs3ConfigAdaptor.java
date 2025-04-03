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

package uk.gov.dbt.ndtp.servlet.auth.jwt.jaxrs3;

import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import jakarta.servlet.ServletContext;

import java.util.Objects;

/**
 * A JAX-RS 3 configuration adaptor
 */
public class JaxRs3ConfigAdaptor implements RuntimeConfigurationAdaptor {

    private final ServletContext context;

    /**
     * Creates a new configuration adaptor
     *
     * @param context Servlet Context
     */
    public JaxRs3ConfigAdaptor(ServletContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public String getParameter(String param) {
        return this.context.getInitParameter(param);
    }

    @Override
    public void setAttribute(String attribute, Object value) {
        this.context.setAttribute(attribute, value);
    }

    @Override
    public Object getAttribute(String attribute) {
        return this.context.getAttribute(attribute);
    }
}
