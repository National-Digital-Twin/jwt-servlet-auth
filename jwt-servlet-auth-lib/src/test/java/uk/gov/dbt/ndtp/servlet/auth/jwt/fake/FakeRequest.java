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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeRequest {

    public final Map<String, List<String>> headers = new HashMap<>();
    public final Map<String, Object> attributes = new HashMap<>();

    public String username = null;

    public String requestUrl = null;

    public FakeRequest() {
    }

    public FakeRequest(String url) {
        this(Collections.emptyMap(), url);
    }

    public FakeRequest(Map<String, String> headers) {
        this(headers, null);
    }

    public FakeRequest(Map<String, String> headers, String url) {
        headers.forEach((key, value) -> {
            this.headers.computeIfAbsent(key, _key -> new ArrayList<>()).add(value);
        });
        this.requestUrl = url;
    }

    public Object getAttribute(String attribute) {
        return this.attributes.get(attribute);
    }

    public void setAttribute(String attribute, Object value) {
        this.attributes.put(attribute, value);
    }
}
