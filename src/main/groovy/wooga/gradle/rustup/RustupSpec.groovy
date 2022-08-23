package wooga.gradle.rustup

import com.wooga.gradle.BaseSpec
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

trait RustupSpec extends BaseSpec {

    private final DirectoryProperty cargoHome = objects.directoryProperty()

    @OutputDirectory
    DirectoryProperty getCargoHome() {
        cargoHome
    }

    void setCargoHome(Provider<Directory> value) {
        cargoHome.set(value)
    }

    void setCargoHome(File value) {
        cargoHome.set(value)
    }

    private final DirectoryProperty rustupHome = objects.directoryProperty()

    @OutputDirectory
    DirectoryProperty getRustupHome() {
        rustupHome
    }

    void setRustupHome(Provider<Directory> value) {
        rustupHome.set(value)
    }

    void setRustupHome(File value) {
        rustupHome.set(value)
    }

    private final ListProperty<String> targets = objects.listProperty(String)


    @Internal
    ListProperty<String> getTargets() {
        targets
    }

    void setTargets(Provider<Iterable<String>> values) {
        targets.set(values)
    }

    void setTargets(Iterable<String> values) {
        targets.set(values)
    }

    void target(String value) {
        targets.add(value)
    }

    void targets(String... values) {
        targets.addAll(values)
    }

    void targets(Iterable<String> values) {
        targets.addAll(values)
    }

    private final Property<String> defaultHost = objects.property(String)

    @Internal
    Property<String> getDefaultHost() {
        defaultHost
    }

    void setDefaultHost(Provider<String> value) {
        defaultHost.set(value)
    }

    void setDefaultHost(String value) {
        defaultHost.set(value)
    }

    private final Property<String> defaultToolchain = objects.property(String)

    @Internal
    Property<String> getDefaultToolchain() {
        defaultToolchain
    }

    void setDefaultToolchain(Provider<String> value) {
        defaultToolchain.set(value)
    }

    void setDefaultToolchain(String value) {
        defaultToolchain.set(value)
    }

    private final ListProperty<String> components = objects.listProperty(String)

    @Internal
    ListProperty<String> getComponents() {
        components
    }

    void setComponents(Provider<Iterable<String>> values) {
        components.set(values)
    }

    void setComponents(Iterable<String> values) {
        components.set(values)
    }

    void component(String value) {
        components.add(value)
    }

    void components(String... values) {
        components.addAll(values)
    }

    void components(Iterable<String> values) {
        components.addAll(values)
    }

    private final Property<String> profile = objects.property(String)

    @Internal
    Property<String> getProfile() {
        profile
    }

    void setProfile(Provider<String> value) {
        profile.set(value)
    }

    void setProfile(String value) {
        profile.set(value)
    }

    private final Property<Boolean> update = objects.property(Boolean)

    @Input
    @Optional
    Property<Boolean> getUpdate() {
        update
    }

    void setUpdate(Provider<Boolean> value) {
        update.set(value)
    }

    void setUpdate(Boolean value) {
        update.set(value)
    }
}
