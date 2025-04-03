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

package uk.gov.dbt.ndtp.servlet.auth.jwt.servlet3.examples;

import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import uk.gov.dbt.ndtp.servlet.auth.jwt.testing.AbstractServer;
import org.eclipse.jetty.server.Server;

public class JettyServlet3Server extends AbstractServer {

    private final Server server;
    private final int port;

    public JettyServlet3Server(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            this.server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            this.server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBaseUrl() {
        return String.format("http://localhost:%d", this.port);
    }

    @Override
    public RuntimeConfigurationAdaptor getRuntimeConfiguration() {
        return new RuntimeConfigurationAdaptor() {
            @Override
            public String getParameter(String param) {
                return null;
            }

            @Override
            public void setAttribute(String attribute, Object value) {
                server.setAttribute(attribute, value);
            }

            @Override
            public Object getAttribute(String attribute) {
                return server.getAttribute(attribute);
            }
        };
    }
}
