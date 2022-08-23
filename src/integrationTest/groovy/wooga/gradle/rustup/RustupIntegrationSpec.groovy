package wooga.gradle.rustup

import com.wooga.gradle.test.IntegrationSpec


class RustupIntegrationSpec extends IntegrationSpec {
    def setup() {
        def gradleVersion = System.getenv("GRADLE_VERSION")
        if (gradleVersion) {
            this.gradleVersion = gradleVersion
            fork = true
        }
    }

    static wrapValueFallback = { Object rawValue, String type, Closure<String> fallback ->
        switch (type) {
            default:
                return rawValue.toString()
        }
    }
}
