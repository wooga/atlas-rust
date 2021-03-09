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
package org.ysb33r.grolifant.api;

import java.io.File;

/**
 * Verifies a checksum.
 *
 * @since 0.15.0
 */
public interface CheckSumVerification {
    /**
     * Verifies a file against a checksum
     *
     * @param downloadedTarget File that was downloaded
     * @throws org.ysb33r.grolifant.api.errors.ChecksumFailedException if verification failed
     */
    void verify(final File downloadedTarget);

    /**
     * Returns the checksum in question.
     *
     * @return Checksum. Can be {@code null}.
     */
    String getChecksum();
}
