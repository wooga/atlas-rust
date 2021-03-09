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

import org.gradle.api.Action;

/**
 * Specified ways of configuring an execution specification for an external tool.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.17.0
 */
public interface ToolExecSpec extends CopyExecSpec, MutableToolExecSpec {
    /**
     * Configure this spec from an {@link org.gradle.api.Action}
     *
     * @param action Configuration action.
     * @return {@code this}.
     */
    ToolExecSpec configure(Action<? extends ToolExecSpec> action);
}
