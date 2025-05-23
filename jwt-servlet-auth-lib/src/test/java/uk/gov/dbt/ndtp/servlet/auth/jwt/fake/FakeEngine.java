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

package uk.gov.dbt.ndtp.servlet.auth.jwt.fake;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dbt.ndtp.servlet.auth.jwt.HeaderBasedJwtAuthenticationEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtHttpConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.challenges.Challenge;
import uk.gov.dbt.ndtp.servlet.auth.jwt.challenges.TokenCandidate;
import uk.gov.dbt.ndtp.servlet.auth.jwt.sources.HeaderSource;

public class FakeEngine extends HeaderBasedJwtAuthenticationEngine<FakeRequest, FakeResponse> {

    public FakeEngine() {
        this(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER, null, null);
    }

    public FakeEngine(String header, String headerPrefix, String realm, String usernameClaim) {
        this(List.of(new HeaderSource(header, headerPrefix)), realm, usernameClaim != null ? List.of(usernameClaim) : null);
    }

    public FakeEngine(List<HeaderSource> headers, String realm, List<String> usernameClaims) {
        super(headers, realm, usernameClaims);
    }

    @Override
    protected boolean hasRequiredParameters(
        FakeRequest fakeRequest) {
        String[] allowedHeaders =
                this.headers.stream().map(HeaderSource::getHeader).toList().toArray(new String[0]);
        return fakeRequest.headers.keySet()
                                  .stream()
                                  .anyMatch(key -> StringUtils.equalsAnyIgnoreCase(key, allowedHeaders));
    }

    @Override
    protected List<TokenCandidate> extractTokens(
        FakeRequest fakeRequest) {
        return this.headers.stream()
                           .flatMap(h -> fakeRequest.headers.entrySet()
                                                            .stream()
                                                            .filter(e -> StringUtils.equalsIgnoreCase(e.getKey(),
                                                                                                      h.getHeader()))
                                                            .flatMap(e -> e.getValue()
                                                                           .stream()
                                                                           .map(v -> new TokenCandidate(h, v))))
                           .collect(Collectors.toList());

    }

    @Override
    protected FakeRequest prepareRequest(
        FakeRequest fakeRequest, Jws<Claims> jws, String username) {
        fakeRequest.username = username;
        return fakeRequest;
    }

    @Override
    protected void sendChallenge(
        FakeRequest fakeRequest, FakeResponse fakeResponse, Challenge challenge) {
        fakeResponse.status = challenge.statusCode();
        String realm = this.selectRealm(this.getRequestUrl(fakeRequest));
        Map<String, String> challengeParams =
                buildChallengeParameters(challenge.errorCode(), challenge.errorDescription());
        fakeResponse.headers.put(JwtHttpConstants.HEADER_WWW_AUTHENTICATE,
                                 Collections.singletonList(buildAuthorizationHeader(realm, challengeParams)));
    }

    @Override
    protected void sendError(FakeResponse fakeResponse, Throwable err) {
        fakeResponse.status = 500;
    }

    @Override
    protected String getRequestUrl(FakeRequest fakeRequest) {
        return fakeRequest.requestUrl;
    }

    @Override
    protected void setRequestAttribute(FakeRequest fakeRequest, String attribute, Object value) {
        fakeRequest.setAttribute(attribute, value);
    }
}
