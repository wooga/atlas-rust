/*
 * Copyright 2021 Wooga GmbH
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

package wooga.gradle.rust

import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Unroll

class RustLibProjectIntegrationSpec extends RustIntegrationSpec {
    File testLibCargoFile
    File testLibSrcFile

    def setup() {
        buildFile << """
        ${applyPlugin(RustLibraryPlugin)}

        """.stripIndent()
        testLibCargoFile = createFile("Cargo.toml", projectDir) << """
        [package]
        name = "test-lib"
        version = "0.1.0"
        authors = ["Manfred Endres <atlas@wooga.net>"]
        edition = "2018"
        
        [dependencies]
        """.stripIndent().trim()
        testLibSrcFile = createFile("src/lib.rs", projectDir) << """
        pub struct Test {
            pub id: String,
        }
        
        impl Test {
            pub fn new(id: String) -> Self {
                Test {id}
            }
        }
        
        #[cfg(test)]
        mod tests {
            #[test]
            fn it_works() {
                assert_eq!(2 + 2, 4);
            }
        }
        """.stripIndent()
    }

    @Unroll()
    def "extension property :#property returns '#testValue' if #reason"() {
        given:
        buildFile << """
            task(custom) {
                doLast {
                    def value = ${extensionName}.${property}.getOrNull()
                    println("${extensionName}.${property}: " + value)
                }
            }
        """

        and: "a gradle.properties"
        def propertiesFile = createFile("gradle.properties")

        switch (location) {
            case PropertyLocation.script:
                buildFile << "${extensionName}.${invocation}"
                break
            case PropertyLocation.property:
                propertiesFile << "${extensionName}.${property} = ${escapedValue}"
                break
            case PropertyLocation.env:
                environmentVariables.set(envNameFromProperty(extensionName, property), "${value}")
                break
            default:
                break
        }

        and: "the test value with replace placeholders"
        if (testValue instanceof String) {
            //testValue = escapedPath(testValue)
            testValue = testValue.replaceAll("#projectDir#", escapedPath(projectDir.path))
            //testValue = escapedPath(testValue)
        }

        when: ""
        def result = runTasksSuccessfully("custom")

        then:
        result.standardOutput.contains("${extensionName}.${property}: ${testValue}")

        where:
        property               | method                     | rawValue                               | expectedValue                                       | type                    | location                  | additionalInfo
        "logsDir"              | _                          | osPath("custom/logs")                  | osPath("#projectDir#/build/custom/logs")            | _                       | PropertyLocation.env      | " as relative path"
        "logsDir"              | _                          | osPath("custom/logs")                  | osPath("#projectDir#/build/custom/logs")            | _                       | PropertyLocation.property | " as relative path"
        "logsDir"              | _                          | osPath("build/custom/logs")            | osPath("#projectDir#/build/custom/logs")            | "File"                  | PropertyLocation.script   | " as relative path"
        "logsDir"              | _                          | osPath("build/custom/logs")            | osPath("#projectDir#/build/custom/logs")            | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "logsDir"              | "logsDir.set"              | osPath("build/custom/logs")            | osPath("#projectDir#/build/custom/logs")            | "File"                  | PropertyLocation.script   | " as relative path"
        "logsDir"              | "logsDir.set"              | osPath("build/custom/logs")            | osPath("#projectDir#/build/custom/logs")            | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "logsDir"              | "logsDir"                  | osPath("build/custom/logs")            | osPath("#projectDir#/build/custom/logs")            | "File"                  | PropertyLocation.script   | " as relative path"
        "logsDir"              | "logsDir"                  | osPath("build/custom/logs")            | osPath("#projectDir#/build/custom/logs")            | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "logsDir"              | _                          | _                                      | osPath("#projectDir#/build/logs")                   | _                       | PropertyLocation.none     | ""

        "logsDir"              | _                          | osPath("/custom/logs")                 | _                                                   | _                       | PropertyLocation.env      | " as absolute path"
        "logsDir"              | _                          | osPath("/custom/logs")                 | _                                                   | _                       | PropertyLocation.property | " as absolute path"
        "logsDir"              | _                          | osPath("/custom/logs")                 | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "logsDir"              | _                          | osPath("/custom/logs")                 | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "logsDir"              | "logsDir.set"              | osPath("/custom/logs")                 | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "logsDir"              | "logsDir.set"              | osPath("/custom/logs")                 | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "logsDir"              | "logsDir"                  | osPath("/custom/logs")                 | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "logsDir"              | "logsDir"                  | osPath("/custom/logs")                 | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"

        "reportsDir"           | _                          | osPath("build/custom/reports")         | osPath("#projectDir#/build/custom/reports")         | "File"                  | PropertyLocation.script   | " as relative path"
        "reportsDir"           | _                          | osPath("build/custom/reports")         | osPath("#projectDir#/build/custom/reports")         | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "reportsDir"           | "reportsDir.set"           | osPath("build/custom/reports")         | osPath("#projectDir#/build/custom/reports")         | "File"                  | PropertyLocation.script   | " as relative path"
        "reportsDir"           | "reportsDir.set"           | osPath("build/custom/reports")         | osPath("#projectDir#/build/custom/reports")         | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "reportsDir"           | "reportsDir"               | osPath("build/custom/reports")         | osPath("#projectDir#/build/custom/reports")         | "File"                  | PropertyLocation.script   | " as relative path"
        "reportsDir"           | "reportsDir"               | osPath("build/custom/reports")         | osPath("#projectDir#/build/custom/reports")         | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "reportsDir"           | _                          | _                                      | osPath("#projectDir#/build/reports")                | _                       | PropertyLocation.none     | ""

        "reportsDir"           | _                          | osPath("/custom/reports")              | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "reportsDir"           | _                          | osPath("/custom/reports")              | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "reportsDir"           | "reportsDir.set"           | osPath("/custom/reports")              | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "reportsDir"           | "reportsDir.set"           | osPath("/custom/reports")              | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "reportsDir"           | "reportsDir"               | osPath("/custom/reports")              | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "reportsDir"           | "reportsDir"               | osPath("/custom/reports")              | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"

        "cargoWorkingDir"      | _                          | osPath("custom/cargoWorkingDir")       | osPath("#projectDir#/build/custom/cargoWorkingDir") | _                       | PropertyLocation.env      | " as relative path"
        "cargoWorkingDir"      | _                          | osPath("custom/cargoWorkingDir")       | osPath("#projectDir#/build/custom/cargoWorkingDir") | _                       | PropertyLocation.property | " as relative path"
        "cargoWorkingDir"      | _                          | osPath("build/custom/cargoWorkingDir") | osPath("#projectDir#/build/custom/cargoWorkingDir") | "File"                  | PropertyLocation.script   | " as relative path"
        "cargoWorkingDir"      | _                          | osPath("build/custom/cargoWorkingDir") | osPath("#projectDir#/build/custom/cargoWorkingDir") | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "cargoWorkingDir"      | "cargoWorkingDir.set"      | osPath("build/custom/cargoWorkingDir") | osPath("#projectDir#/build/custom/cargoWorkingDir") | "File"                  | PropertyLocation.script   | " as relative path"
        "cargoWorkingDir"      | "cargoWorkingDir.set"      | osPath("build/custom/cargoWorkingDir") | osPath("#projectDir#/build/custom/cargoWorkingDir") | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "cargoWorkingDir"      | "cargoWorkingDir"          | osPath("build/custom/cargoWorkingDir") | osPath("#projectDir#/build/custom/cargoWorkingDir") | "File"                  | PropertyLocation.script   | " as relative path"
        "cargoWorkingDir"      | "cargoWorkingDir"          | osPath("build/custom/cargoWorkingDir") | osPath("#projectDir#/build/custom/cargoWorkingDir") | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "cargoWorkingDir"      | _                          | _                                      | osPath("#projectDir#/build/rust-project")           | _                       | PropertyLocation.none     | ""

        "cargoWorkingDir"      | _                          | osPath("/custom/cargoWorkingDir")      | _                                                   | _                       | PropertyLocation.env      | " as absolute path"
        "cargoWorkingDir"      | _                          | osPath("/custom/cargoWorkingDir")      | _                                                   | _                       | PropertyLocation.property | " as absolute path"
        "cargoWorkingDir"      | _                          | osPath("/custom/cargoWorkingDir")      | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "cargoWorkingDir"      | _                          | osPath("/custom/cargoWorkingDir")      | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "cargoWorkingDir"      | "cargoWorkingDir.set"      | osPath("/custom/cargoWorkingDir")      | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "cargoWorkingDir"      | "cargoWorkingDir.set"      | osPath("/custom/cargoWorkingDir")      | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "cargoWorkingDir"      | "cargoWorkingDir"          | osPath("/custom/cargoWorkingDir")      | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "cargoWorkingDir"      | "cargoWorkingDir"          | osPath("/custom/cargoWorkingDir")      | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"

        "manifest"             | _                          | osPath("build/custom/Cargo.toml")      | osPath("#projectDir#/build/custom/Cargo.toml")      | "File"                  | PropertyLocation.script   | " as relative path"
        "manifest"             | _                          | osPath("build/custom/Cargo.toml")      | osPath("#projectDir#/build/custom/Cargo.toml")      | "Provider<RegularFile>" | PropertyLocation.script   | " as relative path"
        "manifest"             | "manifest.set"             | osPath("build/custom/Cargo.toml")      | osPath("#projectDir#/build/custom/Cargo.toml")      | "File"                  | PropertyLocation.script   | " as relative path"
        "manifest"             | "manifest.set"             | osPath("build/custom/Cargo.toml")      | osPath("#projectDir#/build/custom/Cargo.toml")      | "Provider<RegularFile>" | PropertyLocation.script   | " as relative path"
        "manifest"             | "manifest"                 | osPath("build/custom/Cargo.toml")      | osPath("#projectDir#/build/custom/Cargo.toml")      | "File"                  | PropertyLocation.script   | " as relative path"
        "manifest"             | "manifest"                 | osPath("build/custom/Cargo.toml")      | osPath("#projectDir#/build/custom/Cargo.toml")      | "Provider<RegularFile>" | PropertyLocation.script   | " as relative path"
        "manifest"             | _                          | _                                      | osPath("#projectDir#/Cargo.toml")                   | _                       | PropertyLocation.none     | ""

        //"cargoHome"            | _                          | "build/custom/cargoHome"       | _                                           | _                       | PropertyLocation.env      | ""
        //"cargoHome"            | _                          | "build/custom/cargoHome"       | _                                           | _                       | PropertyLocation.property | ""
        "cargoHome"            | _                          | osPath("build/custom/cargoHome")       | osPath("#projectDir#/build/custom/cargoHome")       | "File"                  | PropertyLocation.script   | " as relative path"
        "cargoHome"            | _                          | osPath("build/custom/cargoHome")       | osPath("#projectDir#/build/custom/cargoHome")       | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "cargoHome"            | "cargoHome.set"            | osPath("build/custom/cargoHome")       | osPath("#projectDir#/build/custom/cargoHome")       | "File"                  | PropertyLocation.script   | " as relative path"
        "cargoHome"            | "cargoHome.set"            | osPath("build/custom/cargoHome")       | osPath("#projectDir#/build/custom/cargoHome")       | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        "cargoHome"            | "cargoHome"                | osPath("build/custom/cargoHome")       | osPath("#projectDir#/build/custom/cargoHome")       | "File"                  | PropertyLocation.script   | " as relative path"
        "cargoHome"            | "cargoHome"                | osPath("build/custom/cargoHome")       | osPath("#projectDir#/build/custom/cargoHome")       | "Provider<Directory>"   | PropertyLocation.script   | " as relative path"
        //"cargoHome"         | _                       | _                              | "#projectDir#/build/rust-project"           | _                       | PropertyLocation.none     | ""

        //"rustcPath"            | _                          | "/custom/cargoHome"            | _                                           | _                       | PropertyLocation.env      | ""
        //"rustcPath"            | _                          | "/custom/cargoHome"            | _                                           | _                       | PropertyLocation.property | ""
        "cargoHome"            | _                          | osPath("/custom/cargoHome")            | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "cargoHome"            | _                          | osPath("/custom/cargoHome")            | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "cargoHome"            | "cargoHome.set"            | osPath("/custom/cargoHome")            | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "cargoHome"            | "cargoHome.set"            | osPath("/custom/cargoHome")            | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"
        "cargoHome"            | "cargoHome"                | osPath("/custom/cargoHome")            | _                                                   | "File"                  | PropertyLocation.script   | " as absolute path"
        "cargoHome"            | "cargoHome"                | osPath("/custom/cargoHome")            | _                                                   | "Provider<Directory>"   | PropertyLocation.script   | " as absolute path"

        //"rustcPath"            | _                          | "build/rust/bin/rustc"         | _                                           | _                       | PropertyLocation.env      | ""
        //"rustcPath"            | _                          | "build/rust/bin/rustc"         | _                                           | _                       | PropertyLocation.property | ""
        "rustcPath"            | _                          | osPath("build/rust/bin/rustc")         | osPath("#projectDir#/build/rust/bin/rustc")         | "File"                  | PropertyLocation.script   | ""
        "rustcPath"            | _                          | osPath("build/rust/bin/rustc")         | osPath("#projectDir#/build/rust/bin/rustc")         | "Provider<RegularFile>" | PropertyLocation.script   | ""
        "rustcPath"            | "rustcPath.set"            | osPath("build/rust/bin/rustc")         | osPath("#projectDir#/build/rust/bin/rustc")         | "File"                  | PropertyLocation.script   | ""
        "rustcPath"            | "rustcPath.set"            | osPath("build/rust/bin/rustc")         | osPath("#projectDir#/build/rust/bin/rustc")         | "Provider<RegularFile>" | PropertyLocation.script   | ""
        "rustcPath"            | "rustcPath"                | osPath("build/rust/bin/rustc")         | osPath("#projectDir#/build/rust/bin/rustc")         | "File"                  | PropertyLocation.script   | ""
        "rustcPath"            | "rustcPath"                | osPath("build/rust/bin/rustc")         | osPath("#projectDir#/build/rust/bin/rustc")         | "Provider<RegularFile>" | PropertyLocation.script   | ""
        //"rustcPath"         | _                       | _                              | "#projectDir#/Cargo.toml"                   | _                       | PropertyLocation.none     | ""

        //"cargoPath"            | _                          | "build/rust/bin/cargo"         | _                                           | _                       | PropertyLocation.env      | ""
        //"cargoPath"            | _                          | "build/rust/bin/cargo"         | _                                           | _                       | PropertyLocation.property | ""
        "cargoPath"            | _                          | osPath("build/rust/bin/cargo")         | osPath("#projectDir#/build/rust/bin/cargo")         | "File"                  | PropertyLocation.script   | ""
        "cargoPath"            | _                          | osPath("build/rust/bin/cargo")         | osPath("#projectDir#/build/rust/bin/cargo")         | "Provider<RegularFile>" | PropertyLocation.script   | ""
        "cargoPath"            | "cargoPath.set"            | osPath("build/rust/bin/cargo")         | osPath("#projectDir#/build/rust/bin/cargo")         | "File"                  | PropertyLocation.script   | ""
        "cargoPath"            | "cargoPath.set"            | osPath("build/rust/bin/cargo")         | osPath("#projectDir#/build/rust/bin/cargo")         | "Provider<RegularFile>" | PropertyLocation.script   | ""
        "cargoPath"            | "cargoPath"                | osPath("build/rust/bin/cargo")         | osPath("#projectDir#/build/rust/bin/cargo")         | "File"                  | PropertyLocation.script   | ""
        "cargoPath"            | "cargoPath"                | osPath("build/rust/bin/cargo")         | osPath("#projectDir#/build/rust/bin/cargo")         | "Provider<RegularFile>" | PropertyLocation.script   | ""
        //"rustcPath"         | _                       | _                              | "#projectDir#/Cargo.toml"                   | _                       | PropertyLocation.none     | ""

        "patchCargoVersion"    | _                          | "1"                                    | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "true"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "TRUE"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "True"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "tRuE"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "y"                                    | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "Y"                                    | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "   1 "                                | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | " true    "                            | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | "   TRUE "                             | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | " y "                                  | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | " Y   "                                | true                                                | _                       | PropertyLocation.env      | ""
        "patchCargoVersion"    | _                          | true                                   | _                                                   | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "1"                                    | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "true"                                 | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "TRUE"                                 | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "True"                                 | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "tRuE"                                 | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "y"                                    | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "Y"                                    | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "   1 "                                | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | " true    "                            | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | "   TRUE "                             | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | " y "                                  | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | " Y   "                                | true                                                | _                       | PropertyLocation.property | ""
        "patchCargoVersion"    | _                          | true                                   | _                                                   | "Boolean"               | PropertyLocation.script   | ""
        "patchCargoVersion"    | _                          | true                                   | _                                                   | "Provider<Boolean>"     | PropertyLocation.script   | ""
        "patchCargoVersion"    | "patchCargoVersion.set"    | true                                   | _                                                   | "Boolean"               | PropertyLocation.script   | ""
        "patchCargoVersion"    | "patchCargoVersion.set"    | true                                   | _                                                   | "Provider<Boolean>"     | PropertyLocation.script   | ""
        "patchCargoVersion"    | "patchCargoVersion"        | true                                   | _                                                   | "Boolean"               | PropertyLocation.script   | ""
        "patchCargoVersion"    | "patchCargoVersion"        | true                                   | _                                                   | "Provider<Boolean>"     | PropertyLocation.script   | ""

        "useLocalInstallation" | _                          | "1"                                    | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "true"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "TRUE"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "True"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "tRuE"                                 | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "y"                                    | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "Y"                                    | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "   1 "                                | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | " true    "                            | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | "   TRUE "                             | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | " y "                                  | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | " Y   "                                | true                                                | _                       | PropertyLocation.env      | ""
        "useLocalInstallation" | _                          | true                                   | _                                                   | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "1"                                    | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "true"                                 | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "TRUE"                                 | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "True"                                 | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "tRuE"                                 | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "y"                                    | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "Y"                                    | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "   1 "                                | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | " true    "                            | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | "   TRUE "                             | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | " y "                                  | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | " Y   "                                | true                                                | _                       | PropertyLocation.property | ""
        "useLocalInstallation" | _                          | true                                   | _                                                   | "Boolean"               | PropertyLocation.script   | ""
        "useLocalInstallation" | _                          | true                                   | _                                                   | "Provider<Boolean>"     | PropertyLocation.script   | ""
        "useLocalInstallation" | "useLocalInstallation.set" | true                                   | _                                                   | "Boolean"               | PropertyLocation.script   | ""
        "useLocalInstallation" | "useLocalInstallation.set" | true                                   | _                                                   | "Provider<Boolean>"     | PropertyLocation.script   | ""
        "useLocalInstallation" | "useLocalInstallation"     | true                                   | _                                                   | "Boolean"               | PropertyLocation.script   | ""
        "useLocalInstallation" | "useLocalInstallation"     | true                                   | _                                                   | "Provider<Boolean>"     | PropertyLocation.script   | ""

        "version"              | _                          | "1.42.0"                               | _                                                   | _                       | PropertyLocation.env      | ""
        "version"              | _                          | "1.43.0"                               | _                                                   | _                       | PropertyLocation.property | ""
        "version"              | _                          | "1.45.0"                               | _                                                   | "String"                | PropertyLocation.script   | ""
        "version"              | _                          | "1.46.0"                               | _                                                   | "Provider<String>"      | PropertyLocation.script   | ""
        "version"              | "version.set"              | "1.47.0"                               | _                                                   | "String"                | PropertyLocation.script   | ""
        "version"              | "version.set"              | "1.48.0"                               | _                                                   | "Provider<String>"      | PropertyLocation.script   | ""
        "version"              | "version"                  | "1.49.0"                               | _                                                   | "String"                | PropertyLocation.script   | ""
        "version"              | "version"                  | "1.44.0"                               | _                                                   | "Provider<String>"      | PropertyLocation.script   | ""
        "version"              | _                          | _                                      | "1.50.0"                                            | _                       | PropertyLocation.none     | ""

        extensionName = "rust"
        value = (type != _) ? wrapValueBasedOnType(rawValue, type.toString()) : rawValue
        providedValue = (location == PropertyLocation.script) ? type : value
        testValue = (expectedValue == _) ? rawValue : expectedValue
        reason = location.reason() + ((location == PropertyLocation.none) ? "" : "  with '$providedValue' ") + additionalInfo
        escapedValue = (value instanceof String) ? escapedPath(value) : value
        invocation = (method != _) ? "${method}(${escapedValue})" : "${property} = ${escapedValue}"
    }

    @Unroll("#message when patchCargoVersion is #patchCargoVersion")
    def "patches cargo version when patchCargoVersion is set"() {
        given:
        buildFile << """
        version = "${projectVersion}"
        rust {
            patchCargoVersion = ${patchCargoVersion}
        }
        """

        and: "a future temp manifest file"
        def manifest = new File(projectDir, "build/rust-project/Cargo.toml")
        assert !manifest.exists()

        when:
        def result = runTasks("processRustSource")

        then:
        result.wasSkipped(RustBasePlugin.PATCH_MANIFEST_VERSION) != patchCargoVersion
        manifest.exists()
        manifest.text.contains("version = \"${expectedVersion}\"")

        where:
        patchCargoVersion | expectedVersion
        true              | "1.0.0"
        false             | "0.1.0"
        projectVersion = "1.0.0"
        message = patchCargoVersion ? "patches version Cargo.toml with provided value" : "leaves original version in cargo.toml"
    }

    @IgnoreIf({ os.windows })
    @Unroll("compile")
    def "compile"() {
        when:
        def result = runTasks("build")

        then:
        result.success
        result.wasExecuted("compileLibRust")
        result.wasExecuted("compileTestRust")
        result.wasExecuted("testRust")
    }
}
