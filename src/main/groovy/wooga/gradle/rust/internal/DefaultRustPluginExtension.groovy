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

import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import wooga.gradle.rust.RustPluginExtension

class DefaultRustPluginExtension implements RustPluginExtension {

    protected final Project project
    private static final Map<String, Object> SEARCH_PATH = [search: 'rustc']
    static Map<String, Object> searchPath() {
        SEARCH_PATH
    }

    final RustToolsExtension rustToolsExtension

    DefaultRustPluginExtension(Project project) {
        this.project = project
        rustToolsExtension = new RustToolsExtension(project)

        def rustcExecutableName = project.provider({
            RustInstaller.OS.isWindows() ? "rustc.exe" : "rustc"
        }.memoize())

        def cargoExecutableName = project.provider({
            RustInstaller.OS.isWindows() ? "cargo.exe" : "cargo"
        }.memoize())

        patchCargoVersion = project.objects.property(Boolean)
        cargoWorkingDir = project.objects.directoryProperty()
        logsDir = project.objects.directoryProperty()
        reportsDir = project.objects.directoryProperty()
        manifest = project.objects.fileProperty()
        cargoHome = project.objects.directoryProperty()
        cargoHome.convention(project.layout.dir(project.provider({
            if (useLocalInstallation.present && useLocalInstallation.get()) {
                rustToolsExtension.executableBySearchPath("rustc")
            } else {
                rustToolsExtension.executableByVersion(version)
            }
            rustToolsExtension.executable.map({it.parentFile.parentFile}).get()
        }.memoize())))
        cargoBinPath = project.objects.directoryProperty()
        cargoBinPath.convention(cargoHome.dir("bin"))
        cargoPath = project.objects.fileProperty()
        cargoPath.convention(cargoBinPath.file(cargoExecutableName))
        rustcPath = project.objects.fileProperty()
        rustcPath.convention(cargoBinPath.file(rustcExecutableName))
        abiToolsSearchPath = project.objects.fileCollection()
        abiToolsSearchPath.setFrom(RustInstaller.OS.path)
        version = project.objects.property(String)
        useLocalInstallation = project.objects.property(Boolean)
    }

    final Property<Boolean> patchCargoVersion

    void setPatchCargoVersion(Boolean value) {
        patchCargoVersion.set(value)
    }

    void setPatchCargoVersion(Provider<Boolean> value) {
        patchCargoVersion.set(value)
    }

    DefaultRustPluginExtension patchCargoVersion(Boolean value) {
        setPatchCargoVersion(value)
        this
    }

    DefaultRustPluginExtension patchCargoVersion(Provider<Boolean> value) {
        setPatchCargoVersion(value)
        this
    }

    final DirectoryProperty cargoWorkingDir

    void setCargoWorkingDir(File value) {
        cargoWorkingDir.set(value)
    }

    void setCargoWorkingDir(Provider<Directory> value) {
        cargoWorkingDir.set(value)
    }

    DefaultRustPluginExtension cargoWorkingDir(File value) {
        setCargoWorkingDir(value)
        this
    }

    DefaultRustPluginExtension cargoWorkingDir(Provider<Directory> value) {
        setCargoWorkingDir(value)
        this
    }

    final DirectoryProperty logsDir

    void setLogsDir(File value) {
        logsDir.set(value)
    }

    void setLogsDir(Provider<Directory> value) {
        logsDir.set(value)
    }

    DefaultRustPluginExtension logsDir(File value) {
        setLogsDir(value)
        this
    }

    DefaultRustPluginExtension logsDir(Provider<Directory> value) {
        setLogsDir(value)
        this
    }

    private final DirectoryProperty reportsDir

    @Override
    DirectoryProperty getReportsDir() {
        reportsDir
    }

    @Override
    void setReportsDir(File value) {
        reportsDir.set(value)
    }

    @Override
    void setReportsDir(Provider<Directory> value) {
        reportsDir.set(value)
    }

    @Override
    DefaultRustPluginExtension reportsDir(File value) {
        setReportsDir(value)
        this
    }

    @Override
    DefaultRustPluginExtension reportsDir(Provider<Directory> value) {
        setReportsDir(value)
        this
    }

    private final RegularFileProperty manifest

    @Override
    RegularFileProperty getManifest() {
        manifest
    }

    @Override
    void setManifest(File value) {
        manifest.set(value)
    }

    @Override
    void setManifest(Provider<RegularFile> value) {
        manifest.set(value)
    }

    @Override
    DefaultRustPluginExtension manifest(File value) {
        setManifest(value)
        this
    }

    @Override
    DefaultRustPluginExtension manifest(Provider<RegularFile> value) {
        setManifest(value)
        this
    }

    private final RegularFileProperty cargoPath

    @Override
    RegularFileProperty getCargoPath() {
        cargoPath
    }

    @Override
    void setCargoPath(File value) {
        cargoPath.set(value)
    }

    @Override
    void setCargoPath(Provider<RegularFile> value) {
        cargoPath.set(value)
    }

    @Override
    DefaultRustPluginExtension cargoPath(File value) {
        setCargoPath(value)
        this
    }

    @Override
    DefaultRustPluginExtension cargoPath(Provider<RegularFile> value) {
        setCargoPath(value)
        this
    }

    private final RegularFileProperty rustcPath

    @Override
    RegularFileProperty getRustcPath() {
        rustcPath
    }

    @Override
    void setRustcPath(File value) {
        rustcPath.set(value)
    }

    @Override
    void setRustcPath(Provider<RegularFile> value) {
        rustcPath.set(value)
    }

    @Override
    DefaultRustPluginExtension rustcPath(File value) {
        setRustcPath(value)
        this
    }

    @Override
    DefaultRustPluginExtension rustcPath(Provider<RegularFile> value) {
        setRustcPath(value)
        this
    }

    private final DirectoryProperty cargoBinPath
    private final DirectoryProperty cargoHome

    @Override
    DirectoryProperty getCargoHome() {
        cargoHome
    }

    @Override
    void setCargoHome(File value) {
        cargoHome.set(value)
    }

    @Override
    void setCargoHome(Provider<Directory> value) {
        cargoHome.set(value)
    }

    @Override
    DefaultRustPluginExtension cargoHome(File value) {
        setCargoHome(value)
        this
    }

    @Override
    DefaultRustPluginExtension cargoHome(Provider<Directory> value) {
        setCargoHome(value)
        this
    }

    private final ConfigurableFileCollection abiToolsSearchPath

    @Override
    FileCollection getAbiToolsSearchPath() {
        abiToolsSearchPath
    }

    @Override
    void setAbiToolsSearchPath(Iterable<Object> value) {
        abiToolsSearchPath.setFrom(value)
    }

    @Override
    void setAbiToolsSearchPath(Object[] value) {
        abiToolsSearchPath.setFrom(value)
    }

    @Override
    DefaultRustPluginExtension abiToolsSearchPath(Iterable<Object> value) {
        abiToolsSearchPath.from(value)
        this
    }

    @Override
    DefaultRustPluginExtension abiToolsSearchPath(Object[] value) {
        abiToolsSearchPath.from(value)
        this
    }

    @Override
    FileCollection getSearchPath() {
        project.files(cargoBinPath, abiToolsSearchPath)
    }

    private final Property<String> version

    @Override
    Property<String> getVersion() {
        version
    }

    @Override
    void setVersion(String value) {
        version.set(value)
    }

    @Override
    void setVersion(Provider<String> value) {
        version.set(value)
    }

    @Override
    DefaultRustPluginExtension version(String value) {
        setVersion(value)
        this
    }

    @Override
    DefaultRustPluginExtension version(Provider<String> value) {
        setVersion(value)
        this
    }

    private final Property<Boolean> useLocalInstallation

    @Override
    Property<Boolean> getUseLocalInstallation() {
        useLocalInstallation
    }

    @Override
    void setUseLocalInstallation(Boolean value) {
        useLocalInstallation.set(value)
    }

    @Override
    void setUseLocalInstallation(Provider<Boolean> value) {
        useLocalInstallation.set(value)
    }

    @Override
    DefaultRustPluginExtension useLocalInstallation(Boolean value) {
        setUseLocalInstallation(value)
        this
    }

    @Override
    DefaultRustPluginExtension useLocalInstallation(Provider<Boolean> value) {
        setUseLocalInstallation(value)
        this
    }
}
