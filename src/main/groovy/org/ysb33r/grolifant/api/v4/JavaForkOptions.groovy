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
import org.gradle.api.file.FileCollection
import org.gradle.process.ProcessForkOptions

import static org.ysb33r.grolifant.api.v4.StringUtils.stringize

/** Provides a class that can be populated with various fork options for Java
 * and which can then be used to copy to other methods in the Gradle API that provides a
 * {@link org.gradle.process.JavaForkOptions} in the parameters.
 *
 * @since 0.7
 */
@CompileStatic
@SuppressWarnings('MethodCount')
class JavaForkOptions {
    /**
     * Copies these options to the given options.
     *
     * @param options The target options.
     * @return The options that were passed in.
     */
    org.gradle.process.JavaForkOptions copyTo(org.gradle.process.JavaForkOptions options) {
        if (this.systemProperties != null) {
            options.systemProperties(this.systemProperties)
        }

        if (this.charEncoding != null) {
            options.defaultCharacterEncoding = this.charEncoding
        }

        if (this.minHeapSize != null) {
            options.minHeapSize = this.minHeapSize
        }

        if (this.maxHeapSize != null) {
            options.maxHeapSize = this.maxHeapSize
        }

        if (this.jvmArgs != null) {
            options.jvmArgs(this.jvmArgs)
        }

        if (this.bootstrap != null) {
            options.bootstrapClasspath = bootstrap
        }

        if (this.additionalBootstrap != null) {
            options.bootstrapClasspath(additionalBootstrap.toArray())
        }

        if (this.assertions != null) {
            options.enableAssertions = this.assertions
        }

        if (this.debug != null) {
            options.enableAssertions = this.debug
        }

        copyTo((ProcessForkOptions) options)

        options
    }

    /**
     * Copies these options to the given target options.
     *
     * @param options The target options
     * @return The options that were passed in
     */
    ProcessForkOptions copyTo(ProcessForkOptions options) {
        if (this.exe != null) {
            options.executable = this.exe
        }

        if (this.workingDir != null) {
            options.workingDir = this.workingDir
        }

        if (this.env != null) {
            options.environment(this.env)
        }

        options
    }

    /**
     * Returns the system properties which will be used for the process.
     *
     * @return The system properties. Returns an empty map when there are no system properties.
     */

    Map<String, Object> getSystemProperties() {
        (Map<String, Object>) (this.systemProperties ?: [:])
    }

    /**
     * Sets the system properties to use for the process.
     *
     * @param props The system properties. Must not be null.
     */

    void setSystemProperties(Map<String, ?> props) {
        this.systemProperties = [:]
        this.systemProperties.putAll(props)
    }

    /**
     * Adds some system properties to use for the process.
     *
     * @param props The system properties. Must not be null.
     * @return this
     */

    JavaForkOptions systemProperties(Map<String, ?> props) {
        if (this.systemProperties == null) {
            this.systemProperties = [:]
        }
        this.systemProperties.putAll(props)
        this
    }

    /**
     * Adds a system property to use for the process.
     *
     * @param name The name of the property
     * @param value The value for the property. May be null.
     * @return this
     */

    JavaForkOptions systemProperty(String name, Object value) {
        if (this.systemProperties == null) {
            this.systemProperties = [:]
        }
        this.systemProperties.put(name, value)
        this
    }

    /**
     * Returns the default character encoding to use.
     *
     * @return The default character encoding. Returns null if the {@link java.nio.charset.Charset#defaultCharset()
     *   default character encoding of this JVM} should be used.
     */

    String getDefaultCharacterEncoding() {
        this.charEncoding
    }

    /**
     * Sets the default character encoding to use.
     *
     * Note: Many JVM implementations support the setting of this attribute via system property on startup
     * (namely, the {@code file.encoding} property). For JVMs where this is the case, setting the {@code file.encoding}
     * property via {@link #setSystemProperties(java.util.Map)} or similar will have no effect as this value will be
     * overridden by the value specified by {@link #getDefaultCharacterEncoding()}.
     *
     * @param defaultCharacterEncoding The default character encoding. Use {@code null}
     * {@link java.nio.charset.Charset#defaultCharset()} to use this JVM's default charset.
     */

    void setDefaultCharacterEncoding(String defaultCharacterEncoding) {
        this.charEncoding = defaultCharacterEncoding
    }

    /**
     * Returns the minimum heap size for the process, if any.
     *
     * @return The minimum heap size. Returns null if the default minimum heap size should be used.
     */

    String getMinHeapSize() {
        this.minHeapSize
    }

    /**
     * Sets the minimum heap size for the process.
     *
     * @param heapSize The minimum heap size. Use null for the default minimum heap size.
     */

    void setMinHeapSize(String heapSize) {
        this.minHeapSize = heapSize
    }

    /**
     * Returns the maximum heap size for the process, if any.
     *
     * @return The maximum heap size. Returns null if the default maximum heap size should be used.
     */

    String getMaxHeapSize() {
        this.maxHeapSize
    }

    /**
     * Sets the maximum heap size for the process.
     *
     * @param heapSize The heap size. Use null for the default maximum heap size.
     */

    void setMaxHeapSize(String heapSize) {
        this.maxHeapSize = heapSize
    }

    /**
     * Returns the extra arguments to use to launch the JVM for the process. Does not include system properties and the
     * minimum/maximum heap size.
     *
     * @return The arguments. Returns an empty list if there are no arguments.
     */

    List<String> getJvmArgs() {
        (List<String>) (this.jvmArgs ? stringize(this.jvmArgs) : [])
    }

    /**
     * Sets the extra arguments to use to launch the JVM for the process. System properties
     * and minimum/maximum heap size are updated.
     *
     * @param arguments The arguments. Must not be null.
     */

    void setJvmArgs(Iterable<?> arguments) {
        this.jvmArgs = []
        this.jvmArgs.addAll(arguments)
    }

    /**
     * Adds some arguments to use to launch the JVM for the process.
     *
     * @param arguments The arguments. Must not be null.
     * @return this
     */

    JavaForkOptions jvmArgs(Iterable<?> arguments) {
        if (this.jvmArgs == null) {
            setJvmArgs(arguments)
        } else {
            this.jvmArgs.addAll(arguments)
        }
        this
    }

    /**
     * Adds some arguments to use to launch the JVM for the process.
     *
     * @param arguments The arguments.
     * @return this
     */

    JavaForkOptions jvmArgs(Object... arguments) {
        if (this.jvmArgs == null) {
            setJvmArgs(arguments as List)
        } else {
            this.jvmArgs.addAll(arguments)
        }
        this
    }

    /**
     * Returns the bootstrap classpath to use for the process. The default bootstrap classpath for the JVM is used when
     * this classpath is empty.
     *
     * @return The bootstrap classpath. Never returns null.
     */

    FileCollection getBootstrapClasspath() {
        this.bootstrap
    }

    /**
     * Sets the bootstrap classpath to use for the process. Set to an empty classpath to use the default bootstrap
     * classpath for the specified JVM.
     *
     * @param classpath The classpath. Must not be null. Can be empty.
     */

    void setBootstrapClasspath(FileCollection classpath) {
        this.bootstrap = classpath
    }

    /**
     * Adds the given values to the end of the bootstrap classpath for the process.
     *
     * @param classpath The classpath.
     * @return this
     */

    JavaForkOptions bootstrapClasspath(Object... classpath) {
        if (this.additionalBootstrap == null) {
            this.additionalBootstrap = []
        }
        this.additionalBootstrap.addAll(classpath)
        this
    }

    /**
     * Returns true if assertions are enabled for the process.
     *
     * @return true if assertions are enabled, false if disabled
     */

    boolean getEnableAssertions() {
        this.assertions != null ?: false
    }

    /**
     * Enable or disable assertions for the process.
     *
     * @param enabled true to enable assertions, false to disable.
     */

    void setEnableAssertions(boolean enabled) {
        this.assertions = enabled
    }

    /**
     * Returns true if debugging is enabled for the process. When enabled, the process is started suspended and
     * listening on port 5005.
     *
     * @return true when debugging is enabled, false to disable.
     */

    boolean getDebug() {
        this.debug != null ?: false
    }

    /**
     * Enable or disable debugging for the process. When enabled, the process is started suspended and listening on port
     * 5005.
     *
     * @param enabled true to enable debugging, false to disable.
     */

    void setDebug(boolean enabled) {
        this.debug = enabled
    }

    /**
     * Returns the name of the exe to use.
     *
     * @return The exe.
     */

    String getExecutable() {
        this.exe ? stringize(this.exe) : null
    }

    /**
     * Sets the name of the exe to use.
     *
     * @param executable The exe. Must not be null.
     */

    void setExecutable(Object executable) {
        this.exe = executable
    }

    /**
     * Sets the name of the exe to use.
     *
     * @param executable The exe. Must not be null.
     */

    void executable(Object executable) {
        this.exe = executable
    }

    /**
     * Returns the working directory for the process. Defaults to the project directory.
     *
     * @return The working directory.
     */

    Object getWorkingDir() {
        this.workingDir
    }

    /**
     * Sets the working directory for the process. The supplied argument is evaluated as per {@link
     * org.gradle.api.Project # file ( Object )}.
     *
     * @param dir The working directory. Must not be null.
     */

    void setWorkingDir(Object dir) {
        this.workingDir = dir
    }

    /**
     * Sets the working directory for the process. The supplied argument is evaluated as per {@link
     * org.gradle.api.Project # file ( Object )}.
     *
     * @param dir The working directory. Must not be null.
     */

    void workingDir(Object dir) {
        this.workingDir = dir
    }

    /**
     * The environment variables to use for the process. Defaults to the environment of this process.
     *
     * @return The environment. Returns an empty map when there are no environment variables.
     */

    Map<String, Object> getEnvironment() {
        (Map<String, Object>) (this.env ?: [:])
    }

    /**
     * Sets the environment variable to use for the process.
     *
     * @param environmentVariables The environment variables. Must not be null.
     */
    void setEnvironment(Map<String, ?> environmentVariables) {
        this.env = [:]
        this.env.putAll(environmentVariables)
    }

    /**
     * Adds some environment variables to the environment for this process.
     *
     * @param environmentVariables The environment variables. Must not be null.
     */
    void environment(Map<String, ?> environmentVariables) {
        if (this.env == null) {
            environment = environmentVariables
        } else {
            this.env.putAll(environmentVariables)
        }
        this
    }

    /**
     * Adds an environment variable to the environment for this process.
     *
     * @param name The name of the variable.
     * @param value The value for the variable. Must not be null.
     */
    void environment(String name, Object value) {
        if (this.env == null) {
            this.env = [:]
        }
        this.env.put(name, value)
    }

    private Map<String, Object> systemProperties
    private Map<String, Object> env
    private List<Object> jvmArgs
    private FileCollection bootstrap
    private List<Object> additionalBootstrap
    private Object exe
    private Object workingDir
    private String charEncoding
    private String minHeapSize
    private String maxHeapSize
    private Boolean assertions
    private Boolean debug
}
