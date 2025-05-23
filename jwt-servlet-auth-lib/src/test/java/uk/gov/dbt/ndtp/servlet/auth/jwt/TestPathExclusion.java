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

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestPathExclusion {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exclusion_invalid_01() {
        new PathExclusion(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exclusion_invalid_02() {
        new PathExclusion("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exclusion_invalid_03() {
        new PathExclusion("    ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exclusion_invalid_04() {
        new PathExclusion("/path(unfinished_regex_clause*");
    }

    @DataProvider(name = "excludeAllPatterns")
    private Object[][] excludeAllPatterns() {
        return new Object[][] {
                { "*" },
                { "/*" },
                { "*/" },
                { "**" },
                { "*/*" },
                { " * "},
                { "/*/*" },
                { "/*/*/*" }
        };
    }

    @Test(dataProvider = "excludeAllPatterns", expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*excludes all paths")
    public void givenAnExcludeAllPattern_whenCreating_thenIllegalArgumentError(String pattern) {
        // Given, When and Then
        new PathExclusion(pattern);
    }

    @Test
    public void exclusion_valid_01() {
        PathExclusion exclusion = new PathExclusion("/fixed");
        Assert.assertFalse(exclusion.isWildcard());
        Assert.assertEquals(exclusion.getPattern(), "/fixed");

        Assert.assertTrue(exclusion.matches("/fixed"));
        Assert.assertFalse(exclusion.matches("/fixed-thing"));
        Assert.assertFalse(exclusion.matches("/other"));

        verifyBlankPathsAreNotMatched(exclusion);
    }

    private static void verifyBlankPathsAreNotMatched(PathExclusion exclusion) {
        Assert.assertFalse(exclusion.matches(null));
        Assert.assertFalse(exclusion.matches(""));
        Assert.assertFalse(exclusion.matches("   "));
    }

    @Test
    public void exclusion_valid_02() {
        PathExclusion exclusion = new PathExclusion("/status/*");
        Assert.assertTrue(exclusion.isWildcard());
        Assert.assertEquals(exclusion.getPattern(), "/status/*");

        Assert.assertTrue(exclusion.matches("/status/"));
        Assert.assertTrue(exclusion.matches("/status/healthz"));
        Assert.assertFalse(exclusion.matches("/fixed"));

        verifyBlankPathsAreNotMatched(exclusion);
    }

    @Test
    public void exclusion_parsing_01() {
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns(null);
        Assert.assertEquals(exclusions.size(), 0);
        exclusions = PathExclusion.parsePathPatterns("");
        Assert.assertEquals(exclusions.size(), 0);
        exclusions = PathExclusion.parsePathPatterns("    ");
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void exclusion_parsing_02() {
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/fixed,/status/*");
        Assert.assertEquals(exclusions.size(), 2);

        PathExclusion a = exclusions.get(0);
        Assert.assertFalse(a.isWildcard());
        Assert.assertEquals(a.getPattern(), "/fixed");

        PathExclusion b = exclusions.get(1);
        Assert.assertTrue(b.isWildcard());
        Assert.assertEquals(b.getPattern(), "/status/*");
    }

    @Test
    public void exclusion_parsing_03() {
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/fixed,,/status/*,");
        Assert.assertEquals(exclusions.size(), 2);
    }

    @Test
    public void exclusion_parsing_04() {
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns(",,,");
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void exclusion_parsing_05() {
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns(",  ,  ,");
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void givenWildcardExclusionWithRegexChars_whenTestingForExclusion_thenFails() {
        // Given
        PathExclusion exclusion = new PathExclusion("/$/status/*");

        // When
        boolean excluded = exclusion.matches("/$/status/health");

        // Then
        Assert.assertFalse(excluded);
    }

    @Test
    public void givenWildcardExclusionWithEscapedRegexChars_whenTestingForExclusion_thenSuccess() {
        // Given
        PathExclusion exclusion = new PathExclusion("/\\$/status/*");

        // When
        boolean excluded = exclusion.matches("/$/status/health");

        // Then
        Assert.assertTrue(excluded);
    }
}
