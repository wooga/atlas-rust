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
package org.ysb33r.grolifant.internal.v4.os

import groovy.transform.CompileStatic

/** Constants for internal usage in {@link org.ysb33r.grolifant.api.OperatingSystem}.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.14
 */
@CompileStatic
class OperatingSystemConstants {

    public static final String I386 = 'i386'
    public static final String AMD64 = 'amd64'
    public static final String X86_64 = 'x86_64'
    public static final String X86 = 'x86'

    public static final String DOT_EXE = '.exe'
    public static final String DOT_BAT = '.bat'

    public static final String UNKNOWN = 'unknown'
}
