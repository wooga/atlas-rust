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

import wooga.gradle.rust.internal.PropertyLookup

class RustConsts {

    /**
     * Gradle property lookup object with values for fetching the default xcodebuild logs directiory.
     *
     * @environmentVariable "RUST_LOGS_DIR"
     * @propertyName "rust.logsDir"
     * @defaultValue "build/logs"
     * @see wooga.gradle.rust.RustPluginExtension#getLogsDir()
     */
    static final PropertyLookup LOGS_DIR_LOOKUP = new PropertyLookup("RUST_LOGS_DIR", "rust.logsDir", "logs")

    static final PropertyLookup WORKING_DIR_LOOKUP = new PropertyLookup("RUST_CARGO_WORKING_DIR", "rust.cargoWorkingDir", "rust-project")
    static final PropertyLookup<Boolean> PATCH_CARGO_VERSION = new PropertyLookup("RUST_PATCH_CARGO_VERSION", "rust.patchCargoVersion", false)

    static final PropertyLookup<String> VERSION = new PropertyLookup<>("RUST_VERSION", "rust.version", "1.50.0")
    static final PropertyLookup<Boolean> USE_LOCAL_INSTALLATION = new PropertyLookup<>("RUST_USE_LOCAL_INSTALLATION", "rust.useLocalInstallation", false)
}
