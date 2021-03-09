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
import org.gradle.util.GradleVersion

/** Internal used enumeration to help with logic with specific Gradle version differences.
 *
 */
@CompileStatic
class LegacyLevel {
    public static final boolean PRE_4_1 = GradleVersion.current() < GradleVersion.version('4.1')
    public static final boolean PRE_4_3 = GradleVersion.current() < GradleVersion.version('4.3')
    public static final boolean PRE_4_5 = GradleVersion.current() < GradleVersion.version('4.5')
    public static final boolean PRE_4_8 = GradleVersion.current() < GradleVersion.version('4.8')
    public static final boolean PRE_4_9 = GradleVersion.current() < GradleVersion.version('4.9')
    public static final boolean PRE_5_1 = GradleVersion.current() < GradleVersion.version('5.1')
    public static final boolean PRE_5_6 = GradleVersion.current() < GradleVersion.version('5.6')
}
