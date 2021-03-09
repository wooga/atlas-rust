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

package wooga.gradle.rust.tasks

import wooga.gradle.rust.RustLibraryPlugin

class RustCompileIntegrationSpec extends AbstractCargoTaskIntegrationSpec {
    Class taskType = RustCompile

    String testTaskName = "rustLibCompile"

    String workingRustTaskConfig = """
    task ${testTaskName}(type: ${taskType.name}) {
        compileType = wooga.gradle.rust.tasks.AbstractRustCompile.CompileType.LIB
    }
    """.stripIndent()

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
}
