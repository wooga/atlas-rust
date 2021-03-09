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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.ysb33r.grolifant.api.v4.StringUtils

/** A abstract task type for executing binaries that take a command as well as a set of command arguments.
 *
 * @since 0.3 (Moved to {@code org.ysb33r.grolifant.api.v4.exec} in 0.17.0)
 */
@CompileStatic
@SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract class AbstractCommandExecTask<T extends AbstractCommandExecSpec>
    extends AbstractExecTask<AbstractCommandExecTask<T>, T> {

    /** The command used in this specification as a String.
     *
     * @return Command
     */
    @Input
    String getCommand() {
        StringUtils.stringize(this.commandName)
    }

    /** Set the command to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.grolifant.api.StringUtils#stringize(Object)}
     */
    void setCommand(Object cmd) {
        this.commandName = cmd
    }

    /** Set the command to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.grolifant.api.StringUtils#stringize(Object)}
     */
    @SuppressWarnings('UnnecessarySetter')
    void command(Object cmd) {
        setCommand(cmd)
    }

    /** Replace the command-specific arguments with a new set.
     *
     * @param args New list of command-specific arguments
     */
    void setCmdArgs(Iterable<?> args) {
        this.commandArgs.clear()
        this.commandArgs.addAll(args)
    }

    /** Add more command-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void cmdArgs(Iterable<?> args) {
        this.commandArgs.addAll(args)
    }

    /** Add more command-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void cmdArgs(Object... args) {
        this.commandArgs.addAll(args)
    }

    /** Any arguments specific to the command in use
     *
     * @return Arguments to the command. Can be empty, but never null.
     */
    @Optional
    @Input
    List<String> getCmdArgs() {
        StringUtils.stringize(this.commandArgs)
    }

    /** Configures the executions specification from settings.
     *
     * @return The execution specification.
     * @since 0.5.1
     */
    @Override
    protected T configureExecSpec() {
        super.configureExecSpec()
        T execSpec = toolExecSpec
        execSpec.command(command)
        execSpec.cmdArgs(cmdArgs)
        execSpec
    }

    /** Execution specification customised for the specific tool
     *
     * @return Execution specification cast to the appropriate type.
     */
    protected T getToolExecSpec() {
        (T) (super.execSpec)
    }

    private Object commandName
    private final List<Object> commandArgs = []

}
