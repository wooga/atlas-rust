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
/*
    This code is based upon code from the Gradle org.gradle.internal.os.OperatingSystem class
    which is under the Apache v2.0 license. Original copyright from 2010 remains. Modifications
    from 2017+ are under the copyright and licensed mentioned above
*/
package org.ysb33r.grolifant.api.os

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.OperatingSystem

/** Linux implementation of {@code OperatingSystem}.
 *
 */
@CompileStatic
class Linux extends GenericUnix {
    static final OperatingSystem INSTANCE = new Linux()

    /** Confirms that this is an OS representation of a Linux operating system.
     *
     * @return {@code true}
     */
    @Override
    boolean isLinux() { true }

    private Linux() {
        super()
    }

}
