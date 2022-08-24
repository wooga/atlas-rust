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

package wooga.gradle.rust.internal

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.ConventionMapping
import org.gradle.api.internal.IConventionAware
import org.gradle.api.reporting.Report
import org.gradle.language.base.plugins.LifecycleBasePlugin
import wooga.gradle.rust.RustBasePlugin
import wooga.gradle.rust.RustPluginExtension
import wooga.gradle.rust.tasks.AbstractCargoTask
import wooga.gradle.rust.tasks.CopyManifest
import wooga.gradle.rust.tasks.RustCompile
import wooga.gradle.rust.tasks.RustTest

import java.util.concurrent.Callable

abstract class AbstractRustPlugin implements Plugin<Project> {
    protected Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.pluginManager.apply(RustBasePlugin.class)

        RustPluginExtension extension = project.extensions.findByType(RustPluginExtension.class)
        Task processRustSourceTask = project.tasks.getByName(RustBasePlugin.PROCESS_RUST_SOURCE)
        Task assemble = project.tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME)
        Task check = project.tasks.getByName(LifecycleBasePlugin.CHECK_TASK_NAME)

        CopyManifest copyManifestTask = (CopyManifest) project.tasks.getByName(RustBasePlugin.COPY_MANIFEST)

        RustCompile rustCompile = createRustCompileTask(project)
        assemble.dependsOn rustCompile

        RustCompile testCompileTask = project.tasks.create("compileTestRust", RustCompile.class)
        testCompileTask.compileType.set(RustCompile.CompileType.TEST)
        testCompileTask.dependsOn(rustCompile)

        RustTest testTask = project.tasks.create("testRust", RustTest)
        testTask.dependsOn(testCompileTask)
        testTask.mustRunAfter(rustCompile)

        check.dependsOn(testTask)

        project.tasks.withType(AbstractCargoTask.class, new Action<AbstractCargoTask>() {
            @Override
            void execute(AbstractCargoTask t) {
                t.dependsOn(processRustSourceTask)
                t.workingDir.convention(extension.cargoWorkingDir)
                t.manifest.convention(copyManifestTask.destination)
                t.logFile.convention(extension.logsDir.file("${t.name}.log"))
                t.cargoHome.convention(extension.cargoHome)
                t.cargoPath.convention(extension.cargoPath)
                t.searchPath.setFrom(extension.searchPath)
                t.target.convention(extension.target)
            }
        })

        project.tasks.withType(RustTest.class, new Action<RustTest>() {
            @Override
            void execute(RustTest t) {
                t.reports.all(new Action<Report>() {
                    @Override
                    void execute(final Report report) {
                        ConventionMapping mapping = ((IConventionAware) report).conventionMapping
                        mapping.map("destination", new Callable<File>() {
                            File call() {
                                extension.reportsDir.dir(t.name).get().file(t.name + "." + report.name).asFile
                            }
                        })
                    }
                })
            }
        })
    }

    abstract protected RustCompile createRustCompileTask(Project project)

}
