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

import groovy.transform.CompileStatic
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty

@CompileStatic
abstract class AbstractRustCompile extends AbstractRustLifecycleTask {
    enum CompileType {
        EXE('--bins', 'src'),
        LIB('--lib', 'src'),
        TEST('--tests', 'tests'),
        BENCH('--benches', 'benches')

        final String cargoOption
        final String srcFolder

        CompileType(final String opt, final String folder) {
            cargoOption = opt
            srcFolder = folder
        }

        @Override
        String toString() {
            ":${cargoOption[2..-1]}"
        }
    }

    @SkipWhenEmpty
    @InputFiles
    FileCollection getInputFiles() {
        workingDir.dir(compileType.get().srcFolder).get().asFileTree
    }

    @OutputDirectory
    Provider<Directory> getOutputDir() {
        workingDir.dir("target/${release.getOrElse(false) ? 'release' : 'debug'}")
    }

    @Input
    final Property<CompileType> compileType

    void setCompileType(CompileType value) {
        compileType.set(value)
    }

    void setCompileType(Provider<CompileType> value) {
        compileType.set(value)
    }

    AbstractRustCompile compileType(CompileType value) {
        setCompileType(value)
        this
    }

    AbstractRustCompile compileType(Provider<CompileType> value) {
        setCompileType(value)
        this
    }

    private final Provider<List<String>> cargoCommandArguments

    @Override
    protected Provider<List<String>> getCargoCommandArguments() {
        cargoCommandArguments
    }

    AbstractRustCompile() {
        super()
        compileType = project.objects.property(CompileType)
        cargoCommandArguments = project.provider({
            List<String> arguments = []
            arguments << compileType.get().cargoOption
            arguments
        })
    }

    @Override
    protected String postProcess(int exitValue, String out, String err) {
        if (exitValue != 0) {
            logger.error(err)
        }
        err
    }
}
