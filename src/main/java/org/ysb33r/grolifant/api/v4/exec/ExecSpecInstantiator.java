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

import org.gradle.api.Project;

/**
 * Factory interface for instantiating execution specification.
 * <p>
 * This interface is used when creating specification methods as project extensions.
 *
 * @since 0.3
 */
public interface ExecSpecInstantiator<T extends ToolExecSpec> {
    /**
     * Instantiates an execution specification.
     *
     * @param project Project that this execution specification will be associated with.
     * @return New execution specification.
     */
    T create(Project project);
}
