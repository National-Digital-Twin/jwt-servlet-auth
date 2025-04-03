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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.Assert;
import uk.gov.dbt.ndtp.servlet.auth.jwt.AbstractConfigurableFilterTests;
import uk.gov.dbt.ndtp.servlet.auth.jwt.FilterConfigAdaptorWrapper;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtServletConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.PathExclusion;
import uk.gov.dbt.ndtp.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.HeaderSource;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

public class TestServlet5Filter extends
        AbstractConfigurableFilterTests<HttpServletRequest, HttpServletResponse, JwtAuthFilter> {
    private ServletContext context;

    @Override
    protected HttpServletRequest createMockRequest(String requestUri, Map<String, String> headers) {
        HttpServletRequest request = TestServlet5Engine.mockRequest(URI.create(requestUri), headers);
        when(request.getServletContext()).thenReturn(this.context);
        return request;
    }

    @Override
    protected HttpServletRequest createMockRequest(FilterConfigAdaptorWrapper config, Map<String, String> headers) {
        HttpServletRequest request = TestServlet5Engine.mockRequest(null, headers);
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(any())).thenAnswer(a -> config.getAttribute(a.getArgument(0, String.class)));
        when(request.getServletContext()).thenReturn(servletContext);
        return request;
    }

    @Override
    protected JwtAuthFilter createFilter(JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> engine,
                                         JwtVerifier verifier, List<PathExclusion> exclusions) {
        this.context = null;
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(eq(JwtServletConstants.ATTRIBUTE_JWT_ENGINE))).thenReturn(engine);
        when(servletContext.getAttribute(eq(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER))).thenReturn(verifier);
        when(servletContext.getAttribute(eq(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS))).thenReturn(exclusions);
        this.context = servletContext;

        return new JwtAuthFilter();
    }

    @Override
    protected void invokeFilter(JwtAuthFilter filter, HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse) {
        try {
            filter.doFilter(httpServletRequest, httpServletResponse, mock(FilterChain.class));
        } catch (IOException e) {
            Assert.fail("Unexpected IO error: " + e.getMessage());
        } catch (ServletException e) {
            Assert.fail("Unexpected Servlet error: " + e.getMessage());
        }
    }

    @Override
    protected void verifyNoChallenge(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        verify(httpServletResponse, never()).setStatus(anyInt());
    }

    @Override
    protected String getAuthenticatedUser(JwtAuthFilter filter, HttpServletRequest httpServletRequest) {
        HttpServletRequest authenticatedRequest = filter.lastResult();
        return authenticatedRequest != null ? authenticatedRequest.getRemoteUser() : null;
    }

    @Override
    protected HttpServletRequest createMockRequest(Map<String, String> headers) {
        HttpServletRequest request = TestServlet5Engine.mockRequest(null, headers);
        when(request.getServletContext()).thenReturn(this.context);
        return request;
    }

    @Override
    protected HttpServletResponse createMockResponse() {
        return mock(HttpServletResponse.class);
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine() {
        return new Servlet5JwtAuthenticationEngine();
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine(String authHeader,
                                                                                            String authHeaderPrefix,
                                                                                            String realm,
                                                                                            String usernameClaim) {
        return new Servlet5JwtAuthenticationEngine(List.of(new HeaderSource(authHeader, authHeaderPrefix)), realm,
                                                   usernameClaim != null ? List.of(usernameClaim) : null);
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine(
            List<HeaderSource> authHeaders, String realm, List<String> usernameClaims) {
        return new Servlet5JwtAuthenticationEngine(authHeaders, realm, usernameClaims);
    }

    @Override
    protected void verifyStatusCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    int expectedStatus) throws IOException {
        TestServlet5Engine.verifyStatusCode(httpServletResponse, expectedStatus);
    }

    @Override
    protected String verifyHeaderPresent(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                         String expectedHeader) {
        return TestServlet5Engine.verifyHeaderPresent(httpServletResponse, expectedHeader);
    }

    @Override
    protected String getAuthenticatedUser(HttpServletRequest authenticatedRequest) {
        return authenticatedRequest.getRemoteUser();
    }

    @Override
    protected RuntimeConfigurationAdaptor createConfigAdaptor(Map<String, String> configuration) {
        return new Servlet5FilterConfigAdaptor(new FilterConfig() {
            @Override
            public String getFilterName() {
                return "Test";
            }

            @Override
            public ServletContext getServletContext() {
                return Mockito.mock(ServletContext.class);
            }

            @Override
            public String getInitParameter(String name) {
                return configuration.get(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.enumeration(configuration.keySet());
            }
        });
    }

    @Override
    protected JwtAuthFilter createUnconfiguredFilter() {
        return new JwtAuthFilter();
    }

    @Override
    protected Object verifyRequestAttribute(HttpServletRequest httpServletRequest, String attribute) {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(httpServletRequest).setAttribute(eq(attribute), captor.capture());
        Object value = captor.getValue();
        Assert.assertNotNull(value, "Attribute " + attribute + " unexpectedly null");
        return value;
    }
}
