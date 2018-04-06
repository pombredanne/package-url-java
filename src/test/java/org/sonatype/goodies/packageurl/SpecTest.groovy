/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.packageurl

import org.junit.BeforeClass
import org.junit.Test
import org.sonatype.goodies.testsupport.TestSupport

/**
 * Package URL specification tests.
 *
 * @see TestSuiteData
 */
class SpecTest
    extends TestSupport
{
    static List<TestSuiteData.Entry> entries

    @BeforeClass
    static void 'load test-suite-data entries'() {
        entries = TestSuiteData.get()
    }

    @Test
    void 'test parsing'() {
        Set<String> skip = [
                // FIXME: these tests have notes that these formats should have lower-cased, but spec doesn't indicate they should be
                'bitbucket namespace and name should be lowercased',
                'github namespace and name should be lowercased',
                'nuget names are case sensitive',
                'pypi names have special rules and not case sensitive',

                // FIXME: these tests expect / in qualifier value to be non-encoded in canonical, and have apparently bad test data
                'maven uses qualifiers',
                'maven pom reference'
        ]

        entries.each { entry ->
            if (skip.contains(entry.description)) {
                log "Skipping entry: $entry"
                return
            }

            log "Entry: $entry"
            try {
                def purl = PackageUrl.parse(entry.purl)
                log "PURL: ${purl.explain()} -> $purl"

                assert purl.toString() == entry.canonical_purl
                assert purl.type == entry.type
                assert purl.namespaceAsString == entry.namespace
                assert purl.name == entry.name
                assert purl.version == entry.version
                assert purl.qualifiers == entry.qualifiers
                assert purl.subpathAsString == entry.subpath
            }
            catch (e) {
                if (!entry.is_invalid) {
                    throw e
                }
            }
        }
    }
}