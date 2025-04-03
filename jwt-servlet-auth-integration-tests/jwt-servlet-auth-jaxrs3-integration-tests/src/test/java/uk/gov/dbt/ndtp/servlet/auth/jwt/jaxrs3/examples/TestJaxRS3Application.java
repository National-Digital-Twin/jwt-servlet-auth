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

package uk.gov.dbt.ndtp.servlet.auth.jwt.jaxrs3.examples;

import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.ConfigurationParameters;
import uk.gov.dbt.ndtp.servlet.auth.jwt.jaxrs3.JaxRs3AutomatedAuthConfigurationListener;
import uk.gov.dbt.ndtp.servlet.auth.jwt.jaxrs3.examples.HelloWorldApplication;
import uk.gov.dbt.ndtp.servlet.auth.jwt.testing.AbstractIntegrationTests;
import uk.gov.dbt.ndtp.servlet.auth.jwt.testing.AbstractServer;
import jakarta.servlet.ServletContextListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.jetty.ee9.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

public class TestJaxRS3Application extends AbstractIntegrationTests {
    @Override
    protected boolean areNonExistentUrlsFiltered() {
        return false;
    }

    @Override
    protected AbstractServer buildProgrammaticApplication(File keyFile, int port) {
        WebappContext context = new WebappContext("JWT Auth Integration Tests", "/");

        // Add the JAX-RS application servlet
        ServletRegistration registration =
                context.addServlet(ServletContainer.class.getCanonicalName(), ServletContainer.class);
        registration.addMapping("/*");
        registration.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS,
                                      HelloWorldApplication.class.getCanonicalName());

        ServletContextListener listener = new JaxRs3AutomatedAuthConfigurationListener();
        context.addListener(listener);
        context.addContextInitParameter(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath());

        URI baseUri = null;
        try {
            baseUri = new URI(String.format("http://localhost:%d", port));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, false);
        return new GrizzlyJaxRS3Server(server, context, port);
    }

    @Override
    protected AbstractServer buildWebXmlApplication(int port, String appName) {
        ensureWebAppExists(appName);

        Server server = new Server(port);
        WebAppContext webApp = new WebAppContext(ResourceFactory.root().newResource(new File("src/test/apps/" + appName).toURI()), "/");
        server.setHandler(webApp);

        return new Jetty11JaxRS3Server(server, port);
    }
}
