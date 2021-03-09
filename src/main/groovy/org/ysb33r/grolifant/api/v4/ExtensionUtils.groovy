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
package org.ysb33r.grolifant.api.v4

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.ysb33r.grolifant.api.errors.ConfigurationException
import org.ysb33r.grolifant.api.v4.exec.AbstractToolExecSpec
import org.ysb33r.grolifant.api.v4.exec.ExecSpecInstantiator
import org.ysb33r.grolifant.internal.v4.execspec.ExecProjectExtension

/** Utilities to deal with Gradle extensions and project extensions
 *
 * @since 0.3
 */
@CompileStatic
class ExtensionUtils {

    /** Adds a project extension so that specific tools can be execute in a similar manner to
     * {@link org.gradle.api.Project#exec}.
     *
     * @param name Name of extension.
     * @param project Project to attach to.
     * @param instantiator Instantiator to use to create new execution specifications.
     */
    static void addExecProjectExtension(
        final String name, Project project, ExecSpecInstantiator<? extends AbstractToolExecSpec> instantiator) {
        final ExecProjectExtension delegator = new ExecProjectExtension(project, instantiator)
        project.extensions.extraProperties.set(name) { def cfg ->
            switch (cfg) {
                case Closure:
                    delegator.call((Closure) cfg)
                    break
                case Action:
                    delegator.call((Action) cfg)
                    break
                case AbstractToolExecSpec:
                    delegator.execute((AbstractToolExecSpec) cfg)
                    break
                default:
                    throw new ConfigurationException('Invalid type passed. Use closure or actions.')
            }
        }
    }

    /** Binds a service object to the extensions object on an existing Gradle DSL Object
     *
     * @param dslObject Gradle DSL object to bind to. (Must implement {@link ExtensionAware}).
     * @param serviceName Name of service
     * @param service Object delivering the service
     * @since 0.10
     */
    static void bindService(final Object dslObject, final String serviceName, final Object service) {
        ExtensionAware.cast(dslObject).extensions.add(serviceName, service)
    }

    /** Binds a new extension handler on to the project dependency handler.
     *
     * @param project Project that holds the dependency handler.
     * @param serviceName Name of service to be bound to dependency handler.
     * @param service Object providing the service.
     * @since 0.10
     */
    static void bindDependencyHandlerService(final Project project, final String serviceName, final Object service) {
        bindService(project.dependencies, serviceName, service)
    }

    /** Binds a new extension handler on to the project dependency handler.
     *
     * @param project Project that holds the dependency handler.
     * @param serviceName Name of service to be bound to dependency handler.
     * @param service Class type providing the service. The class requires a constructor which takes a
     * {@link Project} as parameter.
     * @since 0.10
     */
    static void bindDependencyHandlerService(final Project project, final String serviceName, final Class service) {
        bindService(project.dependencies, serviceName, service.newInstance(project))
    }

    /** Binds a new extension handler on to the project repository handler.
     *
     * @param project Project that holds the repository handler.
     * @param serviceName Name of service to be bound to repository handler.
     * @param service Object providing the service.
     * @since 0.10
     */
    static void bindRepositoryHandlerService(final Project project, final String serviceName, final Object service) {
        bindService(project.repositories, serviceName, service)
    }

    /** Binds a new extension handler on to the project repository handler.
     *
     * @param project Project that holds the repository handler.
     * @param serviceName Name of service to be bound to repository handler.
     * @param service Class type providing the service. The vclass requires a constructor which takes a {@link Project}
     *   as parameter.
     * @since 0.10
     */
    static void bindRepositoryHandlerService(final Project project, final String serviceName, final Class service) {
        bindService(project.repositories, serviceName, service.newInstance(project))
    }
}
