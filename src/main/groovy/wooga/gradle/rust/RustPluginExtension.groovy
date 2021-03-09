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

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface RustPluginExtension<T extends RustPluginExtension> {
    Property<Boolean> getPatchCargoVersion()

    void setPatchCargoVersion(Boolean value)
    void setPatchCargoVersion(Provider<Boolean> value)

    T patchCargoVersion(Boolean value)
    T patchCargoVersion(Provider<Boolean> value)

    DirectoryProperty getCargoWorkingDir()

    void setCargoWorkingDir(File value)
    void setCargoWorkingDir(Provider<Directory> value)

    T cargoWorkingDir(File value)
    T cargoWorkingDir(Provider<Directory> value)

    DirectoryProperty getLogsDir()

    void setLogsDir(File value)
    void setLogsDir(Provider<Directory> value)

    T logsDir(File value)
    T logsDir(Provider<Directory> value)

    DirectoryProperty getReportsDir()

    void setReportsDir(File value)
    void setReportsDir(Provider<Directory> value)

    T reportsDir(File value)
    T reportsDir(Provider<Directory> value)

    RegularFileProperty getManifest()

    void setManifest(File value)
    void setManifest(Provider<RegularFile> value)

    T manifest(File value)
    T manifest(Provider<RegularFile> value)

    RegularFileProperty getCargoPath()

    void setCargoPath(File value)
    void setCargoPath(Provider<RegularFile> value)

    T cargoPath(File value)
    T cargoPath(Provider<RegularFile> value)

    RegularFileProperty getRustcPath()

    void setRustcPath(File value)
    void setRustcPath(Provider<RegularFile> value)

    T rustcPath(File value)
    T rustcPath(Provider<RegularFile> value)

    DirectoryProperty getCargoHome()

    void setCargoHome(File value)
    void setCargoHome(Provider<Directory> value)

    T cargoHome(File value)
    T cargoHome(Provider<Directory> value)

    FileCollection getAbiToolsSearchPath()

    void setAbiToolsSearchPath(Iterable<Object> value)
    void setAbiToolsSearchPath(Object[] value)

    T abiToolsSearchPath(Iterable<Object> value)
    T abiToolsSearchPath(Object[] value)

    FileCollection getSearchPath()

    Property<String> getVersion()

    void setVersion(String value)
    void setVersion(Provider<String> value)

    T version(String value)
    T version(Provider<String> value)

    Property<Boolean> getUseLocalInstallation()

    void setUseLocalInstallation(Boolean value)
    void setUseLocalInstallation(Provider<Boolean> value)

    T useLocalInstallation(Boolean value)
    T useLocalInstallation(Provider<Boolean> value)
}
