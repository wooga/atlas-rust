/*
 * Copyright 2018 Wooga GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package wooga.gradle.rust

import nebula.test.ProjectSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import spock.lang.Unroll
import wooga.gradle.rust.tasks.RustCompile

class RustLibraryPluginSpec extends ProjectSpec {

    public static final String PLUGIN_NAME = 'net.wooga.rust.lib'

    @Unroll("creates the task #taskName")
    def 'Creates needed tasks'(String taskName, Class taskType) {
        given:
        assert !project.plugins.hasPlugin(PLUGIN_NAME)
        assert !project.tasks.findByName(taskName)

        when:
        project.plugins.apply(PLUGIN_NAME)

        then:
        def task = project.tasks.findByName(taskName)
        taskType.isInstance(task)

        where:
        taskName            | taskType
        "processRustSource" | DefaultTask
        "compileLibRust"    | RustCompile
    }


    @Unroll
    def 'Creates the [#extensionName] extension with type #extensionType'() {
        given:
        assert !project.plugins.hasPlugin(PLUGIN_NAME)
        assert !project.extensions.findByName(extensionName)

        when:
        project.plugins.apply(PLUGIN_NAME)

        then:
        def extension = project.extensions.findByName(extensionName)
        extensionType.isInstance extension

        where:
        extensionName | extensionType
        'rust'        | RustPluginExtension
    }

}
