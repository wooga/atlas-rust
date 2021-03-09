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
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.util.CollectionUtils
import org.ysb33r.grolifant.api.errors.NotSupportedException

import java.util.concurrent.Callable

import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_3
import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_5

/** A collection of utilities for converting to strings.
 *
 *
 */
@CompileStatic
class StringUtils {

    /** Converts most things to a string. Closures are evaluated as well.
     *
     * @param stringy An object that can be converted to a string or a closure that
     *   can be evaluated to something that can be converted to a string.
     * @return A string object
     */
    @SuppressWarnings('CouldBeSwitchStatement')
    static String stringize(final Object stringy) {
        if (stringy instanceof Callable) {
            stringize(((Callable) stringy).call())
        } else if (isProvider(stringy)) {
            stringize(getProvided(stringy))
        } else if (stringy instanceof CharSequence) {
            stringy.toString()
        } else {
            CollectionUtils.stringize([stringy])[0]
        }
    }

    /** Converts a collection of most things to a list of strings. Closures are evaluated as well.
     *
     * @param Iterable list of objects that can be converted to strings, including closure that can be evaluated
     *   into objects that can be converted to strings.
     * @return A list of strings
     */
    static List<String> stringize(final Iterable<?> stringyThings) {
        List<String> collection = []

        for (Object item in stringyThings) {
            if (isIterableProperty(item)) {
                resolveIterablePropertyTo(collection, item)
            } else {
                switch (item) {
                    case Map:
                        collection.addAll(stringize((Iterable) ((Map) item).values()))
                        break
                    case Iterable:
                        collection.addAll(stringize((Iterable) item))
                        break
                    case Provider:
                        resolveSingleItemOrIterableTo(collection, ((Provider) item).get())
                        break
                    case Callable:
                        resolveSingleItemOrIterableTo(collection, ((Callable) item).call())
                        break
                    default:
                        collection.add(stringize(item))
                }
            }
        }
        collection
    }

    /** Updates a Provider.
     *
     * If the Provider is a {@link org.gradle.api.provider.Property} it will be updated in place,
     * otherwise the provider will be assigned a new Provider instance.
     *
     * This method requires Gradle 4.3 at minimum
     *
     * @param project Project context for creating providers
     * @param provider Current provider
     * @param stringy Value that should be lazy-resolved.
     *
     * @since 0.16
     */
    static void updateStringProperty(Project project, Provider<String> provider, Object stringy) {
        if (isProperty(provider)) {
            Provider<String> newProvider = project.provider({ ->
                stringize(stringy)
            } as Callable<String>)

            updateProperty(provider, newProvider)
        } else {
            throw new NotSupportedException('Provider has to be Property<String>. Upgradle to Gradle 4.3 or later.')
        }
    }

    private static boolean isProvider(Object interrogee) {
        interrogee instanceof Provider
    }

    @CompileDynamic
    @SuppressWarnings('UnnecessaryPackageReference')
    private static boolean isProperty(Object interrogee) {
        PRE_4_3 ? false : interrogee instanceof org.gradle.api.provider.Property
    }

    @CompileDynamic
    private static void updateProperty(Provider<String> prop, Provider<String> newProp) {
        ((Property<String>) prop).set(newProp)
    }

    @CompileDynamic
    @SuppressWarnings('UnnecessaryPackageReference')
    private static Object getProvided(Object interrogee) {
        ((org.gradle.api.provider.Provider) interrogee).get()
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
    static private void resolveIterablePropertyTo(List<String> strings, Object o) {
        strings.addAll(stringize(o.get()))
    }

    static private void resolveSingleItemOrIterableTo(List<String> strings, Object o) {
        if (o instanceof Iterable) {
            strings.addAll(stringize(o))
        } else {
            strings.addAll(stringize([o]))
        }
    }

}
