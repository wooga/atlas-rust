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

import org.gradle.api.file.*
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty

interface CargoActionSpec<T extends CargoActionSpec> {

    Provider<List<String>> getBuildArguments()

    RegularFileProperty getLogFile()

    void setLogFile(File value)
    void setLogFile(Provider<RegularFile> value)

    T logFile(File value)
    T logFile(Provider<RegularFile> value)

    ListProperty<String> getAdditionalBuildArguments()

    void setAdditionalBuildArguments(Iterable<String> value)
    void setAdditionalBuildArguments(Provider<? extends Iterable<String>> value)

    T buildArgument(String argument)
    T buildArguments(String... arguments)
    T buildArguments(Iterable<String> arguments)

    RegularFileProperty getManifest()

    void setManifest(File value)
    void setManifest(Provider<RegularFile> value)

    T manifest(File value)
    T manifest(Provider<RegularFile> value)

    DirectoryProperty getWorkingDir()

    void setWorkingDir(File value)
    void setWorkingDir(Provider<Directory> value)

    T workingDir(File value)
    T workingDir(Provider<Directory> value)

    Property<Integer> getJobs()

    void setJobs(Integer value)
    void setJobs(Provider<Integer> value)

    T jobs(Integer value)
    T jobs(Provider<Integer> value)

    SetProperty<String> getNightlyFlags()

    void setNightlyFlags(Iterable<String> value)
    void setNightlyFlags(Provider<Iterable<String>> value)

    T nightlyFlag(String value)
    T nightlyFlags(String... value)
    T nightlyFlags(Iterable<String> value)

    Property<String> getTarget()

    void setTarget(String value)
    void setTarget(Provider<String> value)

    T target(String value)
    T target(Provider<String> value)

    Property<Boolean> getRelease()

    void setRelease(Boolean value)
    void setRelease(Provider<Boolean> value)

    T release(Boolean value)
    T release(Provider<Boolean> value)

    RegularFileProperty getCargoPath()

    void setCargoPath(File value)
    void setCargoPath(Provider<RegularFile> value)

    T cargoPath(File value)
    T cargoPath(Provider<RegularFile> value)

    DirectoryProperty getCargoHome()

    void setCargoHome(File value)
    void setCargoHome(Provider<Directory> value)

    T cargoHome(File value)
    T cargoHome(Provider<Directory> value)

    ConfigurableFileCollection getSearchPath()

    void setSearchPath(Iterable<Object> value)
    void setSearchPath(Object[] value)

    T searchPath(Iterable<Object> value)
    T searchPath(Object[] value)
}
