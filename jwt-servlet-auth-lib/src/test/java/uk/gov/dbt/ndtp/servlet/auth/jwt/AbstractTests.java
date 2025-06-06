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

package uk.gov.dbt.ndtp.servlet.auth.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.HeaderSource;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

public abstract class AbstractTests<TRequest, TResponse> {
    public static final String CUSTOM_AUTH_HEADER = "X-Custom-Auth-Header";
    public static final String CUSTOM_CLAIM = "preferred_username";

    /**
     * Creates a mock request with the given headers present
     *
     * @param headers Headers
     * @return Mock request
     */
    protected abstract TRequest createMockRequest(Map<String, String> headers);

    /**
     * Creates a mock response
     *
     * @return Mock response
     */
    protected abstract TResponse createMockResponse();

    /**
     * Creates an engine for testing
     *
     * @return Engine
     */
    protected abstract JwtAuthenticationEngine<TRequest, TResponse> createEngine();

    /**
     * Creates an engine for testing
     *
     * @param authHeader       HTTP Header
     * @param authHeaderPrefix Optional header prefix
     * @param realm            Realm for challenges
     * @param usernameClaim    Username claim
     * @return Engine
     */
    protected abstract JwtAuthenticationEngine<TRequest, TResponse> createEngine(String authHeader,
                                                                                 String authHeaderPrefix, String realm,
                                                                                 String usernameClaim);

    /**
     * Creates an engine for testing
     *
     * @param authHeaders    Header sources
     * @param realm          Realm for challenges
     * @param usernameClaims Username claim(s)
     * @return Engine
     */
    protected abstract JwtAuthenticationEngine<TRequest, TResponse> createEngine(List<HeaderSource> authHeaders,
                                                                                 String realm,
                                                                                 List<String> usernameClaims);

    /**
     * Whether the engine implementation throws an error when its
     * {@link JwtAuthenticationEngine#sendError(Object, Throwable)} method gets called
     *
     * @return True if an error is thrown, false otherwise
     */
    protected boolean throwsOnUnexpectedErrors() {
        return false;
    }

    /**
     * Verifies that a given status code has been set
     *
     * @param request        Request
     * @param response       Response
     * @param expectedStatus Expected status
     * @throws IOException
     */
    protected abstract void verifyStatusCode(TRequest request, TResponse response, int expectedStatus) throws
            IOException;

    /**
     * Verifies that a given header is present and returns it
     *
     * @param request        Request
     * @param response       Response
     * @param expectedHeader Expected header
     * @return Actual header value
     */
    protected abstract String verifyHeaderPresent(TRequest request, TResponse response, String expectedHeader);

    protected TRequest verifyAuthenticated(String authHeader, JwtVerifier verifier, String expectedUser) {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine();
        return verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, authHeader, engine, verifier, expectedUser);
    }

    protected TRequest verifyAuthenticated(String authHeaderName, String authHeaderValue,
                                           JwtAuthenticationEngine<TRequest, TResponse> engine, JwtVerifier verifier,
                                           String expectedUser) {
        TRequest request = createMockRequest(Map.of(authHeaderName, authHeaderValue));
        TResponse response = createMockResponse();

        TRequest authenticatedRequest = engine.authenticate(request, response, verifier);
        Assert.assertNotNull(authenticatedRequest);
        String actualUser = getAuthenticatedUser(authenticatedRequest);
        Assert.assertEquals(actualUser, expectedUser);
        return authenticatedRequest;
    }

    protected TRequest verifyAuthenticated(Map<String, String> headers,
                                           JwtAuthenticationEngine<TRequest, TResponse> engine, JwtVerifier verifier,
                                           String expectedUser) {
        TRequest request = createMockRequest(headers);
        TResponse response = createMockResponse();

        TRequest authenticatedRequest = engine.authenticate(request, response, verifier);
        Assert.assertNotNull(authenticatedRequest);
        String actualUser = getAuthenticatedUser(authenticatedRequest);
        Assert.assertEquals(actualUser, expectedUser);
        return authenticatedRequest;
    }

    protected abstract String getAuthenticatedUser(TRequest authenticatedRequest);

    protected void verifyChallenge(TRequest request, TResponse response, int expectedStatus,
                                   String... expectedChallengeContents) throws IOException {
        verifyStatusCode(request, response, expectedStatus);

        if (expectedChallengeContents.length > 0) {
            String challenge = verifyHeaderPresent(request, response, JwtHttpConstants.HEADER_WWW_AUTHENTICATE);
            for (String expectedContent : expectedChallengeContents) {
                Assert.assertTrue(StringUtils.contains(challenge, expectedContent),
                                  "Expected challenge content " + expectedContent + " not found in Challenge " + challenge);
            }
        }
    }

    /**
     * Verifies that the request contains a non-null value for an attribute and returns that value
     *
     * @param request   Request
     * @param attribute Attribute
     * @return Attribute Value
     */
    protected abstract Object verifyRequestAttribute(TRequest request, String attribute);

    /**
     * Verifies that the authenticated request contains a given attribute
     *
     * @param request       Authenticated request
     * @param attribute     Request attribute
     * @param expectedValue Expected value that <strong>MUST</strong> match the request attribute value
     */
    protected void verifyRequestAttribute(TRequest request, String attribute, Object expectedValue) {
        Object value = verifyRequestAttribute(request, attribute);
        Assert.assertEquals(value, expectedValue);
    }
}
