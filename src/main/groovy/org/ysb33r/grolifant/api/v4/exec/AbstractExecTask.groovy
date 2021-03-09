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
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.ysb33r.grolifant.api.v4.StringUtils
import org.ysb33r.grolifant.api.errors.ExecutionException

/** A base class to use for developing execution tasks for wrapping execution tools
 *
 * @since 0.17.0
 */
@CompileStatic
@SuppressWarnings('MethodCount')
abstract class AbstractExecTask<B extends AbstractExecTask, T extends AbstractToolExecSpec> extends DefaultTask {

    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return {@code this}.
     */
    B setIgnoreExitValue(boolean flag) {
        this.ignoreExitValue = flag
        (B) this
    }

    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return {@code this}.
     */
    B ignoreExitValue(boolean flag) {
        this.ignoreExitValue = flag
        (B) this
    }

    /** State of exit value monitoring.
     *
     * @return Whether return value should be ignored.
     */
    @Input
    boolean isIgnoreExitValue() {
        this.ignoreExitValue
    }

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return {@code this}.
     */
    B setStandardInput(InputStream inputStream) {
        this.standardInput = inputStream
        (B) this
    }

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return {@code this}.
     */
    B standardInput(InputStream inputStream) {
        setStandardInput(inputStream)
    }

    /** Where input is read from during execution.
     *
     * @return Input stream.
     */
    @Internal
    InputStream getStandardInput() {
        this.standardInput
    }

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B setStandardOutput(OutputStream outputStream) {
        this.standardOutput = outputStream
        (B) this
    }

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B standardOutput(OutputStream outputStream) {
        setStandardOutput(outputStream)
    }

    /** Where standard output is sent to during execution.
     *
     * @return Output stream.
     */
    @Internal
    OutputStream getStandardOutput() {
        this.standardOutput
    }

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B setErrorOutput(OutputStream outputStream) {
        this.errorOutput = outputStream
        (B) this
    }

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B errorOutput(OutputStream outputStream) {
        setErrorOutput(outputStream)
    }

    /** Where error output is sent to during execution.
     *
     * @return Output stream.
     */
    @Internal
    OutputStream getErrorOutput() {
        this.errorOutput
    }

    /** Obtain the working directory for this process.
     *
     * This call will evaluate the lazily-set working directory for {@link #setWorkingDir}0
     * @return A{@code java.io.File} object.
     */
    @Internal
    File getWorkingDir() {
        project.file(this.workingDir)
    }

    /** Set the working directory for the execution.
     *
     * @param workDir Any object that is convertible using Gradle's {@code project.file}.
     */
    void setWorkingDir(Object workDir) {
        this.workingDir = workDir
    }

    /** Set the working directory for the execution.
     *
     * @param workDir Any object that is convertible using Gradle's {@code project.file}.
     * @return This object as {@link org.gradle.process.ProcessForkOptions}
     */
    B workingDir(Object workDir) {
        this.workingDir = workDir
        (B) this
    }

    /** Returns the environment to be used for the process. Defaults to the environment of this process.
     *
     * @return Key-value pairing of environmental variables.
     */
    @Internal
    Map<String, Object> getEnvironment() {
        this.env
    }

    /** Set the environment variables to use for the process.
     *
     * @param map Environmental variables as key-value pairs.
     */
    void setEnvironment(Map<String, ?> map) {
        this.env.clear()
        this.env.putAll(map)
    }

    /** Add additional environment variables for use with the process.
     *
     * @param map Environmental variables as key-value pairs.
     * @return {@code this}.
     */
    B environment(Map<String, ?> map) {
        this.env.putAll(map)
        (B) this
    }

    /** Add additional environment variable for use with the process.
     *
     * @param envVar Name of environmental variable.
     * @param value Value of environmental variable.
     * @return {@code this}.
     */
    B environment(String envVar, Object value) {
        this.env.put(envVar, value)
        (B) this
    }

    /** The exe used in this specification as a String.
     *
     * @return Executable name if set (else null).
     */
    @Optional
    @Input
    String getExecutable() {
        this.exe
    }

    /** Returns the full script line, including the exe, it's specific arguments, tool specific instruction and
     * the arguments specific to the instruction.
     *
     * @return Command-line as a list of items
     */
    @Internal
    @CompileDynamic
    List<String> getCommandLine() {
        execSpec ? execSpec.commandLine : []
    }

    /** Replace the tool-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    void setExeArgs(Iterable<?> args) {
        this.args.clear()
        this.args.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Iterable<?> args) {
        this.args.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Object... args) {
        this.args.addAll(args)
    }

    /** Any arguments specific to the tool in use
     *
     * @return Arguments to the tool. Can be empty, but never null.
     */
    @Optional
    @Input
    List<String> getExeArgs() {
        StringUtils.stringize(this.args)
    }

    /** Returns the result for the execution, that was run by this task.
     *
     * @return The result of the execution. Returns null if this task has not been executed yet.
     */
    @Internal
    ExecResult getExecResult() {
        execResult
    }

    /** Runs this process against an internal execution specification. If a failure occurs and
     * {@link #isIgnoreExitValue} is not set, an exception will be raised.
     */
    @TaskAction
    @SuppressWarnings('CatchException')
    void exec() {
        this.execSpec = createExecSpec()
        configureExecSpec()
        Closure runner = { T fromSpec, ExecSpec toSpec ->
            fromSpec.copyToExecSpec(toSpec)
        }
        try {
            execResult = project.exec runner.curry(this.execSpec)
        } catch (final Exception e) {
            throw new ExecutionException('Failure in running external process', e)
        }
    }

    /** Creates class and sets default environment to be that of Gradle,
     *
     */
    protected AbstractExecTask() {
        super()
        environment.clear()
        environment.putAll(System.getenv())
    }

    /** Sets the exe to use for this task
     *
     * @param exe Anything resolvable via {@link org.ysb33r.grolifant.api.StringUtils#stringize(Object)}
     */
    protected void setToolExecutable(Object exe) {
        this.exe = exe
    }

    /** Provides access to the execution specification that is associated with this task
     *
     * @return Execution specification. Can be null if call too early.
     */
    protected T getExecSpec() {
        this.execSpec
    }

    /** Configures the executions specification from settings.
     *
     * @return The execution specification.
     * @since 0.5.1
     */
    protected T configureExecSpec() {
        this.execSpec.setIgnoreExitValue(this.ignoreExitValue)
        this.execSpec.environment(this.env)
        this.execSpec.exeArgs(exeArgs)

        if (this.standardInput != null) {
            this.execSpec.setStandardInput(this.standardInput)
        }

        if (this.standardOutput != null) {
            this.execSpec.setStandardOutput(this.standardOutput)
        }

        if (this.errorOutput != null) {
            this.execSpec.setErrorOutput(this.errorOutput)
        }

        if (this.workingDir != null) {
            this.execSpec.workingDir(this.workingDir)
        }

        if (this.exe != null) {
            this.execSpec.executable(StringUtils.stringize(this.exe))
        }

        this.execSpec
    }

    /** Factory method for creating an execution specification
     *
     * @return Execution Specification
     */
    protected abstract T createExecSpec()

    private T execSpec
    private ExecResult execResult
    private boolean ignoreExitValue = false
    private InputStream standardInput
    private OutputStream standardOutput
    private OutputStream errorOutput
    private Object workingDir
    private Object exe
    private final List<Object> args = []
    private final Map<String, Object> env = [:]

}
