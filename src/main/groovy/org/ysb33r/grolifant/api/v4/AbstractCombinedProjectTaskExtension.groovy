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
package org.ysb33r.grolifant.api.v4

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task

/** Base class for an extension that can both be used on a project or a task.
 *
 * @since 0.4
 */
@CompileStatic
class AbstractCombinedProjectTaskExtension {

    /** Uses reflection to invoke a method on a project extension or a task extension.
     * If the extension is attached to a task and the returned value is null, it will first try to resolve the value
     * by the project extension.
     *
     * @param methodName Method to invoke.
     * @return Object or {@code null}.
     */
    Object getValueByMethod(final String methodName) {
        Object ret = resolveValueByMethod(this, methodName)
        if (ret == null && task != null) {
            ret = resolveValueByMethod(projectExtension, methodName)
        }
        ret
    }

    /** Uses a closure to resolve a value on a project extension or a task extension.
     * If the extension is attached to a task and the returned value is null, it will first try to resolve the value
     * by the project extension.
     *
     * @param getter A closure which will be passed an extension object which is an implementation of
     * {@code AbstractCombinedProjectTaskExtension}.
     * @return Object or {@code null}.
     */
    Object getValue(Closure getter) {
        Object result = getter(this)
        if (result == null && task != null) {
            result = getter(projectExtension)
        }
        result
    }

    /** Attach this extension to a project
     *
     * @param project Project to attach to.
     */
    protected AbstractCombinedProjectTaskExtension(Project project) {
        this.project = project
    }

    /** Attach this extension to a task
     *
     * @param task Task to attach to
     */
    protected AbstractCombinedProjectTaskExtension(Task task, final String projectExtName) {
        this.task = task
        this.projectExtName = projectExtName
    }

    /** Project this extension is associated with.
     *
     * @return @{code task ? task.project : project}
     */
    protected Project getProject() {
        task?.project ?: project
    }

    /** Task this extension is attached to.
     *
     * @return Task or {@code null} if extension is not attached to a task.
     */
    protected Task getTask() {
        this.task
    }

    /** Returns the extension that is attached to the project.
     *
     * @return Extension as attached to the project
     * @throw {@link org.gradle.api.UnknownDomainObjectException} is extension does not exist.
     */
    protected AbstractCombinedProjectTaskExtension getProjectExtension() {
        (AbstractCombinedProjectTaskExtension) (getProject().extensions.getByName(projectExtName))
    }

    @CompileDynamic
    private Object resolveValueByMethod(AbstractCombinedProjectTaskExtension ext, final String methodName) {
        ext."${methodName}"()
    }

    private final Project project
    private final Task task
    private final String projectExtName
}
