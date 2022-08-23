package wooga.gradle.rustup

import com.wooga.gradle.test.PropertyLocation
import com.wooga.gradle.test.writers.PropertyGetterTaskWriter
import com.wooga.gradle.test.writers.PropertySetInvocation
import com.wooga.gradle.test.writers.PropertySetterWriter
import spock.lang.Unroll

class RustupPluginIntegrationSpec extends RustupIntegrationSpec {

    def setup() {
        buildFile << """
          ${applyPlugin(RustupPlugin)}
       """.stripIndent()
    }

    @Unroll()
    def "rustup task property #property of type #type sets #rawValue when #location"() {
        given:
        environmentVariables.clear("CARGO_HOME", "RUSTUP_HOME")

        expect:
        runPropertyQuery(get, set).matches(rawValue)

        where:
        property     | method                            | rawValue                                           | type                    | location
        "cargoHome"  | PropertySetInvocation.assignment  | osPath("/path/to/cargo_home")                      | "File"                  | PropertyLocation.script
        "cargoHome"  | PropertySetInvocation.assignment  | osPath("/path/to/cargo_home")                      | "Provider<Directory>"   | PropertyLocation.script
        "cargoHome"  | PropertySetInvocation.providerSet | osPath("/path/to/cargo_home")                      | "File"                  | PropertyLocation.script
        "cargoHome"  | PropertySetInvocation.providerSet | osPath("/path/to/cargo_home")                      | "Provider<Directory>"   | PropertyLocation.script
        "cargoHome"  | PropertySetInvocation.setter      | osPath("/path/to/cargo_home")                      | "File"                  | PropertyLocation.script
        "cargoHome"  | PropertySetInvocation.setter      | osPath("/path/to/cargo_home")                      | "Provider<Directory>"   | PropertyLocation.script
        "cargoHome"  | _                                 | System.getenv("HOME") + File.separator + ".cargo"  | _                       | PropertyLocation.none

        "rustupHome" | PropertySetInvocation.assignment  | osPath("/path/to/rustup_home")                     | "File"                  | PropertyLocation.script
        "rustupHome" | PropertySetInvocation.assignment  | osPath("/path/to/rustup_home")                     | "Provider<Directory>"   | PropertyLocation.script
        "rustupHome" | PropertySetInvocation.providerSet | osPath("/path/to/rustup_home")                     | "File"                  | PropertyLocation.script
        "rustupHome" | PropertySetInvocation.providerSet | osPath("/path/to/rustup_home")                     | "Provider<Directory>"   | PropertyLocation.script
        "rustupHome" | PropertySetInvocation.setter      | osPath("/path/to/rustup_home")                     | "File"                  | PropertyLocation.script
        "rustupHome" | PropertySetInvocation.setter      | osPath("/path/to/rustup_home")                     | "Provider<Directory>"   | PropertyLocation.script
        "rustupHome" | _                                 | System.getenv("HOME") + File.separator + ".rustup" | _                       | PropertyLocation.none

        "logFile"    | PropertySetInvocation.assignment  | osPath("/custom/logs/log1.log")                    | "File"                  | PropertyLocation.script
        "logFile"    | PropertySetInvocation.assignment  | osPath("/custom/logs/log2.log")                    | "Provider<RegularFile>" | PropertyLocation.script
        "logFile"    | PropertySetInvocation.providerSet | osPath("/custom/logs/log3.log")                    | "File"                  | PropertyLocation.script
        "logFile"    | PropertySetInvocation.providerSet | osPath("/custom/logs/log4.log")                    | "Provider<RegularFile>" | PropertyLocation.script
        "logFile"    | PropertySetInvocation.setter      | osPath("/custom/logs/log5.log")                    | "File"                  | PropertyLocation.script
        "logFile"    | PropertySetInvocation.setter      | osPath("/custom/logs/log6.log")                    | "Provider<RegularFile>" | PropertyLocation.script

        extensionName = "xcodebuild"
        set = new PropertySetterWriter("rustup", property)
                .serialize(wrapValueFallback)
                .set(rawValue, type)
                .to(location)
                .use(method)

        get = new PropertyGetterTaskWriter(set)
    }
}
