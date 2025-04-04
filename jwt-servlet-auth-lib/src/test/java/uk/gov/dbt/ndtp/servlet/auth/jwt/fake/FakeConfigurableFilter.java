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

import java.util.List;
import uk.gov.dbt.ndtp.servlet.auth.jwt.AbstractConfigurableJwtAuthFilter;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtAuthenticationEngine;
import uk.gov.dbt.ndtp.servlet.auth.jwt.JwtServletConstants;
import uk.gov.dbt.ndtp.servlet.auth.jwt.PathExclusion;
import uk.gov.dbt.ndtp.servlet.auth.jwt.verification.JwtVerifier;

public class FakeConfigurableFilter extends AbstractConfigurableJwtAuthFilter<FakeRequest, FakeResponse> {

    private JwtAuthenticationEngine<FakeRequest, FakeResponse> engine;
    private JwtVerifier verifier;
    private List<PathExclusion> exclusions;

    public FakeConfigurableFilter() {
    }

    public FakeConfigurableFilter(JwtAuthenticationEngine<FakeRequest, FakeResponse> engine, JwtVerifier verifier,
                                  List<PathExclusion> exclusions) {
        this.engine = engine;
        this.verifier = verifier;
        this.exclusions = exclusions;
    }

    @Override
    protected Object getAttribute(FakeRequest fakeRequest, String attribute) {
        switch (attribute) {
            case JwtServletConstants.ATTRIBUTE_JWT_ENGINE:
                if (engine != null) {
                    return this.engine;
                }
                break;
            case JwtServletConstants.ATTRIBUTE_JWT_VERIFIER:
                if (verifier != null) {
                    return this.verifier;
                }
                break;
            case JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS:
                if (exclusions != null) {
                    return this.exclusions;
                }
                break;
            default:
        }
        return fakeRequest.attributes.get(attribute);
    }

    @Override
    protected String getPath(FakeRequest fakeRequest) {
        return fakeRequest.requestUrl;
    }

    @Override
    protected JwtAuthenticationEngine<FakeRequest, FakeResponse> getDefaultEngine() {
        return new FakeEngine();
    }

    @Override
    protected int getStatus(FakeResponse fakeResponse) {
        return fakeResponse.status;
    }
}
