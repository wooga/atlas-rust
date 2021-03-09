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
package org.ysb33r.grolifant.internal.v4

import groovy.transform.CompileDynamic
import org.gradle.api.Action
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Task
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer

import static LegacyLevel.PRE_4_9

/** Internal utilities that deals with API changes in Gradle 4.9.
 *
 * @since 0.11
 */
class TaskProviderHelpers {

    /** Creates or registers a task
     *
     * @param tasks Task container
     * @param name Name of task
     * @return Either a {@code org.gradle.api.TaskProvider} (Gradle 4.9+)
     *   or a {@code org.gradle.api.Task} (Gradle < 4.9)
     */
    @CompileDynamic
    static Object createOrRegister(TaskContainer tasks, String name) {
        if (PRE_4_9) {
            tasks.create(name)
        } else {
            tasks.register(name)
        }
    }

    /** Creates or registers a task
     *
     * @param tasks Task container
     * @param name Name of task
     * @param type Type of task
     * @return Either a {@code org.gradle.api.TaskProvider} (Gradle 4.9+)
     *   or a {@code org.gradle.api.Task} (Gradle < 4.9)
     */
    @CompileDynamic
    static Object createOrRegister(TaskContainer tasks, String name, Class type) {
        if (PRE_4_9) {
            tasks.create(name, type)
        } else {
            tasks.register(name, type)
        }
    }

    /** Creates or registers a task
     *
     * @param tasks Task container
     * @param name Name of task
     * @param type Type of task
     * @param args Task constructor arguments
     * @return Either a {@code org.gradle.api.TaskProvider} (Gradle 4.9+)
     *   or a {@code org.gradle.api.Task} (Gradle < 4.9)
     */
    @CompileDynamic
    static Object createOrRegister(TaskContainer tasks, String name, Class type, Object... args) {
        if (PRE_4_9) {
            tasks.create(name, type, args)
        } else {
            tasks.register(name, type, args)
        }
    }

    /** Obtains a task provider or a task by name.
     *
     * @param tasks Task container.
     * @param name Name of task.
     * @return Either a {@code org.gradle.api.TaskProvider} (Gradle 4.9+)
     *   or a {@code org.gradle.api.Task} (Gradle < 4.9)
     */
    @CompileDynamic
    static Object getByName(TaskContainer tasks, String name) {
        if (PRE_4_9) {
            tasks.getByName(name)
        } else {
            tasks.named(name)
        }
    }

    /** Obtains a task provider or a task by name.
     *
     * @param tasks Task collection.
     * @param name Name of task.
     * @return Either a {@code org.gradle.api.TaskProvider} (Gradle 4.9+)
     *   or a {@code org.gradle.api.Task} (Gradle < 4.9)
     */
    @CompileDynamic
    static Object getByName(TaskCollection tasks, String name) {
        if (PRE_4_9) {
            tasks.getByName(name)
        } else {
            tasks.named(name)
        }
    }

    /** Configures a collection of tasks and/or task providers.
     *
     * @param taskOrProviderCollection Collection of tasks or task providers.
     * @param configurator Configuration action.
     */
    @CompileDynamic
    static void configureEachTaskObject(
        DomainObjectCollection taskOrProviderCollection,
        Action<? super Task> configurator
    ) {
        if (PRE_4_9) {
            taskOrProviderCollection.all(configurator)
        } else {
            taskOrProviderCollection.configureEach(configurator)
        }
    }

    /** Configures a task of a task provider
     *
     * @param taskOrProvider Task or task provider.
     * @param configurator Configuration action.
     */
    @CompileDynamic
    static void configureTaskObject(Object taskOrProvider, Action<? super Task> configurator) {
        if (PRE_4_9) {
            taskOrProvider.project.configure([taskOrProvider], configurator)
        } else {
            taskOrProvider.configure(configurator)
        }
    }

    @CompileDynamic
    static Task resolveTask(Object taskOrProvider) {
        if (PRE_4_9) {
            (Task) taskOrProvider
        } else {
            (Task) taskOrProvider.get()
        }
    }
}
