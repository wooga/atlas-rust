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
import org.gradle.api.provider.Provider
import org.ysb33r.grolifant.api.errors.UnsupportedConfigurationException

import java.util.concurrent.Callable

import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_3
import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_5
import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_8

/** Utilities for dealing with tasks.
 *
 * @author Schalk W. Cronje
 *
 * @since 0.17.0
 */
@CompileStatic
class TaskUtils {

    /** Resolves a list of items to a list of tasks.
     *
     * List items can be any of the types supported by {@link #taskize(Project project, Object o)}, but
     * in addition the following are supported:
     *
     * <ul>
     *     <li>Map</li>
     *     <li>Iterable / Collection</li>
     * </ul>
     *
     * Embedded lists are resolved recursively and the final result is a flattend list. This will be done even when
     * a {@code Provider}, {@code Callable} or {@code Closure} returns an iterable sequence.
     *
     * @param project Project context
     * @param taskyThings Iterable sequence of items to be resolved.
     * @return LIst of resolved tasks.
     * @throw {@link UnsupportedConfigurationException} if any of the objects cannot be converted to a task.
     * @throw {@link org.gradle.api.UnknownTaskException} if the conversion is supported but the task cannot be located.
     */
    static List<Task> taskize(final Project project, final Iterable<Object> taskyThings) {
        List<Task> collection = []
        for (Object item in taskyThings) {
            if (isIterableProperty(item)) {
                resolveIterablePropertyTo(collection, project, item)
            } else {
                switch (item) {
                    case Map:
                        collection.addAll(taskize(project, (Iterable) ((Map) item).values()))
                        break
                    case Iterable:
                        collection.addAll(taskize(project, (Iterable) item))
                        break
                    case Provider:
                        resolveSingleItemOrIterableTo(collection, project, ((Provider) item).get())
                        break
                    case Callable:
                        resolveSingleItemOrIterableTo(collection, project, ((Callable) item).call())
                        break
                    default:
                        collection.add(taskize(project, item))
                }
            }
        }
        collection
    }

    /** Resolves a single item to an existing task instance.
     *
     * The following types are supported:
     * <ul>
     *     <li>Any Gradle {@link Task}</li>
     *     <li>Any Gradle {@link org.gradle.api.tasks.TaskProvider} since Gradle 4.8+</li>
     *     <li>Any Grolifant {@link org.ysb33r.grolifant.api.v4.TaskProvider}</li>
     *     <li>Any standard Java {@code CharSequence}</li>
     *     <li>Any {@link Provider} of the above types</li>
     *     <li>Any Groovy Closure that returns one of the above types.</li>
     *     <li>Any Java {@code Callable} that returns one of the above types</li>
     * </ul>
     *
     * Resolving occurs recursively if necessary.
     *
     * @param project Project context
     * @param t Object to resolve to task
     * @return Resolved task instance.
     * @throw {@link UnsupportedConfigurationException} if the object cannot be converted to a task.
     * @throw {@link org.gradle.api.UnknownTaskException} if the conversion is supported but the task cannto be located.
     */
    static Task taskize(final Project project, final Object t) {
        if (isGradleTaskProvider(t)) {
            return gradleTaskProviderToTask(t)
        }

        switch (t) {
            case Task:
                return (Task) t
            case TaskProvider:
                return ((TaskProvider) t).get()
            case Provider:
                return taskize(project, ((Provider) t).get())
            case Callable:
                return taskize(project, ((Callable) t).call())
            case Closure:
                return taskize(project, ((Closure) t).call())
            case CharSequence:
                return project.tasks.getByName(StringUtils.stringize(t))
            default:
                throw new UnsupportedConfigurationException("Cannot convert ${t.class.name} to a task")
        }
    }

    /** Creates a basic task {@link Provider} instance from an object.
     *
     * @param project Project context.
     * @param o Object to be evaluated to a task.
     * @return Lazy-evaluatable task.
     */
    static Provider<Task> taskProviderFrom(Project project, Object o) {
        project.provider({ ->
            taskize(project, o)
        } as Callable<Task>)
    }

    @CompileDynamic
    static private boolean isGradleTaskProvider(Object o) {
        if (PRE_4_8) {
            false
        } else {
            o instanceof org.gradle.api.tasks.TaskProvider
        }
    }

    @CompileDynamic
    static private Task gradleTaskProviderToTask(Object taskProvider) {
        if (PRE_4_8) {
            throw new UnsupportedConfigurationException('rg.gradle.api.tasks.TaskProvider is not supported')
        } else {
            ((org.gradle.api.tasks.TaskProvider) (taskProvider)).get()
        }
    }

    @CompileDynamic
    static private boolean isIterableProperty(Object o) {
        isListProperty(o) || isSetProperty(o)
    }

    @CompileDynamic
    static private boolean isListProperty(Object o) {
        if (PRE_4_3) {
            false
        } else {
            o instanceof org.gradle.api.provider.ListProperty
        }
    }

    @CompileDynamic
    static private boolean isSetProperty(Object o) {
        if (PRE_4_5) {
            false
        } else {
            o instanceof org.gradle.api.provider.SetProperty
        }
    }

    @CompileDynamic
    static private void resolveIterablePropertyTo(List<Task> tasks, Project project, Object o) {
        tasks.addAll(taskize(project, o.get()))
    }

    static private void resolveSingleItemOrIterableTo(List<Task> tasks, Project project, Object o) {
        if (o instanceof Iterable) {
            tasks.addAll(taskize(project, o))
        } else {
            tasks.addAll(taskize(project, [o]))
        }
    }
}
