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
package org.ysb33r.grolifant.api.v4.exec

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.ysb33r.grolifant.api.errors.ExecConfigurationException
import org.ysb33r.grolifant.api.v4.AbstractCombinedProjectTaskExtension

/** Use as a base class for extensions that will wrap tools.
 *
 * <p> This base class will also enable extensions to discover whether they are inside a task or a
 * project.
 *
 * @since 0.17.0
 */
@CompileStatic
abstract class AbstractToolExtension extends AbstractCombinedProjectTaskExtension {

    /** Set the parameters for locating an exe.
     *
     * @param opts Options for locating the exe. The exact parameters are tool-specific.
     */
    void executable(final Map<String, ?> opts) {
        this.resolvableExecutable = resolver.getResolvableExecutable((Map<String, Object>) opts)
        this.resolvableExecutableType = typeResolver.getResolvableExecutableType((Map<String, Object>) opts)
    }

    /** Obtain a lazy-evaluated object to resolve a path to an exe.
     *
     * @return An object for which will resolve a path on-demand.
     */
    @SuppressWarnings('DuplicateStringLiteral')
    ResolvableExecutable getResolvableExecutable() {
        ResolvableExecutable exe = this.resolvableExecutable
        if (exe == null && task != null) {
            exe = ((AbstractToolExtension) projectExtension).resolvableExecutable
        }
        if (exe == null) {
            throw new ExecConfigurationException('Executable has not been configured')
        }
        exe
    }

    /** Describes the parameters for resolving the executable.
     *
     * @return {@link org.ysb33r.grolifant.api.v4.exec.ResolvableExecutableType} populated with values to the
     *   last call to {@link #executable}.
     *
     * @since 0.14
     */
    @SuppressWarnings('DuplicateStringLiteral')
    ResolvableExecutableType getResolvableExecutableType() {
        ResolvableExecutableType exe = this.resolvableExecutableType
        if (!exe) {
            if (task) {
                exe = ((AbstractToolExtension) projectExtension).resolvableExecutableType
            } else {
                throw new ExecConfigurationException('Executable has not been configured')
            }
        }
        exe
    }

    /** Get access to object that can resolve an executable's location from a property map.
     *
     * @return Resolver
     */
    ExternalExecutable getResolver() {
        this.registry ?: ((AbstractToolExtension) projectExtension).resolver
    }

    /** Get access to object that can resolve an executable's location type from a property map
     *
     * @return Resolver*
     * @since 0.14
     */
    ExternalExecutableType getTypeResolver() {
        this.registry ?: ((AbstractToolExtension) projectExtension).typeResolver
    }

    /** Attach this extension to a project
     *
     * @param project Project to attach to.
     */
    protected AbstractToolExtension(Project project) {
        super(project)
        this.registry = new ResolverFactoryRegistry(project)
    }

    /** Attach this extension to a task
     *
     * @param task Task to attach to.
     * @param projectExtName Name of the extension that is attached to the project.
     */
    protected AbstractToolExtension(Task task, final String projectExtName) {
        super(task, projectExtName)
    }

    /** Access to the registry of exe resolver factories.
     *
     * <p> This is used to add additional factory types i.e. for version-based resolving.
     *
     * @return Registry
     */
    protected ResolverFactoryRegistry getResolverFactoryRegistry() {
        this.registry ?: ((AbstractToolExtension) projectExtension).resolverFactoryRegistry
    }

    private final ResolverFactoryRegistry registry
    private ResolvableExecutable resolvableExecutable
    private ResolvableExecutableType resolvableExecutableType
}
