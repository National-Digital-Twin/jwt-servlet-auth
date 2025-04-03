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

package uk.gov.dbt.ndtp.servlet.auth.jwt.servlet5.examples;

import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.ConfigurationParameters;
import uk.gov.dbt.ndtp.servlet.auth.jwt.servlet5.JwtAuthFilter;
import uk.gov.dbt.ndtp.servlet.auth.jwt.servlet5.examples.HelloWorldServlet;
import uk.gov.dbt.ndtp.servlet.auth.jwt.testing.AbstractIntegrationTests;
import uk.gov.dbt.ndtp.servlet.auth.jwt.testing.AbstractServer;
import jakarta.servlet.DispatcherType;
import java.io.File;
import java.util.EnumSet;
import org.eclipse.jetty.ee9.servlet.FilterHolder;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;
import org.eclipse.jetty.ee9.servlet.ServletHandler;
import org.eclipse.jetty.ee9.servlet.ServletHolder;
import org.eclipse.jetty.ee9.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.ResourceFactory;

public class TestServlet5Application extends AbstractIntegrationTests {

    protected AbstractServer buildProgrammaticApplication(File keyFile, int port) {
        Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        ServletHandler servletHandler = new ServletHandler();
        handler.setHandler(servletHandler);
        ServletHolder helloWorld = servletHandler.addServletWithMapping(HelloWorldServlet.class, "/hello");
        helloWorld.setInitParameter(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath());
        FilterHolder auth =
                servletHandler.addFilterWithMapping(JwtAuthFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        auth.setInitParameter(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath());
        server.setHandler(handler);
        return new JettyServlet5Server(server, port);
    }

    protected AbstractServer buildWebXmlApplication(int port, String appName) {
        ensureWebAppExists(appName);

        Server server = new Server(port);
        WebAppContext webApp = new WebAppContext(ResourceFactory.root().newResource(new File("src/test/apps/" + appName).toURI()), "/");
        server.setHandler(webApp);
        return new JettyServlet5Server(server, port);
    }

}
