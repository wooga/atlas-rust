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

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class CopyManifest extends DefaultTask {

    @InputFile
    RegularFileProperty getSource() {
        source
    }

    private final RegularFileProperty source

    void setSource(File value) {
        source.set(value)
    }

    void setSource(Provider<RegularFile> value) {
        source.set(value)
    }

    CopyManifest input(File value) {
        setSource(value)
        this
    }

    CopyManifest input(Provider<RegularFile> value) {
        setSource(value)
        this
    }

    @OutputFile
    RegularFileProperty getDestination() {
        destination
    }

    private final RegularFileProperty destination

    void setDestination(File value) {
        destination.set(value)
    }

    void setDestination(Provider<RegularFile> value) {
        destination.set(value)
    }

    CopyManifest destination(File value) {
        setDestination(value)
        this
    }

    CopyManifest destination(Provider<RegularFile> value) {
        setDestination(value)
        this
    }

    CopyManifest() {
        source = project.objects.fileProperty()
        destination = project.objects.fileProperty()
    }

    @TaskAction
    protected copy() {
        project.copy(new Action<CopySpec>() {
            @Override
            void execute(CopySpec copySpec) {
                def destination = destination.get().asFile
                def destinationDir = destination.parentFile
                destinationDir.mkdirs()

                copySpec.from(source) {
                    rename {
                        destination.name
                    }
                }
                copySpec.into(destinationDir)
            }
        })
    }
}
