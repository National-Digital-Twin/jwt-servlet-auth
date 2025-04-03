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

import uk.gov.dbt.ndtp.servlet.auth.jwt.AbstractConfigurableJwtAuthFilter;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter that requires that users provide a valid JSON Web Token (JWT) in order for their requests to proceed
 */
public class JwtAuthFilter  extends AbstractConfigurableJwtAuthFilter<HttpServletRequest, HttpServletResponse>
    implements Filter {

    /**
     * Default engine singleton instance
     */
    protected static final Servlet3JwtAuthenticationEngine DEFAULT_ENGINE = new Servlet3JwtAuthenticationEngine();

    @Override
    public void init(FilterConfig filterConfig) {
        this.configure(new Servlet3FilterConfigAdaptor(filterConfig));
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
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        super.doFilter(request, response, (req,resp) -> {
            try {
                filterChain.doFilter(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service.
     *
     * <p>This method is only called once all threads within the filter's
     * doFilter method have exited or after a timeout period has passed. After the web container calls
     * this method, it will not call the doFilter method again on this instance of the filter.
     *
     * <p>This method gives the filter an opportunity to clean up any
     * resources that are being held (for example, memory, file handles, threads) and make sure that
     * any persistent state is synchronized with the filter's current state in memory.
     */
    @Override
    public void destroy() {
        //Overriden method
        // Method intentionally left empty for now
        // The method will be implemented in a future release or is not needed in the current version.
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
