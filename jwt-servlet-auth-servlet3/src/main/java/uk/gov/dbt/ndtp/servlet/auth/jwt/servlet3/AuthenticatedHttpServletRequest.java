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

package uk.gov.dbt.ndtp.servlet.auth.jwt.servlet3;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * An authenticated HTTP Servlet Request, which is a decorator around the original request
 */
public class AuthenticatedHttpServletRequest extends HttpServletRequestWrapper {

    private final Jws<Claims> jws;
    private final String username;

    /**
     * Creates a new authenticated request
     * @param request Original request
     * @param jws Verified JWT
     * @param username Username extracted from the JWT
     */
    public AuthenticatedHttpServletRequest(HttpServletRequest request, Jws<Claims> jws, String username) {
        super(request);
        this.username = username;
        this.jws = jws;
    }

    @Override
    public String getRemoteUser() {
        return this.username;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> username;
    }

    /**
     * Gets the verified JSON Web Token (JWT) for the request
     * @return Verified JWT
     */
    public Jws<Claims> getVerifiedJwt() {
        return this.jws;
    }
}
