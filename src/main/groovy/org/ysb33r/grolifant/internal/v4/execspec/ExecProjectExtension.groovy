/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronje 2016 - 2020
 *
 * This software is licensed under the Apache License 2.0
 * See http://www.apache.org/licenses/LICENSE-2.0 for license details
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * ============================================================================
 */
package org.ysb33r.grolifant.internal.v4.execspec

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.ysb33r.grolifant.api.v4.exec.ExecSpecInstantiator
import org.ysb33r.grolifant.api.v4.exec.ToolExecSpecGroovy

/**
 *
 * @since 0.3
 */
@CompileStatic
class ExecProjectExtension<T extends ToolExecSpecGroovy> {

    ExecProjectExtension(final Project project, final ExecSpecInstantiator<T> instantiator) {
        this.project = project
        this.instantiator = instantiator
    }

    ExecResult call(Action<T> execSpecConfigurator) {
        T execSpec = instantiator.create(project)
        execSpecConfigurator.execute(execSpec)
        execute(execSpec)
    }

    ExecResult call(Closure execSpecConfigurator) {
        T execSpec = instantiator.create(project)
        execSpec.configure execSpecConfigurator
        execute(execSpec)
    }

    ExecResult execute(T execSpec) {
        Closure runner = { T fromSpec, ExecSpec toSpec ->
            fromSpec.copyToExecSpec(toSpec)
        }
        project.exec runner.curry(execSpec)
    }

    private final Project project
    private final ExecSpecInstantiator<T> instantiator
}
