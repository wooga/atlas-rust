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

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.ysb33r.grolifant.api.v4.MapUtils

/** Base task class to wrap external tool executions without exposing command-line parameters directly.
 *
 *  <p> {@code T} is the execution specification class
 *
 *  <p> {@code E} is the extension class type that allows for configuration of the external tool.
 *
 * @since 0.17.0
 */
@CompileStatic
abstract class AbstractExecWrapperTask<T extends AbstractExecSpec, E extends AbstractToolExtension>
    extends DefaultTask {

    /** Replace current environment with new one.
     *
     * @param args New environment key-value map of properties.
     */
    void setEnvironment(Map<String, ?> args) {
        this.env.clear()
        this.env.putAll((Map<String, Object>) args)
    }

    /** Environment for running the exe
     *
     * <p> Calling this will resolve all lazy-values in the variable map.
     *
     * @return Map of environmental variables that will be passed.
     */
    @Input
    Map<String, String> getEnvironment() {
        MapUtils.stringizeValues(this.env)
    }

    /** Add environmental variables to be passed to the exe.
     *
     * @param args Environmental variable key-value map.
     */
    void environment(Map<String, ?> args) {
        this.env.putAll((Map<String, Object>) args)
    }

    /** The default implementation will build an execution specification
     * and run it.
     *
     */
    @TaskAction
    void exec() {
        T execSpec = createExecSpec()
        addExecutableToExecSpec(execSpec)

        configureExecSpec(execSpec)
        runExecSpec(execSpec)
    }

    protected AbstractExecWrapperTask() {
        super()
    }

    /** Adds the exe to the execution specification.
     *
     * @param execSpec
     * @return execSpec
     */
    protected T addExecutableToExecSpec(final T execSpec) {
        execSpec.executable(toolExtension.resolvableExecutable.executable.absolutePath)
        execSpec
    }

    /** Adds the configured environment to the execution specification.
     *
     * @param execSpec
     * @return execSpec
     */
    protected T addEnvironmentToExecSpec(final T execSpec) {
        execSpec.environment(environment)
        execSpec
    }

    /** Creates a new execution specification.
     *
     * @return New exec specification
     */
    abstract protected T createExecSpec()

    /** Configures an execution specification from task properties
     *
     * @param execSpec
     * @return
     */
    abstract protected T configureExecSpec(final T execSpec)

    /** Returns the extension that is associated with the specific tool
     *
     * @return Extension (project or task).
     */
    abstract protected E getToolExtension()

    @CompileDynamic
    protected ExecResult runExecSpec(final T execSpec) {
        Closure runner = { T fromSpec, ExecSpec toSpec ->
            fromSpec.copyToExecSpec(toSpec)
        }
        project.exec runner.curry(execSpec)
    }

    private final Map<String, Object> env = [:]

}
