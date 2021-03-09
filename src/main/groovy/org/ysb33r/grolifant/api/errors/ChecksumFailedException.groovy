/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronje 2016 - 2020
 *
 * This software is licensed under the Apache License 2.0
 * See http://www.apache.org/licenses/LICENSE-2.0 for license details
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * ============================================================================
 */
package org.ysb33r.grolifant.api.errors

import groovy.transform.CompileStatic

/** Thrown when a checksum fails.
 *
 * @since 0.1
 */
@CompileStatic
class ChecksumFailedException extends RuntimeException implements GrolifantError {

    /** Instantiates an exception.
     *
     * @param distributionName A descriptive name of the distribution
     * @param sourceUrl The URL/URI where it was downloaded from
     * @param localCompressedFile The location of the downloaded archive
     * @param expectedSum The expected checksum
     * @param actualSum The actual checksum
     */
    ChecksumFailedException(final String distributionName,
                            final String sourceUrl,
                            final File localCompressedFile,
                            final String expectedSum,
                            final String actualSum) {
        super("""Verification of ${distributionName} failed!

This ${distributionName} may have been tampered with.
 Distribution Url: ${sourceUrl}
Download Location: ${localCompressedFile}
Expected checksum: ${expectedSum}
  Actual checksum: ${actualSum}
""")
    }
}
