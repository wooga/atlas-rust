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
package org.ysb33r.grolifant.internal.v4

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.ClassLocation

/** Implementation of {@link ClassLocation}.
 *
 * @since 0.9
 */
@CompileStatic
class ClassLocationImpl implements ClassLocation {
    final File file
    final URL runtime

    ClassLocationImpl(final File f) {
        this.file = f
    }

    ClassLocationImpl(final URL f) {
        this.runtime = f
    }
}
