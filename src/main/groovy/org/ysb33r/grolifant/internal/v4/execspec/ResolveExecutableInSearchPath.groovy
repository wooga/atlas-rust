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
package org.ysb33r.grolifant.internal.v4.execspec

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.OperatingSystem

import org.ysb33r.grolifant.api.errors.ExecutionException
import org.ysb33r.grolifant.api.v4.StringUtils
import org.ysb33r.grolifant.api.v4.exec.ResolvableExecutable
import org.ysb33r.grolifant.api.v4.exec.ResolvedExecutableFactory

/** Resolves exe by search the system path.
 *
 * @since 0.3
 */
@CompileStatic
class ResolveExecutableInSearchPath implements ResolvedExecutableFactory {

    static final ResolveExecutableInSearchPath INSTANCE = new ResolveExecutableInSearchPath()

    /** Creates {@link ResolvableExecutable} from a specific input.
     *
     * @param options Ignored.
     * @param lazyPath Any object that can be resolved to a string using {@link StringUtils#stringize(Object)}.
     * @return The resolved exe.
     */
    @Override
    ResolvableExecutable build(Map<String, Object> options, Object lazyPath) {
        new ResolvableExecutable() {
            @Override
            File getExecutable() {
                final String path = StringUtils.stringize(lazyPath)
                final File foundPath = OS.findInPath(path)

                if (foundPath == null) {
                    throw new ExecutionException("Cannot locate '${path}' in system search path")
                }

                foundPath
            }
        }
    }

    private static final OperatingSystem OS = OperatingSystem.current()
}
