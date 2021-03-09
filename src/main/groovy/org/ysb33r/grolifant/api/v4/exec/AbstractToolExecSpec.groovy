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
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.process.ExecSpec
import org.gradle.process.ProcessForkOptions
import org.ysb33r.grolifant.api.errors.ExecConfigurationException
import org.ysb33r.grolifant.api.errors.ExecutionException
import org.ysb33r.grolifant.api.v4.ClosureUtils
import org.ysb33r.grolifant.api.v4.StringUtils

/** A base class to aid plugin developers create their own {@link org.gradle.process.ExecSpec} implementations.
 *
 * @since 0.17.0
 */
@CompileStatic
abstract class AbstractToolExecSpec extends AbstractExecSpec implements ToolExecSpecGroovy {

    /** The exe used in this specification as a String.
     *
     * @return Executable name if set (else null).
     */
    @Override
    String getExecutable() {
        if (this.executable == null) {
            null
        } else {
            this.executable.getExecutable().toString()
        }
    }

    /** Set the exe to use.
     *
     * If you need to search the system path, to find the exe use the {@code search : 'exeName'} form instead.
     *
     * @param exe Anything that can be resolved via {@link org.ysb33r.grolifant.api.v4.StringUtils#stringize(Object)}
     * or an implementation of {@link org.ysb33r.grolifant.api.v4.exec.ResolvableExecutable}
     */
    @Override
    void setExecutable(Object exe) {
        setExecutable([path: exe])
    }

    /** Set the exe to use.
     *
     * @param exe An implementation of {@link ResolvableExecutable}
     */
    @Override
    void setExecutable(ResolvableExecutable resolver) {
        this.executable = resolver
    }

    /** Set the exe to use.
     *
     * <p> This variant of the method has been introduced to cope with the API change in Gradle 4.0.
     *
     * @param exe Executable as String representation
     */
    @Override
    void setExecutable(String exe) {
        setExecutable([path: (Object) exe])
    }

    /** Set the exe to use.
     *
     * If you need to search the system path, to find the exe use the {@code search : 'exeName'} form instead.
     *
     * @param exe Anything that can be resolved via {@link StringUtils#stringize(Object)}
     *   or an implementation of {@link ResolvableExecutable}
     * @return This object as an instance of {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions executable(Object exe) {
        setExecutable(exe)
        this
    }

    /** Set the exe to use.
     *
     * @param exe An implementation of {@link ResolvableExecutable}
     * @return This object as an instance of {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions executable(ResolvableExecutable resolver) {
        this.executable = resolver
        this
    }

    /** Use a key-value approach to setting the exe.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys.
     * Implementations should use {@link ResolverFactoryRegistry} to add more keys.
     *
     * @param exe Key-value setting exe (with optional extra keys)
     */
    @Override
    void setExecutable(Map<String, Object> exe) {
        this.executable = executableResolver.getResolvableExecutable(exe)
    }

    /** Use a key-value approach to setting the exe.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys.
     * Implementations should use {@link ResolverFactoryRegistry} to add more keys.
     *
     * @param exe Key-value setting exe (with optional extra keys)
     */
    @Override
    void executable(Map<String, Object> exe) {
        setExecutable(exe)
    }

    /** Lazy-evaluated version of the exe
     *
     * @return Lazy-evaluated version of the exe or {@code null} if not configured.
     */
    @Override
    ResolvableExecutable getResolvableExecutable() {
        this.executable
    }

    /** Returns the full script line, including the exe, it's specific arguments, tool specific instruction and
     * the arguments specific to the instruction.
     *
     * @return Command-line as a list of items
     */
    @Override
    List<String> getCommandLine() {
        buildCommandLine()
    }

    /** Configure this spec from an {@link org.gradle.api.Action}
     *
     * @param action Configuration action.
     * @return {@code this}.
     */
    @Override
    AbstractToolExecSpec configure(Action<? extends AbstractToolExecSpec> action) {
        action.execute(this)
        this
    }

    /** Configure this spec from a closure.
     *
     * @param cfg Closure to use.
     * @return
     */
    @Override
    AbstractToolExecSpec configure(@DelegatesTo(AbstractToolExecSpec) Closure cfg) {
        ClosureUtils.configureItem(this, cfg)
        this
    }

    /** Replace the tool-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    @Override
    void setExeArgs(Iterable<?> args) {
        exeArgs.clear()
        exeArgs.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    @Override
    void exeArgs(Iterable<?> args) {
        exeArgs.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    @Override
    void exeArgs(Object... args) {
        exeArgs.addAll(args)
    }

    /** Any arguments specific to the tool in use
     *
     * @return Arguments to the tool. Can be empty, but never null.
     */
    @Override
    List<String> getExeArgs() {
        StringUtils.stringize(this.exeArgs)
    }

    /** Copies options from this Spec to the given target.
     *
     * If the target is not an instance of {@link AbstractToolExecSpec} and
     * the exe is of type {@link ResolvableExecutable} then it will be processed before
     * copying.
     *
     * @param processForkOptions Copy to this target.
     * @return This object as a {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    @SuppressWarnings('UnnecessarySetter')
    ProcessForkOptions copyTo(ProcessForkOptions processForkOptions) {
        processForkOptions.environment = this.env
        processForkOptions.setWorkingDir((Object) this.workingDir) // cast required for Gradle < 4.0

        if (!(processForkOptions instanceof AbstractToolExecSpec) && this.executable instanceof ResolvableExecutable) {
            processForkOptions.setExecutable(((ResolvableExecutable) (this.executable)).getExecutable())
        } else {
            processForkOptions.setExecutable(this.executable)
        }
        this
    }

    /** Copies settings from this execution specification to a standard {@link org.gradle.process.ExecSpec}
     *
     * This method is intended to be called as late as possible by a project extension or a task
     *   which would want to delegate to {@code project.exec} project extension. It will cause arguments
     *   to be evaluated. The only items not immediately evaluated are {@code workingDir} and {@code exe}.
     *
     * @param execSpec Exec spec to configure.
     */
    @Override
    void copyToExecSpec(ExecSpec execSpec) {
        copyTo(execSpec)

        execSpec.errorOutput = errorOutput
        execSpec.standardOutput = standardOutput
        execSpec.standardInput = standardInput
        execSpec.ignoreExitValue = ignoreExitValue

        setArgsOnExecSpec(execSpec, buildCommandLine().drop(1))
    }

    /** Construct class and attach it to specific project.
     *
     * @param project Project this exec spec is attached.
     * @param executableResolver A way to resolve executables.
     */
    protected AbstractToolExecSpec(Project project, ExternalExecutable executableResolver) {
        super(project)
        if (executableResolver == null) {
            throw new ExecConfigurationException('Cannot create an execution specification without an exe resolver')
        }
        this.executableResolver = executableResolver
    }

    /** Builds up the command line.
     *
     * <p> The default format in use is the following:
     *
     * <pre>
     *   exe exeArgs... toolInstruction instructionsArgs...
     * </pre>
     *
     * <p> {@code toolInstruction} is typically a command or a script and
     * {@code instructionArgs} the arguments for that script of tool. {@code exeArgs} are any
     *   command-line arguments meant for the exe which do not form part of the script/command
     *   customisation.
     *
     * @return List of command line parts
     * @throw {@code GradleException} if exe is not set.
     */
    protected List<String> buildCommandLine() {
        List<String> parts = []

        String exe = getExecutable()

        if (exe == null) {
            throw new ExecutionException('''The 'exe' part cannot be null.''')
        }

        parts.add exe
        parts.addAll getExeArgs()

        String instruction = toolInstruction

        if (instruction != null && !instruction.empty) {
            parts.add instruction
            parts.addAll instructionsArgs
        }

        parts
    }

    /** Replace the instruction-specific arguments with a new set.
     *
     * @param args New list of instruction-specific arguments
     */
    protected void setInstructionArgs(Iterable<?> args) {
        instructionArgs.clear()
        instructionArgs.addAll(args)
    }

    /** Add more instruction-specific arguments.
     *
     * @param args Additional list of arguments
     */
    protected void instructionArgs(Iterable<?> args) {
        instructionArgs.addAll(args)
    }

    /** Add more instruction-specific arguments.
     *
     * @param args Additional list of arguments
     */
    protected void instructionArgs(Object... args) {
        instructionArgs.addAll(args)
    }

    /** List of arguments specific to the tool instruction.
     *
     * @return List of arguments. Can be empty, but not null.
     */
    protected List<String> getInstructionsArgs() {
        StringUtils.stringize(this.instructionArgs)
    }

    /** A specific instruction passed to a tool.
     *
     * * Instruction can be empty or null. In the default implementation {@link #getToolInstruction} will be ignored.
     *
     * @return Instruction as string
     */
    protected String getToolInstruction() {
        null
    }

    /** Access to the object that can resolve exe location from property maps.
     *
     * @return Resolver.
     */
    protected org.ysb33r.grolifant.api.v4.exec.ExternalExecutable getExecutableResolver() {
        this.executableResolver
    }

    private void setArgsOnExecSpec(final ExecSpec execSpec, final List<String> args) {
        execSpec.args = args
    }

    private org.ysb33r.grolifant.api.v4.exec.ResolvableExecutable executable
    private final List<Object> exeArgs = []
    private final List<Object> instructionArgs = []

    private final org.ysb33r.grolifant.api.v4.exec.ExternalExecutable executableResolver
}
