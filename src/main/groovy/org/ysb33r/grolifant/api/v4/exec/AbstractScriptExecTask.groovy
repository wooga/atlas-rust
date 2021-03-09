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
abstract class AbstractScriptExecTask<T extends AbstractScriptExecSpec>
    extends AbstractExecTask<AbstractScriptExecTask<T>, T> {

    /** The script used in this specification as a String.
     *
     * @return Script
     */
    @Input
    String getScript() {
        StringUtils.stringize(this.scriptName)
    }

    /** Set the script to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.grolifant.api.StringUtils#stringize(Object)}
     */
    void setScript(Object cmd) {
        this.scriptName = cmd
    }

    /** Set the script to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.grolifant.api.StringUtils#stringize(Object)}
     */
    @SuppressWarnings('UnnecessarySetter')
    void script(Object cmd) {
        setScript(cmd)
    }

    /** Replace the script-specific arguments with a new set.
     *
     * @param args New list of script-specific arguments
     */
    void setScriptArgs(Iterable<?> args) {
        this.scriptArgs.clear()
        this.scriptArgs.addAll(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Iterable<?> args) {
        this.scriptArgs.addAll(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Object... args) {
        this.scriptArgs.addAll(args)
    }

    /** Any arguments specific to the script in use
     *
     * @return Arguments to the script. Can be empty, but never null.
     */
    @Optional
    @Input
    List<String> getScriptArgs() {
        StringUtils.stringize(this.scriptArgs)
    }

    /** Configures the executions specification from settings.
     *
     * @return The execution specification.
     * @since 0.5.1
     */
    @Override
    @SuppressWarnings(['UnneccessarySetter', 'UnnecessaryGetter'])
    protected T configureExecSpec() {
        super.configureExecSpec()
        T execSpec = getToolExecSpec()
        execSpec.script(getScript())
        execSpec.scriptArgs(getScriptArgs())
        execSpec
    }

    private T getToolExecSpec() {
        (T) (super.execSpec)
    }

    private Object scriptName
    private final List<Object> scriptArgs = []
}
