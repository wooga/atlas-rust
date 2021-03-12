/*
 * Copyright 2018-2020 Wooga GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wooga.gradle.rust.tasks

import spock.lang.Unroll
import wooga.gradle.rust.RustIntegrationSpec

abstract class AbstractCargoTaskIntegrationSpec extends RustIntegrationSpec {

    abstract String getTestTaskName()

    abstract Class getTaskType()

    abstract String getWorkingRustTaskConfig()

    @Unroll("can set property #property with #method and type #type")
    def "can set property #property with #method and type #type base"() {
        given: "a custom cargo task"
        buildFile << """
            task("${testTaskName}", type: ${taskType.name})
        """.stripIndent()

        and: "a task to read back the value"
        buildFile << """
            task("readValue") {
                doLast {
                    println("property: " + ${testTaskName}.${property}.get())
                }
            }
        """.stripIndent()

        and: "a set property"
        buildFile << """
            ${testTaskName}.${invocation}
        """.stripIndent()

        when:
        def result = runTasksSuccessfully("readValue")

        then:
        outputContains(result, "property: " + testValue.toString())

        where:
        property     | method           | rawValue                              | expectedValue | type
        "logFile"    | "logFile"        | osPath("/some/path/test1.log")        | _             | "File"
        "logFile"    | "logFile"        | osPath("/some/path/test2.log")        | _             | "Provider<RegularFile>"
        "logFile"    | "logFile.set"    | osPath("/some/path/test3.log")        | _             | "File"
        "logFile"    | "logFile.set"    | osPath("/some/path/test4.log")        | _             | "Provider<RegularFile>"
        "logFile"    | "setLogFile"     | osPath("/some/path/test5.log")        | _             | "File"
        "logFile"    | "setLogFile"     | osPath("/some/path/test6.log")        | _             | "Provider<RegularFile>"

        "manifest"   | "manifest"       | osPath("/some/path/test1/Cargo.toml") | _             | "File"
        "manifest"   | "manifest"       | osPath("/some/path/test2/Cargo.toml") | _             | "Provider<RegularFile>"
        "manifest"   | "manifest.set"   | osPath("/some/path/test3/Cargo.toml") | _             | "File"
        "manifest"   | "manifest.set"   | osPath("/some/path/test4/Cargo.toml") | _             | "Provider<RegularFile>"
        "manifest"   | "setManifest"    | osPath("/some/path/test5/Cargo.toml") | _             | "File"
        "manifest"   | "setManifest"    | osPath("/some/path/test6/Cargo.toml") | _             | "Provider<RegularFile>"

        "cargoPath"  | "cargoPath"      | osPath("/some/path/test1/cargo")      | _             | "File"
        "cargoPath"  | "cargoPath"      | osPath("/some/path/test2/cargo")      | _             | "Provider<RegularFile>"
        "cargoPath"  | "cargoPath.set"  | osPath("/some/path/test3/cargo")      | _             | "File"
        "cargoPath"  | "cargoPath.set"  | osPath("/some/path/test4/cargo")      | _             | "Provider<RegularFile>"
        "cargoPath"  | "setCargoPath"   | osPath("/some/path/test5/cargo")      | _             | "File"
        "cargoPath"  | "setCargoPath"   | osPath("/some/path/test6/cargo")      | _             | "Provider<RegularFile>"

        "workingDir" | "workingDir"     | osPath("/some/path/test1")            | _             | "File"
        "workingDir" | "workingDir"     | osPath("/some/path/test2")            | _             | "Provider<Directory>"
        "workingDir" | "workingDir.set" | osPath("/some/path/test3")            | _             | "File"
        "workingDir" | "workingDir.set" | osPath("/some/path/test4")            | _             | "Provider<Directory>"
        "workingDir" | "setWorkingDir"  | osPath("/some/path/test5")            | _             | "File"
        "workingDir" | "setWorkingDir"  | osPath("/some/path/test6")            | _             | "Provider<Directory>"

        "jobs"       | "jobs"           | 1                                     | _             | "Integer"
        "jobs"       | "jobs"           | 2                                     | _             | "Provider<Integer>"
        "jobs"       | "jobs.set"       | 3                                     | _             | "Integer"
        "jobs"       | "jobs.set"       | 4                                     | _             | "Provider<Integer>"
        "jobs"       | "setJobs"        | 5                                     | _             | "Integer"
        "jobs"       | "setJobs"        | 6                                     | _             | "Provider<Integer>"

        "target"     | "target"         | "aarch64-unknown-linux-gnu"           | _             | "String"
        "target"     | "target"         | "i686-pc-windows-gnu"                 | _             | "Provider<String>"
        "target"     | "target.set"     | "i686-pc-windows-msvc"                | _             | "String"
        "target"     | "target.set"     | "i686-unknown-linux-gnu"              | _             | "Provider<String>"
        "target"     | "setTarget"      | "x86_64-pc-windows-gnu"               | _             | "String"
        "target"     | "setTarget"      | "x86_64-unknown-linux-gnu"            | _             | "Provider<String>"

        "release"    | "release"        | true                                  | _             | "Boolean"
        "release"    | "release"        | false                                 | _             | "Provider<Boolean>"
        "release"    | "release.set"    | true                                  | _             | "Boolean"
        "release"    | "release.set"    | false                                 | _             | "Provider<Boolean>"
        "release"    | "setRelease"     | true                                  | _             | "Boolean"
        "release"    | "setRelease"     | false                                 | _             | "Provider<Boolean>"


        value = wrapValueBasedOnType(rawValue, type)
        invocation = (method == _) ? "${property} = ${value}" : "${method}(${value})"
        testValue = (expectedValue == _) ? rawValue : expectedValue
    }

    @Unroll("property #property sets flag #expectedCommandlineFlag")
    def "constructs build arguments"() {
        given:
        buildFile << workingRustTaskConfig

        and: "a task to read the build arguments"
        buildFile << """
            task("readValue") {
                doLast {
                    println("arguments: " + ${testTaskName}.buildArguments.get().join(" "))
                }
            }
        """.stripIndent()

        and: "a set property"
        buildFile << """
            ${testTaskName}.${method}($value)
        """.stripIndent()

        when:
        def result = runTasksSuccessfully("readValue")

        then:
        outputContains(result, expectedCommandlineFlag)

        where:
        property       | method             | rawValue                              | type           | expectedCommandlineFlag
        "manifest"     | "manifest.set"     | osPath("/some/path/test1/Cargo.toml") | "File"         | "--manifest-path ${rawValue}"
        "jobs"         | "jobs.set"         | 22                                    | "Integer"      | "--jobs ${rawValue}"
        "target"       | "target.set"       | "i686-pc-windows-gnu"                 | "String"       | "--target ${rawValue}"
        "release"      | "release.set"      | true                                  | "Boolean"      | "--release"
        "nightlyFlags" | "nightlyFlags.set" | ["unstable-options", "timings"]       | "List<String>" | "-Z ${rawValue[0]} -Z ${rawValue[1]}"
        value = wrapValueBasedOnType(rawValue, type)
    }

    @Unroll
    def "can configure #property with #method #message"() {
        given: "a custom archive task"
        buildFile << """
            ${workingRustTaskConfig}
            ${testTaskName} {
                buildArguments(["--test", "value"])
                nightlyFlags(["unstable-options", "timings"])
            }
        """.stripIndent()

        and: "a task to read back the value"
        buildFile << """
            task("readValue") {
                doLast {
                    println("property: " + ${testTaskName}.${property}.get())
                }
            }
        """.stripIndent()

        and: "a set property"
        buildFile << """
            ${testTaskName}.${method}($value)
        """.stripIndent()

        when:
        def result = runTasksSuccessfully("readValue")

        then:
        outputContains(result, "property: " + expectedValue.toString())

        where:
        property                   | method                         | rawValue         | type                      | append | expectedValue
        "additionalBuildArguments" | "buildArgument"                | "--foo"          | "String"                  | true   | ["--test", "value", "--foo"]
        "additionalBuildArguments" | "buildArguments"               | "--foo"          | "String"                  | true   | ["--test", "value", "--foo"]
        "additionalBuildArguments" | "buildArguments"               | ["--foo", "bar"] | "List<String>"            | true   | ["--test", "value", "--foo", "bar"]
        "additionalBuildArguments" | "buildArguments"               | ["--foo", "bar"] | "String[]"                | true   | ["--test", "value", "--foo", "bar"]
        "additionalBuildArguments" | "setAdditionalBuildArguments"  | ["--foo", "bar"] | "List<String>"            | false  | ["--foo", "bar"]
        "additionalBuildArguments" | "setAdditionalBuildArguments"  | ["--foo", "bar"] | "Provider<List<String>>"  | false  | ["--foo", "bar"]
        "additionalBuildArguments" | "additionalBuildArguments.set" | ["--foo", "bar"] | "List<String>"            | false  | ["--foo", "bar"]
        "additionalBuildArguments" | "additionalBuildArguments.set" | ["--foo", "bar"] | "Provider<List<String>>>" | false  | ["--foo", "bar"]
        "nightlyFlags"             | "nightlyFlag"                  | "avoid-dev-deps" | "String"                  | true   | ["unstable-options", "timings", "avoid-dev-deps"]
        "nightlyFlags"             | "nightlyFlags"                 | "avoid-dev-deps" | "String"                  | true   | ["unstable-options", "timings", "avoid-dev-deps"]
        "nightlyFlags"             | "nightlyFlags"                 | ["foo", 'bar']   | "List<String>"            | true   | ["unstable-options", "timings", "foo", "bar"]
        "nightlyFlags"             | "nightlyFlags"                 | ["foo", 'bar']   | "String[]"                | true   | ["unstable-options", "timings", "foo", "bar"]
        "nightlyFlags"             | "setNightlyFlags"              | ["foo", "bar"]   | "List<String>"            | false  | ["foo", "bar"]
        "nightlyFlags"             | "setNightlyFlags"              | ["foo", "bar"]   | "Provider<List<String>>"  | false  | ["foo", "bar"]
        "nightlyFlags"             | "nightlyFlags.set"             | ["foo", "bar"]   | "List<String>"            | false  | ["foo", "bar"]
        "nightlyFlags"             | "nightlyFlags.set"             | ["foo", "bar"]   | "Provider<List<String>>>" | false  | ["foo", "bar"]

        value = wrapValueBasedOnType(rawValue, type)
        message = (append) ? "which appends arguments" : "which replaces arguments"
    }

    def "task :#testTaskName writes log output"() {
        given:
        buildFile << workingRustTaskConfig

        and: "a future log file"
        def logFile = new File(projectDir, "build/logs/${testTaskName}.log")
        assert !logFile.exists()

        when:
        runTasks(testTaskName)

        then:
        logFile.exists()
        !logFile.text.empty
    }

    def "can provide additional build arguments"() {
        given:
        buildFile << workingRustTaskConfig

        and: "some custom arguments"
        buildFile << """
        ${testTaskName}.buildArgument("-quiet")
        ${testTaskName}.buildArguments("-enableAddressSanitizer", "YES")
        ${testTaskName}.buildArguments("-enableThreadSanitizer", "NO")
        """.stripIndent()

        when:
        def result = runTasks(testTaskName)

        then:
        outputContains(result, "-quiet")
        outputContains(result, "-enableAddressSanitizer YES")
        outputContains(result, "-enableThreadSanitizer NO")
    }
}
