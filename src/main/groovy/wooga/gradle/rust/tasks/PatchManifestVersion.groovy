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

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class PatchManifestVersion extends DefaultTask {
    @Input
    final Property<String> version

    void setVersion(String value) {
        version.set(value)
    }

    void setVersion(Provider<String> value) {
        version.set(value)
    }

    PatchManifestVersion version(String value) {
        setVersion(value)
        this
    }

    PatchManifestVersion version(Provider<String> value) {
        setVersion(value)
        this
    }

    @InputFile
    final RegularFileProperty manifest

    void setManifest(File value) {
        manifest.set(value)
    }

    void setManifest(Provider<RegularFile> value) {
        manifest.set(value)
    }

    PatchManifestVersion manifest(File value) {
        setManifest(value)
        this
    }

    PatchManifestVersion manifest(Provider<RegularFile> value) {
        setManifest(value)
        this
    }

    PatchManifestVersion() {
        version = project.objects.property(String)
        manifest = project.objects.fileProperty()
    }

    @TaskAction
    protected exec() {
        logger.info("Patch version in ${manifest.get().asFile} to version ${version.get()}")
        ant.replaceregexp(
                file: manifest.get().asFile.path,
                match: '^version\\s=.*$',
                replace:"version = \"${version.get()}\"", byline: true)
    }
}
