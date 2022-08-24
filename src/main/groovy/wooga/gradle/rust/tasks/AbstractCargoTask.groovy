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
import org.gradle.api.file.*
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import wooga.gradle.rust.CargoActionSpec
import wooga.gradle.rust.CargoExecutionException
import wooga.gradle.rust.internal.ForkTextStream
import wooga.gradle.rust.internal.LineBufferingOutputStream
import wooga.gradle.rust.internal.RustInstaller
import wooga.gradle.rust.internal.TextStream

abstract class AbstractCargoTask extends DefaultTask implements CargoActionSpec {

    @Internal
    abstract protected Provider<List<String>> getCargoCommandArguments()

    @Input
    final Provider<List<String>> buildArguments

    @Internal
    abstract protected String getCargoCommand()

    private final ListProperty<String> additionalBuildArguments

    @Input
    @Override
    ListProperty<String> getAdditionalBuildArguments() {
        additionalBuildArguments
    }

    @Override
    void setAdditionalBuildArguments(Iterable<String> value) {
        additionalBuildArguments.set(value)
    }

    @Override
    void setAdditionalBuildArguments(Provider<? extends Iterable<String>> value) {
        additionalBuildArguments.set(value)
    }

    @Override
    AbstractCargoTask buildArgument(String argument) {
        additionalBuildArguments.add(argument)
        return this
    }

    @Override
    AbstractCargoTask buildArguments(String[] arguments) {
        additionalBuildArguments.addAll(project.provider({ arguments.toList() }))
        return this
    }

    @Override
    AbstractCargoTask buildArguments(Iterable arguments) {
        additionalBuildArguments.addAll(project.provider({ arguments }))
        return this
    }

    private final RegularFileProperty logFile

    @Internal
    @Override
    RegularFileProperty getLogFile() {
        logFile
    }

    @Override
    void setLogFile(File value) {
        logFile.set(value)
    }

    @Override
    void setLogFile(Provider<RegularFile> value) {
        logFile.set(value)
    }

    @Override
    AbstractCargoTask logFile(File value) {
        setLogFile(value)
        this
    }

    @Override
    AbstractCargoTask logFile(Provider<RegularFile> value) {
        setLogFile(value)
        this
    }

    private final RegularFileProperty manifest

    @InputFile
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
    AbstractCargoTask manifest(File value) {
        setManifest(value)
        this
    }

    @Override
    AbstractCargoTask manifest(Provider<RegularFile> value) {
        setManifest(value)
        this
    }

    private final DirectoryProperty workingDir

    @InputDirectory
    @Override
    DirectoryProperty getWorkingDir() {
        workingDir
    }

    @Override
    void setWorkingDir(File value) {
        workingDir.set(value)
    }

    @Override
    void setWorkingDir(Provider<Directory> value) {
        workingDir.set(value)
    }

    @Override
    AbstractCargoTask workingDir(File value) {
        setWorkingDir(value)
        this
    }

    @Override
    AbstractCargoTask workingDir(Provider<Directory> value) {
        setWorkingDir(value)
        this
    }

    final Property<Integer> jobs

    @Internal
    @Override
    Property<Integer> getJobs() {
        jobs
    }

    @Override
    void setJobs(Integer value) {
        jobs.set(value)
    }

    @Override
    void setJobs(Provider<Integer> value) {
        jobs.set(value)
    }

    @Override
    AbstractCargoTask jobs(Integer value) {
        setJobs(value)
        this
    }

    @Override
    AbstractCargoTask jobs(Provider<Integer> value) {
        setJobs(value)
        this
    }

    private final SetProperty<String> nightlyFlags

    @Optional
    @Input
    @Override
    SetProperty<String> getNightlyFlags() {
        nightlyFlags
    }

    @Override
    void setNightlyFlags(Iterable<String> value) {
        nightlyFlags.set(value)
    }

    @Override
    void setNightlyFlags(Provider<Iterable<String>> value) {
        nightlyFlags.set(value)
    }

    @Override
    AbstractCargoTask nightlyFlag(String value) {
        nightlyFlags.add(value)
        this
    }

    @Override
    CargoActionSpec nightlyFlags(String[] value) {
        nightlyFlags(value.toList())
    }

    @Override
    AbstractCargoTask nightlyFlags(Iterable<String> value) {
        nightlyFlags.addAll(value)
        this
    }

    private final Property<String> target

    @Optional
    @Input
    @Override
    Property<String> getTarget() {
        target
    }

    @Override
    void setTarget(String value) {
        target.set(value)
    }

    @Override
    void setTarget(Provider<String> value) {
        target.set(value)
    }

    @Override
    AbstractCargoTask target(String value) {
        setTarget(value)
        this
    }

    @Override
    AbstractCargoTask target(Provider<String> value) {
        setTarget(value)
        this
    }

    private final Property<Boolean> release

    @Optional
    @Input
    @Override
    Property<Boolean> getRelease() {
        release
    }

    @Override
    void setRelease(Boolean value) {
        release.set(value)
    }

    @Override
    void setRelease(Provider<Boolean> value) {
        release.set(value)
    }

    @Override
    AbstractCargoTask release(Boolean value) {
        setRelease(value)
        this
    }

    @Override
    AbstractCargoTask release(Provider<Boolean> value) {
        setRelease(value)
        this
    }

    private final ConfigurableFileCollection searchPath

    @Internal
    ConfigurableFileCollection getSearchPath() {
        searchPath
    }

    void setSearchPath(Iterable<Object> value) {
        searchPath.setFrom(value)
    }


    void setSearchPath(Object[] value) {
        searchPath.setFrom(value)
    }

    @Override
    AbstractCargoTask searchPath(Iterable<Object> value) {
        searchPath.from(value)
        this
    }

    @Override
    AbstractCargoTask searchPath(Object[] value) {
        searchPath.from(value)
        this
    }

    @InputFile
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
    AbstractCargoTask cargoPath(File value) {
        setCargoPath(value)
        this
    }

    @Override
    AbstractCargoTask cargoPath(Provider<RegularFile> value) {
        setCargoPath(value)
        this
    }

    private final DirectoryProperty cargoHome

    @Override
    @InputDirectory
    DirectoryProperty getCargoHome() {
        cargoHome
    }

    @Override
    void setCargoHome(File value) {
        cargoHome.set(value)
    }

    @Override
    void setCargoHome(Provider value) {
        cargoHome.set(value)
    }

    @Override
    CargoActionSpec cargoHome(File value) {
        cargoHome.set(value)
        this
    }

    @Override
    CargoActionSpec cargoHome(Provider value) {
        cargoHome.set(value)
        this
    }

    AbstractCargoTask() {
        additionalBuildArguments = project.objects.listProperty(String)
        logFile = project.objects.fileProperty()
        workingDir = project.objects.directoryProperty()
        manifest = project.objects.fileProperty()
        manifest.convention(workingDir.file("Cargo.toml"))
        jobs = project.objects.property(Integer)
        nightlyFlags = project.objects.setProperty(String)
        target = project.objects.property(String)
        release = project.objects.property(Boolean)
        searchPath = project.objects.fileCollection()
        cargoHome = project.objects.directoryProperty()
        cargoPath = project.objects.fileProperty()
        buildArguments = project.provider({
            List<String> args = []
            args << getCargoCommand()
            args << "--manifest-path" << manifest.get().asFile.path
            args << "-vv"
            if (release.present && release.get()) {
                args << "--release"
            }

            if (jobs.present) {
                args << "--jobs" << jobs.get().toString()
            }

            if (target.present) {
                args << "--target" << target.get().toString()
            }

            if (nightlyFlags.present) {
                nightlyFlags.get().each {
                    args << "-Z" << it
                }
            }

            if (additionalBuildArguments.present) {
                args.addAll(additionalBuildArguments.get())
            }

            args.addAll(cargoCommandArguments.get())
            args
        })
    }

    protected String postProcess(int exitValue, String out, String err) {
        ''
    }

    @TaskAction
    protected void action() {
        TextStream outHandler = new ForkTextStream()

        def outStream = new LineBufferingOutputStream(outHandler)
        def outString = new StringWriter()
        outHandler.addWriter(outString)
        if (logging.level >= LogLevel.INFO) {
            outHandler.addWriter(System.out.newPrintWriter())
        }

        TextStream errHandler = new ForkTextStream()
        def errStream = new LineBufferingOutputStream(errHandler)
        def errString = new StringWriter()
        if (logFile.present) {
            logFile.get().asFile.parentFile.mkdirs()
            errHandler.addWriter(logFile.get().asFile.newPrintWriter())
        }
        errHandler.addWriter(errString)
        if (logging.level >= LogLevel.INFO) {
            errHandler.addWriter(System.out.newPrintWriter())
        }

        ExecResult result = project.exec(new Action<ExecSpec>() {
            @Override
            void execute(ExecSpec execSpec) {
                execSpec.executable cargoPath.get().asFile.absolutePath
                execSpec.args buildArguments.get()
                execSpec.workingDir workingDir.get().asFile.absolutePath
                execSpec.environment RustInstaller.OS.pathVar, searchPath.asPath
                execSpec.environment "CARGO_HOME", cargoHome.get().asFile.absolutePath
                execSpec.ignoreExitValue = true
                execSpec.errorOutput = errStream
                execSpec.standardOutput = outStream
            }
        })

        String resultText = postProcess(result.exitValue, outString.toString(), errString.toString())

        if (result.exitValue) {
            try {
                result.assertNormalExitValue()
            } catch (Exception e) {
                throw new CargoExecutionException(resultText, e)
            }
        } else {
            logger.info resultText
        }
    }
}
