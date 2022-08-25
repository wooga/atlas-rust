/*
 * Copyright 2018 Wooga GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package wooga.gradle.rust

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.CopySpec
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Copy
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.slf4j.Logger
import wooga.gradle.rust.internal.DefaultRustPluginExtension
import wooga.gradle.rust.internal.PropertyLookup
import wooga.gradle.rust.tasks.CopyManifest
import wooga.gradle.rust.tasks.PatchManifestVersion

@CompileStatic
class RustBasePlugin implements Plugin<Project> {

    static Logger logger = Logging.getLogger(RustBasePlugin)

    static final String PROCESS_RUST_SOURCE = 'processRustSource'
    static final String SOURCE_COPY_TASK = 'sourceCopy'
    static final String PATCH_MANIFEST_VERSION = 'patchManifestVersion'
    static final String COPY_MANIFEST = 'copyManifest'
    static final String RUST_GROUP = "rust"
    static String EXTENSION_NAME = "rust"

    private Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.pluginManager.apply(LifecycleBasePlugin.class)
        project.pluginManager.apply(ReportingBasePlugin.class)
        def extension = create_and_configure_extension(project)

        Task processRustSourceTask = project.tasks.create(PROCESS_RUST_SOURCE)
        processRustSourceTask.description = 'Process Rust source before compilation'

        CopyManifest copyManifestTask = project.tasks.create(COPY_MANIFEST, CopyManifest.class)
        copyManifestTask.source.convention(extension.manifest)
        copyManifestTask.destination.convention(extension.cargoWorkingDir.file(extension.manifest.map({ RegularFile file ->
            file.asFile.name
        })))

        Copy sourceCopyTask = createSourceCopyTask(project, SOURCE_COPY_TASK, extension.cargoWorkingDir)
        sourceCopyTask.description = 'Copy Rust sources to working directory before compilation'
        sourceCopyTask.group = RUST_GROUP

        PatchManifestVersion patchManifestVersionTask = project.tasks.create(PATCH_MANIFEST_VERSION, PatchManifestVersion.class)
        patchManifestVersionTask.description = 'Update version in Cargo.toml with provided version'
        patchManifestVersionTask.group = RUST_GROUP
        patchManifestVersionTask.version.set(project.provider({ project.version.toString() }))
        patchManifestVersionTask.manifest.convention(copyManifestTask.destination)
        patchManifestVersionTask.dependsOn(copyManifestTask)
        patchManifestVersionTask.onlyIf(new Spec<Task>() {
            @Override
            boolean isSatisfiedBy(Task task) {
                extension.patchCargoVersion.get()
            }
        })
        processRustSourceTask.dependsOn(sourceCopyTask, patchManifestVersionTask)
    }

    /** Creates a task that will copy Rust source code to a place where it can be dealt with by Cargo layout.
     *
     * @param project Project the task will be attached to.
     * @param name Name of task
     * @param relativeDir Directory relative to build directory where Rust source code will be placed.
     * @return The{@code Copy} task
     */
    static Copy createSourceCopyTask(final Project project, final String name, final Provider<Directory> destinationDir) {
        Copy task = project.tasks.create(name, Copy.class)
        task.into(destinationDir)
        ['src', 'tests', 'benches', 'doc', 'data'].each { String path ->
            task.from(path, new Action<CopySpec>() {
                @Override
                void execute(CopySpec copySpec) {
                    copySpec.include('**')
                    copySpec.into(path)
                }
            })
        }
        task
    }

    protected RustPluginExtension create_and_configure_extension(Project project) {
        def extension = project.extensions.create(RustPluginExtension, EXTENSION_NAME, DefaultRustPluginExtension, project)
        extension.version.convention(lookupValueInEnvAndPropertiesProvider(RustConsts.VERSION))
        extension.useLocalInstallation.convention(lookupBooleanValueInEnvAndPropertiesProvider(RustConsts.USE_LOCAL_INSTALLATION))
        extension.patchCargoVersion.set(lookupBooleanValueInEnvAndPropertiesProvider(RustConsts.PATCH_CARGO_VERSION))
        extension.cargoWorkingDir.set(project.layout.buildDirectory.dir(lookupValueInEnvAndPropertiesProvider(RustConsts.WORKING_DIR_LOOKUP)))
        extension.logsDir.set(project.layout.buildDirectory.dir(lookupValueInEnvAndPropertiesProvider(RustConsts.LOGS_DIR_LOOKUP)))
        extension.manifest.convention(project.layout.projectDirectory.file("Cargo.toml"))
        final ReportingExtension reportingExtension = (ReportingExtension) project.getExtensions().getByName(ReportingExtension.NAME)
        extension.reportsDir.convention(project.layout.dir(project.provider({ reportingExtension.file("rust") })))
        extension.target.convention(lookupValueInEnvAndPropertiesProvider(RustConsts.TARGET))
        extension.rustupHome.convention(lookupDirectoryValueInEnvAndPropertiesProvider(RustConsts.RUSTUP_HOME ))
        extension
    }

    private Provider<String> lookupValueInEnvAndPropertiesProvider(PropertyLookup<String> lookup) {
        lookupValueInEnvAndPropertiesProvider(lookup.env, lookup.property, lookup.defaultValue)
    }

    private Provider<Boolean> lookupBooleanValueInEnvAndPropertiesProvider(PropertyLookup<Boolean> lookup) {
        lookupBooleanValueInEnvAndPropertiesProvider(lookup.env, lookup.property, lookup.defaultValue)
    }

    private Provider<RegularFile> lookupFileValueInEnvAndPropertiesProvider(PropertyLookup<File> lookup) {
        lookupFileValueInEnvAndPropertiesProvider(lookup.env, lookup.property, project.provider({ lookup.defaultValue }))
    }

    private Provider<Directory> lookupDirectoryValueInEnvAndPropertiesProvider(PropertyLookup<File> lookup) {
        lookupDirectoryValueInEnvAndPropertiesProvider(lookup.env, lookup.property, project.provider({ lookup.defaultValue }))
    }

    private Provider<String> lookupValueInEnvAndPropertiesProvider(String env, String property, String defaultValue = null) {
        project.provider({
            lookupValueInEnvAndProperties(env, property, defaultValue)
        })
    }

    private Provider<Boolean> lookupBooleanValueInEnvAndPropertiesProvider(String env, String property, Boolean defaultValue = null) {
        project.provider({
            def value = lookupValueInEnvAndProperties(env, property, null)
            if (value) {
                return value.toBoolean()
            }
            defaultValue
        })
    }

    private Provider<RegularFile> lookupFileValueInEnvAndPropertiesProvider(String env, String property, Provider<File> defaultValue = null) {
        project.layout.file(project.provider({
            def path = lookupValueInEnvAndProperties(env, property, null)
            if (path) {
                return new File(path)
            }
            null
        }).orElse(defaultValue))
    }

    private Provider<Directory> lookupDirectoryValueInEnvAndPropertiesProvider(String env, String property, Provider<File> defaultValue = null) {
        project.layout.dir(project.provider({
            def path = lookupValueInEnvAndProperties(env, property, null)
            if (path) {
                return new File(path)
            }
            null
        }).orElse(defaultValue))
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected String lookupValueInEnvAndProperties(String env, String property, String defaultValue = null) {
        System.getenv().get(env) ?:
                project.properties.getOrDefault(property, defaultValue)
    }
}
