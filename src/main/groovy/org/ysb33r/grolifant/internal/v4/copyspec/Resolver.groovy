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
package org.ysb33r.grolifant.internal.v4.copyspec

import groovy.transform.CompileStatic
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.copy.CopySpecInternal

/** Resolves files in a copy specification.
 *
 * @since 0.8
 */
@CompileStatic
class Resolver {

    /** Resolves files in {@link CopySpec}.
     *
     * @param copySpec Implementation of a {@link CopySpec}
     * @return Resulting file collection.
     */
    static FileCollection resolveFiles(CopySpec copySpec) {
        ((CopySpecInternal) copySpec).buildRootResolver().allSource
    }
}
