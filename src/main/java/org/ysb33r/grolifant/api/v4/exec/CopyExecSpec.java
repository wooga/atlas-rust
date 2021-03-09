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
package org.ysb33r.grolifant.api.v4.exec;

import org.gradle.process.ExecSpec;

/** Indicates that a class can copy settings to a standard {@link org.gradle.process.ExecSpec}.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.17.0
 */
public interface CopyExecSpec {
    /** Copies settings from this execution specification to a standard {@link org.gradle.process.ExecSpec}
     *
     * This method is intended to be called as late as possible by a project extension or a task
     *   which would want to delegate to {@code project.exec} project extension. It will cause arguments
     *   to be evaluated. The only items not immediately evaluated are {@code workingDir} and {@code exe}.
     *
     * @param execSpec Exec spec to configure.
     */
    void copyToExecSpec(ExecSpec execSpec);
}
