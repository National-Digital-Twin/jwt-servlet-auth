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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dbt.ndtp.servlet.auth.jwt.EqualsIgnoreCase.eqIgnoresCase;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import uk.gov.dbt.ndtp.servlet.auth.jwt.AbstractHeaderBasedEngineTests;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.TestEnumeration;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.HeaderSource;

public class TestServlet3Engine extends AbstractHeaderBasedEngineTests<HttpServletRequest, HttpServletResponse> {
    @Override
    protected HttpServletRequest createMockRequest(Map<String, String> headers) {
        return mockRequest(null, headers);
    }

    public static HttpServletRequest mockRequest(URI requestUri, Map<String, String> headers) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        headers.forEach((key, value) -> {
            when(request.getHeader(eqIgnoresCase(key))).thenReturn(value);
            when(request.getHeaders(eqIgnoresCase(key))).thenReturn(
                    new TestEnumeration<>(Collections.singletonList(value)));
        });
        if (requestUri != null) {
            when(request.getRequestURI()).thenReturn(requestUri.toString());
            when(request.getPathInfo()).thenReturn(requestUri.getPath());
        }
        when(request.getAttribute(any())).thenAnswer(invocationOnMock -> {
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(request).setAttribute(eq(invocationOnMock.getArgument(0)), captor.capture());
            return captor.getValue();
        });
        return request;
    }

    @Override
    protected HttpServletResponse createMockResponse() {
        return mock(HttpServletResponse.class);
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine() {
        return new uk.gov.dbt.ndtp.servlet.auth.jwt.servlet3.Servlet3JwtAuthenticationEngine();
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine(String authHeader,
                                                                                            String authHeaderPrefix,
                                                                                            String realm,
                                                                                            String usernameClaim) {
        return new uk.gov.dbt.ndtp.servlet.auth.jwt.servlet3.Servlet3JwtAuthenticationEngine(List.of(new HeaderSource(authHeader, authHeaderPrefix)), realm,
                                                   usernameClaim != null ? List.of(usernameClaim) : null);
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine(
            List<HeaderSource> authHeaders, String realm, List<String> usernameClaims) {
        return new Servlet3JwtAuthenticationEngine(authHeaders, realm, usernameClaims);
    }

    @Override
    protected void verifyStatusCode(HttpServletRequest request, HttpServletResponse httpServletResponse,
                                    int expectedStatus) throws IOException {
        verifyStatusCode(httpServletResponse, expectedStatus);
    }

    public static void verifyStatusCode(HttpServletResponse httpServletResponse, int expectedStatus) throws
            IOException {
        verify(httpServletResponse, atMostOnce()).sendError(eq(expectedStatus));
        verify(httpServletResponse, atMostOnce()).sendError(eq(expectedStatus), any());
    }

    @Override
    protected String verifyHeaderPresent(HttpServletRequest request, HttpServletResponse httpServletResponse,
                                         String expectedHeader) {
        return verifyHeaderPresent(httpServletResponse, expectedHeader);
    }

    public static String verifyHeaderPresent(HttpServletResponse httpServletResponse, String expectedHeader) {
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(httpServletResponse, times(1)).addHeader(eq(expectedHeader), valueCapture.capture());
        return valueCapture.getValue();
    }

    @Override
    protected String getAuthenticatedUser(HttpServletRequest authenticatedRequest) {
        return authenticatedRequest.getRemoteUser();
    }

    @Override
    protected Object verifyRequestAttribute(HttpServletRequest httpServletRequest, String attribute) {
        Object value = httpServletRequest.getAttribute(attribute);
        Assert.assertNotNull(value, "Attribute " + attribute + " unexpectedly null");
        return value;
    }
}
