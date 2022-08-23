package wooga.gradle.rustup.tasks

import com.wooga.gradle.PlatformUtils
import com.wooga.gradle.test.PropertyQueryTaskWriter
import com.wooga.gradle.test.writers.PropertyGetterTaskWriter
import com.wooga.gradle.test.writers.PropertySetterWriter
import spock.lang.Unroll
import wooga.gradle.rustup.RustupTaskIntegrationSpec

import static com.wooga.gradle.test.PropertyUtils.toSetter
import static com.wooga.gradle.test.writers.PropertySetInvocation.*

class RustupInstallIntegrationSpec extends RustupTaskIntegrationSpec<Rustup> {

    @Unroll("property #property #valueMessage sets flag #expectedCommandlineFlag")
    def "constructs arguments"() {
        given: "a set property"
        if (method != _) {
            buildFile << """
            ${subjectUnderTestName}.${invocation}($value)
            """.stripIndent()
        }

        when:
        def query = new PropertyQueryTaskWriter("${subjectUnderTestName}.arguments", ".get().join(\" \")")
        query.write(buildFile)
        def result = runTasksSuccessfully(query.taskName)

        then:
        outputContains(result, expectedCommandlineFlag)

        where:
        property              | invocation               | rawValue                                             | type           || expectedCommandlineFlag
        "defaultHost"         | toSetter(property)       | "x86_64-unknown-linux-gnu"                           | "String"       || "--default-host ${rawValue}"
        "defaultToolchain"    | toSetter(property)       | "stable"                                             | "String"       || "--default-toolchain ${rawValue}"
        "profile"             | toSetter(property)       | "minimal"                                            | "String"       || "--${property} ${rawValue}"
        "additionalArguments" | "setAdditionalArguments" | ["--verbose", "--foo bar"]                           | "List<String>" || "--verbose --foo bar"
        "components"          | "components"             | ["1", "2", "3"]                                      | "List<String>" || "--component 1 --component 2 --component 3"
        "target"              | "targets"                | ["x86_64-unknown-linux-gnu", "x86_64-linux-android"] | "List<String>" || "--target x86_64-unknown-linux-gnu --target x86_64-linux-android"
        value = wrapValueBasedOnType(rawValue, type)
        valueMessage = (rawValue != _) ? "with value ${value}" : "without value"
    }

    @Unroll("can set property #property with #invocation and type #type")
    def "can set property"() {
        expect:
        runPropertyQuery(get, set).matches(rawValue)

        where:
        property           | invocation  | rawValue                   | type
        "defaultHost"      | providerSet | "x86_64-unknown-linux-gnu" | "String"
        "defaultHost"      | providerSet | "x86_64-unknown-linux-gnu" | "Provider<String>"
        "defaultHost"      | setter      | "x86_64-unknown-linux-gnu" | "String"
        "defaultHost"      | setter      | "x86_64-unknown-linux-gnu" | "Provider<String>"

        "defaultToolchain" | providerSet | "stable"                   | "String"
        "defaultToolchain" | providerSet | "stable"                   | "Provider<String>"
        "defaultToolchain" | setter      | "stable"                   | "String"
        "defaultToolchain" | setter      | "stable"                   | "Provider<String>"

        "profile"          | providerSet | "minimal"                  | "String"
        "profile"          | providerSet | "minimal"                  | "Provider<String>"
        "profile"          | setter      | "minimal"                  | "String"
        "profile"          | setter      | "minimal"                  | "Provider<String>"

        "rustupHome"       | providerSet | osPath("/some/path/to")            | "File"
        "rustupHome"       | providerSet | osPath("/some/path/to")            | "Provider<Directory>"
        "rustupHome"       | setter      | osPath("/some/path/to")            | "File"
        "rustupHome"       | setter      | osPath("/some/path/to")            | "Provider<Directory>"

        "cargoHome"        | providerSet | osPath("/some/path/to")            | "File"
        "cargoHome"        | providerSet | osPath("/some/path/to")            | "Provider<Directory>"
        "cargoHome"        | setter      | osPath("/some/path/to")            | "File"
        "cargoHome"        | setter      | osPath("/some/path/to")            | "Provider<Directory>"

        set = new PropertySetterWriter(subjectUnderTestName, property)
                .set(rawValue, type)
                .toScript(invocation)
                .serialize(wrapValueFallback)

        get = new PropertyGetterTaskWriter(set)
    }

    private static executableName(String name) {
        PlatformUtils.isWindows() ? "${name}.exe" : name
    }

    def "installs rustup"() {
        given:
        def tempRustupHome = File.createTempDir("rustup", "home")
        def tempCargoHome = File.createTempDir("cargo", "home")

        and: "future files"
        File rustup = new File(tempCargoHome, executableName("bin/rustup"))
        File cargo = new File(tempCargoHome, executableName("bin/cargo"))
        assert !rustup.exists()
        assert !cargo.exists()

        and: "a default toolchain"
        appendToSubjectTask("""
            defaultToolchain='stable'
            rustupHome = ${wrapValueBasedOnType(tempRustupHome, "File")}
            cargoHome = ${wrapValueBasedOnType(tempCargoHome, "File")}
        """.stripIndent())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        rustup.exists()
        rustup.canExecute()

        cargo.exists()
        cargo.canExecute()
    }

    def listInstalledTargets(File rustupHome, File cargoHome) {
        def out = new ByteArrayOutputStream()
        def err = new ByteArrayOutputStream()
        File rustup = new File(cargoHome, executableName("bin/rustup"))
        "${rustup.absolutePath} target list --installed".execute(["RUSTUP_HOME=${rustupHome.absolutePath}", "CARGO_HOME=${cargoHome.absolutePath}"], null).waitForProcessOutput(out, err)

        out.toString().readLines()
    }

    def listInstalledComponents(File rustupHome, File cargoHome) {
        def out = new ByteArrayOutputStream()
        def err = new ByteArrayOutputStream()
        File rustup = new File(cargoHome, executableName("bin/rustup"))
        "${rustup.absolutePath} component list --installed".execute(["RUSTUP_HOME=${rustupHome.absolutePath}", "CARGO_HOME=${cargoHome.absolutePath}"], null).waitForProcessOutput(out, err)

        out.toString().readLines()
    }

    def installRustup(File rustupHome, File cargoHome) {
        def out = new ByteArrayOutputStream()
        def err = new ByteArrayOutputStream()
        String postfixName = PlatformUtils.isWindows() ? ".exe" : "init.sh"
        String prefixName = PlatformUtils.isWindows() ? "rustup-init" : "rustup"
        File rustupInstallScript = File.createTempFile(prefixName, postfixName)

        def url = new URL(PlatformUtils.isWindows() ? "https://static.rust-lang.org/rustup/dist/x86_64-pc-windows-msvc/rustup-init.exe" : "https://sh.rustup.rs")
        url.withInputStream { i ->
            rustupInstallScript.withOutputStream {
                it << i
            }
        }
        rustupInstallScript.setExecutable(true)
        def p = "${rustupInstallScript.absolutePath} -y --no-modify-path --profile=minimal".execute(["RUSTUP_HOME=${rustupHome.absolutePath}", "CARGO_HOME=${cargoHome.absolutePath}"], null)
        p.waitForProcessOutput(out, err)
        if (p.exitValue() != 0) {
            throw new Exception("rustup install failed")
        }
    }

    def "installs rustup with targets"() {
        given:
        def tempRustupHome = File.createTempDir("rustup", "home")
        def tempCargoHome = File.createTempDir("cargo", "home")

        and: "future files"
        File rustup = new File(tempCargoHome, "bin/rustup")
        File cargo = new File(tempCargoHome, "bin/cargo")
        assert !rustup.exists()
        assert !cargo.exists()

        and: "a default toolchain"
        appendToSubjectTask("""
            defaultToolchain='stable'
            rustupHome = ${wrapValueBasedOnType(tempRustupHome, "File")}
            cargoHome = ${wrapValueBasedOnType(tempCargoHome, "File")}
            targets = ${wrapValueBasedOnType(targets, "List<String>")} 
        """.stripIndent())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        listInstalledTargets(tempRustupHome, tempCargoHome).containsAll(targets)

        where:
        targets = ["aarch64-apple-darwin", "x86_64-apple-darwin"]
    }

    def "installs rustup with components"() {
        given:
        def tempRustupHome = File.createTempDir("rustup", "home")
        def tempCargoHome = File.createTempDir("cargo", "home")

        and: "future files"
        File rustup = new File(tempCargoHome, executableName("bin/rustup"))
        File cargo = new File(tempCargoHome, executableName("bin/cargo"))
        assert !rustup.exists()
        assert !cargo.exists()

        and: "a default toolchain"
        appendToSubjectTask("""
            defaultToolchain='stable'
            rustupHome = ${wrapValueBasedOnType(tempRustupHome, "File")}
            cargoHome = ${wrapValueBasedOnType(tempCargoHome, "File")}
            components = ${wrapValueBasedOnType(components, "List<String>")} 
        """.stripIndent())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        listInstalledComponents(tempRustupHome, tempCargoHome).any { component ->
            components.any { component.startsWith(it) }
        }

        where:
        components = ['rls', 'rust-analysis']
    }

    def "installs rustup missing components"() {
        given:
        def tempRustupHome = File.createTempDir("rustup", "home")
        tempRustupHome.deleteOnExit()
        def tempCargoHome = File.createTempDir("cargo", "home")
        tempRustupHome.deleteOnExit()

        and: "future files"
        File rustup = new File(tempCargoHome, executableName("bin/rustup"))
        File cargo = new File(tempCargoHome, executableName("bin/cargo"))
        assert !rustup.exists()
        assert !cargo.exists()

        and: "a rustup installation"
        installRustup(tempRustupHome, tempCargoHome)

        assert !listInstalledTargets(tempRustupHome, tempCargoHome).containsAll(targets)
        assert !listInstalledComponents(tempRustupHome, tempCargoHome).any { component ->
            components.any { component.startsWith(it) }
        }

        and:
        appendToSubjectTask("""
            rustupHome = ${wrapValueBasedOnType(tempRustupHome, "File")}
            cargoHome = ${wrapValueBasedOnType(tempCargoHome, "File")}
            components = ${wrapValueBasedOnType(components, "List<String>")}
            targets = ${wrapValueBasedOnType(targets, "List<String>")}
        """.stripIndent())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        rustup.exists()
        rustup.canExecute()

        cargo.exists()
        cargo.canExecute()

        listInstalledComponents(tempRustupHome, tempCargoHome).any { component ->
            components.any { component.startsWith(it) }
        }

        listInstalledTargets(tempRustupHome, tempCargoHome).containsAll(targets)

        where:
        components = ['rls']
        targets = ["riscv32i-unknown-none-elf", "powerpc-unknown-linux-gnu"]
    }

    def "update runs rustup update and self update"() {
        given:
        def tempRustupHome = File.createTempDir("rustup", "home")
        tempRustupHome.deleteOnExit()
        def tempCargoHome = File.createTempDir("cargo", "home")
        tempRustupHome.deleteOnExit()

        and: "a rustup installation"
        installRustup(tempRustupHome, tempCargoHome)

        and:
        appendToSubjectTask("""
            rustupHome = ${wrapValueBasedOnType(tempRustupHome, "File")}
            cargoHome = ${wrapValueBasedOnType(tempCargoHome, "File")}
            update = true
        """.stripIndent())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        result.standardOutput.contains("update rustup")

    }
}
