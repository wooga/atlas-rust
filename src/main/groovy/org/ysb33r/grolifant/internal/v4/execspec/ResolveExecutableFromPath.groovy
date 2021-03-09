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
import org.gradle.api.Project
import org.ysb33r.grolifant.api.v4.exec.ResolvableExecutable
import org.ysb33r.grolifant.api.v4.exec.ResolvedExecutableFactory

/** Resolves an exe from a path.
 *
 * @since 0.3
 */
@CompileStatic
class ResolveExecutableFromPath implements ResolvedExecutableFactory {

    ResolveExecutableFromPath(Project project) {
        this.project = project
    }

    /** Builds a path-located execution resolver.
     *
     * @param options Ignored.
     * @param lazyPath Lazy-evaluated path to use. Anything that can be resolved by {@code [project.file} is acceptable.
     * @return Resolved path to exe.
     */
    @Override
    ResolvableExecutable build(Map<String, Object> options, Object lazyPath) {
        new Resolver(lazyPath, project)
    }

    private final Project project

    private static class Resolver implements ResolvableExecutable {
        Resolver(final Object lazyPath, final Project project) {
            this.lazyPath = lazyPath
            this.project = project
        }

        @Override
        File getExecutable() {
            project.file(this.lazyPath)
        }

        private final Object lazyPath
        private final Project project
    }
}
