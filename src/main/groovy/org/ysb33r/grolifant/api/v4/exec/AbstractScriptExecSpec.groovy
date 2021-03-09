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
import org.ysb33r.grolifant.api.v4.StringUtils

/** Tool execution specification aimed at script-line tools which takes a script as one of the arguments.
 *
 * @since 0.17.0
 */
@CompileStatic
abstract class AbstractScriptExecSpec extends AbstractToolExecSpec {

    /** The script used in this specification as a String.
     *
     * @return Command if set (else null).
     */
    String getScript() {
        if (this.script == null) {
            null
        } else {
            StringUtils.stringize(this.script)
        }
    }

    /** Set the exe to use.
     *
     * @param cmd Anything that can be resolved via {@link StringUtils#stringize(Object)}
     */
    void setScript(Object cmd) {
        this.script = cmd
    }

    /** Set the exe to use.
     *
     * @param exe Anything that can be resolved via {@link StringUtils#stringize(Object)}
     */
    void script(Object cmd) {
        setScript(cmd)
    }

    /** Replace the script-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    @SuppressWarnings('UnnecessarySetter')
    void setScriptArgs(Iterable<?> args) {
        setInstructionArgs(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Iterable<?> args) {
        instructionArgs(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Object... args) {
        instructionArgs(args)
    }

    /** Any arguments specific to the script.
     *
     * @return Arguments to the commands. Can be empty, but never null.
     */
    List<String> getScriptArgs() {
        instructionsArgs
    }

    /** Construct class and attach it to specific project.
     *
     * @param project Project this exec spec is attached.
     * @param registry Registry used to resolve executables.
     */
    protected AbstractScriptExecSpec(Project project, ExternalExecutable registry) {
        super(project, registry)
    }

    /** A specific instruction passed to a tool.
     *
     * Instruction can be empty or null, which means that by default implementation {@link #getToolInstruction}
     * will be ignored.
     *
     * @return Instruction as string
     */
    @Override
    protected String getToolInstruction() {
        getScript()
    }

    private Object script
}
