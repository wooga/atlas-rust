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
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.ysb33r.grolifant.api.ClassLocation
import org.ysb33r.grolifant.api.errors.NotSupportedException
import org.ysb33r.grolifant.internal.v4.ClassLocationImpl
import org.ysb33r.grolifant.internal.v4.LegacyLevel
import org.ysb33r.grolifant.internal.v4.Transform
import org.ysb33r.grolifant.internal.v4.copyspec.Resolver

import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.regex.Pattern

import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_3
import static org.ysb33r.grolifant.internal.v4.LegacyLevel.PRE_4_5

/** Various file utilities.
 *
 */
@CompileStatic
class FileUtils {

    public static final Pattern SAFE_FILENAME_REGEX = ~/[^\w_\-.$]/

    /** Converts a string into a string that is safe to use as a file name. T
     *
     * The result will only include ascii characters and numbers, and the "-","_", #, $ and "." characters.
     *
     * @param A potential file name
     * @return A name that is safe on the local filesystem of the current operating system.
     */
    @CompileDynamic
    static String toSafeFileName(String name) {
        name.replaceAll SAFE_FILENAME_REGEX, { String match ->
            String bytes = match.bytes.collect { int it -> Integer.toHexString(it) }.join('')
            "#${bytes}!"
        }
    }

    /** Converts a collection of String into a {@link Path} with all parts guarantee to be safe file parts
     *
     * @param parts File path parts
     * @return File path
     * @since 0.8
     */
    static Path toSafePath(String... parts) {
        List<String> safeParts = Transform.toList(parts as List) { String it -> toSafeFileName(it) }
        safeParts.size() > 0 ? Paths.get(safeParts[0], safeParts[1..-1].toArray() as String[]) : Paths.get(safeParts[0])
    }

    /** Converts a collection of String into a {@@link File} with all parts guarantee to be safe file parts
     *
     * @param parts File path parts
     * @return File path
     * @since 0.8
     */
    static File toSafeFile(String... parts) {
        toSafePath(parts).toFile()
    }

    /** Returns the file collection that a {@link CopySpec} describes.
     *
     * @param copySpec An instance of a {@link CopySpec}
     * @return Result collection of files.
     */
    static FileCollection filesFromCopySpec(CopySpec copySpec) {
        Resolver.resolveFiles(copySpec)
    }

    /** Provides a list of directories below another directory
     *
     * @param distDir Directory
     * @return List of directories. Can be empty if, but never {@code null}
     *   supplied directory.
     */
    static List<File> listDirs(File distDir) {
        if (distDir.exists()) {
            distDir.listFiles(new FileFilter() {
                @Override
                boolean accept(File pathname) {
                    pathname.directory
                }
            }) as List<File>
        } else {
            []
        }
    }

    /** Returns the classpath location for a specific class
     *
     * @param aClass Class to find.
     * @return Location of class. Can be {@code null} which means class has been found, but cannot be placed
     *   on classpath
     * @throw ClassNotFoundException*
     * @since 0.9
     */
    @SuppressWarnings('DuplicateStringLiteral')
    static ClassLocation resolveClassLocation(Class aClass) {
        String location = aClass?.protectionDomain?.codeSource?.location

        if (location) {
            new ClassLocationImpl(new File(location.toURI()).absoluteFile)
        } else {
            URI uri = aClass.getResource(
                '/' + aClass.canonicalName.replace('.', '/') + '.class'
            )?.toURI()

            if (uri == null) {
                throw new ClassNotFoundException("Location for ${aClass.name} cannot be located.")
            } else if (uri.scheme == 'jar') {
                new ClassLocationImpl(
                    new File(new URI(uri.rawSchemeSpecificPart.replaceAll(~/(!.+?)$/, ''))).absoluteFile
                )
            } else if (uri.scheme == 'jrt') {
                new ClassLocationImpl(uri.toURL())
            } else {
                new ClassLocationImpl(new File(uri).parentFile.absoluteFile)
            }
        }
    }

    /** Returns the project cache directory for the given project.
     *
     * @param project Project to query.
     *
     * @return Project cache directory. Never {@code null}.
     *
     * @since 0.14
     */
    static File projectCacheDirFor(Project project) {
        project.gradle.startParameter.projectCacheDir ?: project.file("${project.rootDir}/.gradle")
    }

    /** Converts a file-like object to a {@link java.io.File} instance with project context.
     *
     * Converts any of the following recursively until it gets to a file:
     *
     * <ul>
     *   <li> {@code CharSequence} including {@code String} and {@code GString}.
     *   <li> {@link java.io.File}.
     *   <li> {@link java.nio.file.Path} is it is associated with the default provider
     *   <li> URLs and URis of {@code file:} schemes.
     *   <li> Groovy Closures.
     *   <li> {@link java.util.concurrent.Callable}.
     *   <li> {@link org.gradle.api.provider.Provider}.
     *   <li> {@link org.gradle.api.file.Directory} (Gradle 4.1+)
     *   <li> {@link org.gradle.api.resources.TextResource}
     * </ul>
     *
     * @param project Project context
     * @param file
     * @return File instance.
     *
     * @since 0.17.0
     */
    static File fileize(Project project, Object file) {
        if (LegacyLevel.PRE_4_1 && file instanceof Provider) {
            fileize(project, ((Provider) file).get())
        } else {
            project.file(file)
        }
    }

    /** Converts a collection of file-like objects to a a list of  {@link java.io.File} instances with project context.
     *
     * It will convert anything that the singular version of {@link #fileize(Project project, Object o)} can do.
     * In addition it will recursively resolve any collections that result out of resolving the supplied items.
     *
     * @param project Project context
     * @param files List of object to evaluate
     * @return LIst of resolved files.
     */
    static List<File> fileize(Project project, Iterable<Object> files) {
        List<File> collection = []

        for (Object item in files) {
            if (isIterableProperty(item)) {
                resolveIterablePropertyTo(project, collection, item)
            } else {
                switch (item) {
                    case Map:
                        collection.addAll(fileize(project, (Iterable) ((Map) item).values()))
                        break
                    case Iterable:
                        collection.addAll(fileize(project, (Iterable) item))
                        break
                    case Provider:
                        resolveSingleItemOrIterableTo(project, collection, ((Provider) item).get())
                        break
                    case Callable:
                        resolveSingleItemOrIterableTo(project, collection, ((Callable) item).call())
                        break
                    default:
                        collection.add(fileize(project, item))
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
     * @since 0.17.0
     */
    static void updateFileProperty(Project project, Provider<File> provider, Object file) {
        if (isProperty(provider)) {
            Provider<File> newProvider = project.provider({ ->
                fileize(project, file)
            } as Callable<File>)

            updateProperty(provider, newProvider)
        } else {
            throw new NotSupportedException('Provider has to be Property<File>. Upgradle to Gradle 4.3 or later.')
        }
    }

    @CompileDynamic
    private static void updateProperty(Provider<File> prop, Provider<File> newProp) {
        ((Property<File>) prop).set(newProp)
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
    static private void resolveIterablePropertyTo(Project project, List<File> files, Object o) {
        files.addAll(fileize(project, o.get()))
    }

    static private void resolveSingleItemOrIterableTo(Project project, List<File> files, Object o) {
        if (o instanceof Iterable) {
            files.addAll(fileize(project, o))
        } else {
            files.addAll(fileize(project, [o]))
        }
    }

    @CompileDynamic
    @SuppressWarnings('UnnecessaryPackageReference')
    private static boolean isProperty(Object interrogee) {
        PRE_4_3 ? false : interrogee instanceof org.gradle.api.provider.Property
    }
}

