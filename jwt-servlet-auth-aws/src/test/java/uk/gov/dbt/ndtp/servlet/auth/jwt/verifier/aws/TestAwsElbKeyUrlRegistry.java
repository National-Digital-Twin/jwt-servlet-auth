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

package uk.gov.dbt.ndtp.servlet.auth.jwt.verifier.aws;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.JwkSetBuilder;
import io.jsonwebtoken.security.Jwks;
import org.testng.Assert;
import org.testng.annotations.*;

import java.security.Key;
import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAwsElbKeyUrlRegistry {

    private AwsElbServer keyServer;
    private JwkSet jwks;

    private Object[][] keyIds;

    private static final AtomicInteger TEST_PORT = new AtomicInteger(35791);

    @BeforeClass
    public void setup() throws Exception {
        List<KeyPair> keyPairs = List.of(Jwts.SIG.ES256.keyPair().build(), Jwts.SIG.ES384.keyPair().build(),
                                         Jwts.SIG.ES512.keyPair().build());
        JwkSetBuilder privateJwks = Jwks.set();
        JwkSetBuilder publicJwks = Jwks.set();
        keyPairs.forEach(p -> {
            privateJwks.add(Jwks.builder().keyPair(p).idFromThumbprint().build());
            publicJwks.add(Jwks.builder().key(p.getPublic()).idFromThumbprint().build());
        });
        this.jwks = privateJwks.build();

        this.keyIds = new Object[keyPairs.size()][];
        for (int i = 0; i < this.jwks.getKeys().size(); i++) {
            this.keyIds[i] =
                    new Object[] { this.jwks.getKeys().stream().skip(i).map(k -> k.getId()).findFirst().orElse(null) };
        }

        this.keyServer = new AwsElbServer(TEST_PORT.getAndIncrement(), publicJwks.build());
        this.keyServer.start();
    }

    @AfterMethod
    public void testCleanup() {
        AwsElbKeyUrlRegistry.reset();
    }

    @AfterClass
    public void teardown() throws Exception {
        this.keyServer.stop();
    }

    @DataProvider(name = "keyIds")
    public Object[][] keyIds() {
        return this.keyIds;
    }

    @Test
    public void givenNormalRegion_whenLookingUpKeys_thenDefaultUrlIsUsed() {
        // Given

        // When
        String keyUrlFormat = AwsElbKeyUrlRegistry.lookupUrlFormat(
            TestAwsElbKeyResolver.TEST_AWS_REGION);
        String keyUrl = AwsElbKeyUrlRegistry.prepareKeyUrl(TestAwsElbKeyResolver.TEST_AWS_REGION, "example");

        // Then
        Assert.assertEquals(keyUrlFormat, AwsElbKeyUrlRegistry.DEFAULT_KEY_URL_FORMAT);
        Assert.assertEquals(keyUrl,
                            String.format(AwsElbKeyUrlRegistry.DEFAULT_KEY_URL_FORMAT, TestAwsElbKeyResolver.TEST_AWS_REGION, "example"));
    }

    @Test
    public void givenGovCloudRegion_whenLookingUpKeys_thenCustomUrlIsUsed() {
        // Given

        // When
        String keyUrlFormat = AwsElbKeyUrlRegistry.lookupUrlFormat("us-gov-west-1");
        String keyUrl = AwsElbKeyUrlRegistry.prepareKeyUrl("us-gov-west-1", "example");

        // Then
        Assert.assertNotEquals(keyUrlFormat, AwsElbKeyUrlRegistry.DEFAULT_KEY_URL_FORMAT);
        Assert.assertEquals(keyUrl, String.format(AwsElbKeyUrlRegistry.GOVCLOUD_WEST_KEY_URL_FORMAT, "example"));
    }

    @Test
    public void givenCustomRegion_whenLookingUpKeys_thenCustomUrlIsUsed() {
        // Given
        AwsElbKeyUrlRegistry.register("custom", this.keyServer.getUrl() + "/%s");

        // When
        String keyUrl = AwsElbKeyUrlRegistry.prepareKeyUrl("custom", "example");

        // Then
        Assert.assertEquals(keyUrl, this.keyServer.getUrl() + "/example");
    }

    @Test(dataProvider = "keyIds")
    public void givenCustomRegion_whenResolvingKeys_thenKeyIsResolved(String keyId) {
        // Given
        AwsElbKeyUrlRegistry.register("custom", this.keyServer.getUrl() + "/%s");
        AwsElbKeyResolver resolver = new AwsElbKeyResolver("custom");
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);

        // When
        Key key = resolver.locate(header);

        // Then
        Assert.assertNotNull(key);
    }

    @Test(expectedExceptions = InvalidKeyException.class)
    public void givenCustomRegion_whenResolvingNonExistentKey_thenErrorIsThrown() {
        // Given
        AwsElbKeyUrlRegistry.register("custom", this.keyServer.getUrl() + "/%s");
        AwsElbKeyResolver resolver = new AwsElbKeyResolver("custom");
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn("no-such-key");

        // When and Then
        resolver.locate(header);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*at least one.*")
    public void givenBadRegionUrlFormatWithTooFewPlaceholders_whenPreparingUrl_thenErrorIsThrown() {
        // Given
        AwsElbKeyUrlRegistry.register("custom", "http://example.org");

        // When and Then
        AwsElbKeyUrlRegistry.prepareKeyUrl("custom", "test");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*too many.*")
    public void givenBadRegionUrlFormatWithTooManyPlaceholders_whenPreparingUrl_thenErrorIsThrown() {
        // Given
        AwsElbKeyUrlRegistry.register("custom", "http://example.org/%s/%s/%s");

        // When and Then
        AwsElbKeyUrlRegistry.prepareKeyUrl("custom", "test");
    }
}
