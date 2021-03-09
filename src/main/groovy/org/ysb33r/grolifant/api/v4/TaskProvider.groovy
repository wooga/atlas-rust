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
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task

import static groovy.lang.Closure.DELEGATE_FIRST
import static org.ysb33r.grolifant.internal.v4.TaskProviderHelpers.configureEachTaskObject
import static org.ysb33r.grolifant.internal.v4.TaskProviderHelpers.configureTaskObject
import static org.ysb33r.grolifant.internal.v4.TaskProviderHelpers.createOrRegister
import static org.ysb33r.grolifant.internal.v4.TaskProviderHelpers.getByName
import static org.ysb33r.grolifant.internal.v4.TaskProviderHelpers.resolveTask

/** Utilities dealing with the creating of tasks allowing for lazy creation of tasks
 * on Gradle 4.9+, but still use the standard Gradle {@code TaskContainer.create} API methods
 * for earlier versions.
 *
 * In this way plugin authors can maintain compatibility back to older versions of Gradle that does
 * not have lazy creation.
 *
 * @since 0.11
 */
@CompileStatic
class TaskProvider<T extends Task> {

    /** Depending on the version of Gradle creates or registers a new task.
     *
     * @param project Project to attach task to.
     * @param name Name of task to register.
     * @return Task provider proxy.
     */
    static TaskProvider<DefaultTask> registerTask(Project project, String name) {
        new TaskProvider<DefaultTask>(createOrRegister(project.tasks, name))
    }

    /** Depending on the version of Gradle creates or registers a new task.
     *
     * @param project Project to attach task to.
     * @param name Name of task to register.
     * @param type Task type.
     * @return Task provider proxy.
     */
    public static <T extends Task> TaskProvider<T> registerTask(Project project, String name, Class<T> type) {
        new TaskProvider<T>(createOrRegister(project.tasks, name, type))
    }

    /** Depending on the version of Gradle creates or registers a new task.
     *
     * @param project Project to attach task to.
     * @param name Name of task to register.
     * @param type Task type.
     * @param cfg Configuration closure.
     * @return Task provider proxy.
     */
    static <T extends Task> TaskProvider<T> registerTask(
        Project project,
        String name,
        Class<T> type, @DelegatesTo(strategy = DELEGATE_FIRST, type = 'T') Closure cfg) {
        TaskProvider tp = new TaskProvider<T>(createOrRegister(project.tasks, name, type))
        tp.configure(ClosureConfigurator.of(cfg))
        tp
    }

    /** Depending on the version of Gradle creates or registers a new task.
     *
     * @param project Project to attach task to.
     * @param name Name of task to register.
     * @param type Task type.
     * @param action Configuration action.
     * @return Task provider proxy.
     */
    static <T extends Task> TaskProvider<T> registerTask(
        Project project,
        String name,
        Class<T> type,
        Action<? extends Task> cfg
    ) {
        TaskProvider tp = new TaskProvider<T>(createOrRegister(project.tasks, name, type))
        tp.configure(cfg)
        tp
    }

    /** Depending on the version of Gradle creates or registers a new task.
     *
     * @param project Project to attach task to.
     * @param name Name of task to register.
     * @param type Task type.
     * @param args Task constructor arguments.
     * @return Task provider proxy.
     */
    static <T extends Task> TaskProvider<T> registerTask(Project project, String name, Class<T> type, Object... args) {
        new TaskProvider(createOrRegister(project.tasks, name, type, args))
    }

    /** Finds a task on the given project by name
     *
     * @param project Project to query.
     * @param name Name of task.
     * @return A task provider proxy.
     */
    static TaskProvider taskByName(Project project, String name) {
        new TaskProvider(getByName(project.tasks, name))
    }

    /** Finds a task by both type and name
     *
     * @param project Project to query
     * @param type Types to restrict query to.
     * @param name Name of task.
     * @return A task provider proxy.
     */
    public static <T> TaskProvider<? extends Task> taskByTypeAndName(Project project, Class type, String name) {
        new TaskProvider<? extends Task>(getByName(project.tasks.withType(type), name))
    }

    /** Configures a task by name.
     *
     * Depending on the version on Gradle this can be done in a lazy manner.
     *
     * @param project Project the task is attached to
     * @param name Name of the task
     * @param configurator Configuration closure.
     */
    static void configureByName(Project project, String name, Action<? super Task> configurator) {
        taskByName(project, name).configure(configurator)
    }

    /** Configures a task by name.
     *
     * Depending on the version on Gradle this can be done in a lazy manner.
     *
     * @param project Project the task is attached to
     * @param name Name of the task
     * @param configurator Configuration closure.
     */
    static void configureByName(Project project, String name, Closure configurator) {
        configureByName(project, name, configurator as Action)
    }

    /** Configures each task of a specific type.
     *
     * @param project Project to query.
     * @param type Task type.
     * @param configurator Configuration action.
     */
    public static <T> void configureEach(Project project, Class type, Action<? super Task> configurator) {
        configureEachTaskObject(project.tasks.withType(type), configurator)
    }

    /** Configures that the task this task provider proxy points to.
     *
     * @param configurator Configuration action
     */
    void configure(Action<? super Task> configurator) {
        configureTaskObject(this.taskOrProvider, configurator)
    }

    /** Configures that the task this task provider proxy points to.
     *
     * @param configurator Configuration closure
     */
    void configure(Closure configurator) {
        configureTaskObject(this.taskOrProvider, ClosureConfigurator.of(configurator))
    }

    /** The name of the task this task provider proxy is associated with.
     *
     * @return Name of task
     */
    @CompileDynamic
    String getName() {
        this.taskOrProvider.name
    }

    /** Resolves the task
     *
     * @return Resolved task.
     */
    T get() {
        (T) resolveTask(this.taskOrProvider)
    }

    protected TaskProvider(Object taskOrProvider) {
        this.taskOrProvider = taskOrProvider
    }

    private final Object taskOrProvider

    private static class ClosureConfigurator implements Action<Task> {

        static Action<Task> of(Closure cfg) {
            new ClosureConfigurator(cfg)
        }

        @Override
        void execute(Task o) {
            Closure action = (Closure) configurator.clone()
            action.resolveStrategy = action.DELEGATE_FIRST
            action.delegate = o
            action.call()
        }

        private ClosureConfigurator(Closure cfg) {
            configurator = cfg
        }

        private final Closure configurator
    }

}
