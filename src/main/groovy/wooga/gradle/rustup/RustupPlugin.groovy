package wooga.gradle.rustup

import com.wooga.gradle.PlatformUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import wooga.gradle.rustup.tasks.Rustup

class RustupPlugin implements Plugin<Project> {

    static final String EXTENSION_NAME = "rustup"

    @Override
    void apply(Project project) {
        project.tasks.register("rustup", Rustup) {
            def cargoHomeDefault = System.getenv("CARGO_HOME") ?: (PlatformUtils.isWindows() ? System.getenv("USERPROFILE") : System.getenv("HOME")) + "/.cargo"
            def rustupHomeDefault = System.getenv("RUSTUP_HOME") ?: (PlatformUtils.isWindows() ? System.getenv("USERPROFILE") : System.getenv("HOME")) + "/.rustup"

            cargoHome.convention(project.layout.projectDirectory.dir(cargoHomeDefault))
            rustupHome.convention(project.layout.projectDirectory.dir(rustupHomeDefault))
            logFile.convention(project.layout.buildDirectory.file("logs/${it.name}.log"))
        }
    }
}
