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

package uk.gov.dbt.ndtp.servlet.auth.jwt.servlet5;

import uk.gov.dbt.ndtp.servlet.auth.jwt.AbstractConfigurableJwtAuthFilter;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that requires that users provide a valid JSON Web Token (JWT) in order for their requests to proceed
 */
public class JwtAuthFilter extends AbstractConfigurableJwtAuthFilter<HttpServletRequest, HttpServletResponse>
        implements Filter {

    /**
     * Default engine singleton instance
     */
    private static final Servlet5JwtAuthenticationEngine DEFAULT_ENGINE = new Servlet5JwtAuthenticationEngine();

    @Override
    public void init(FilterConfig filterConfig) {
        this.configure(new Servlet5FilterConfigAdaptor(filterConfig));
    }

    @Override
    protected Object getAttribute(HttpServletRequest httpServletRequest, String attribute) {
        return httpServletRequest.getServletContext().getAttribute(attribute);
    }

    @Override
    protected String getPath(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI();
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> getDefaultEngine() {
        return DEFAULT_ENGINE;
    }

    @Override
    protected int getStatus(HttpServletResponse httpServletResponse) {
        return httpServletResponse.getStatus();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        super.doFilter(request, response, (req, resp) -> {
            try {
                filterChain.doFilter(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Used by unit tests to check authentication results
     *
     * @return Last filter request results, {@code null} if authentication failed
     */
    final HttpServletRequest lastResult() {
        return this.lastAuthenticatedRequest;
    }

}
