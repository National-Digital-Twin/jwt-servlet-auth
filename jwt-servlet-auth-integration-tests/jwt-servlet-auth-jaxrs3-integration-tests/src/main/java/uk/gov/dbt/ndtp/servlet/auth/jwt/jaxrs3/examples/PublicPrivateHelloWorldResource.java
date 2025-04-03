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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * A toy JAX-RS resource for integration testing
 */
@Path("/")
public class PublicPrivateHelloWorldResource extends AbstractResource {

    /**
     * Produces a hello world response
     * @return Response
     */
    @GET
    @Path("private/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public Response privateHello() {
        return helloWorld();
    }

    /**
     * Produces a hello world response
     * @return Response
     */
    @GET
    @Path("public/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public Response publicHello() {
        return helloWorld();
    }
}
