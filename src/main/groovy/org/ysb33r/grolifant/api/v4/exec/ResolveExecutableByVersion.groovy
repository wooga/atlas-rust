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
package org.ysb33r.grolifant.api.v4.exec

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.api.v4.AbstractDistributionInstaller
import org.ysb33r.grolifant.api.v4.StringUtils

/** Uses an implementation of an {@link AbstractDistributionInstaller} to find an exe by version number.
 *
 * @since 0.17.0
 */
@CompileStatic
class ResolveExecutableByVersion<T extends AbstractDistributionInstaller> implements NamedResolvedExecutableFactory {

    static interface DownloaderFactory<T> {
        /** Creates a downloader
         *
         * @param options An arbitrary map of options. This could interpreted by the factory or simply ignored.
         * @param version The version of the exe that is required.
         * @param project The associated Gradle project
         * @return An instance of a downloader
         */
        T create(Map<String, Object> options, String version, Project project)
    }

    static interface DownloadedExecutable<T> {
        /** Given a downloader resolve the path to the exe.
         *
         * @param downloader Downloader as potentially supplied via a {@link DownloaderFactory}.
         * @return Path on filesystem to exe.
         */
        File getPath(T downloader)
    }

    /** Returns {@code name}.
     */
    final String name = NAME

    /** Resolve by downloadable version.
     *
     * @param project Associated project.
     * @param factory Factory for creating downloaders.
     * @param resolver Resolves execution path from download item.
     */
    ResolveExecutableByVersion(Project project, final DownloaderFactory factory, final DownloadedExecutable resolver) {
        this.project = project
        this.factory = factory
        this.resolver = resolver
    }

    /** Creates {@link org.ysb33r.grolifant.api.v4.exec.ResolvableExecutable} from a Packer version.
     *
     * @param options Passed through to the downloader, ignored otherwise.
     * @param from Anything convertible to a string that contains a valid version for the
     *   specific exe / tool / distribution.
     * @return The resolved exe.
     */
    @Override
    ResolvableExecutable build(Map<String, Object> options, Object from) {
        T dnl = (T) (factory.create(options, StringUtils.stringize(from), project))
        DownloadedExecutable scopedResolver = this.resolver
        new ResolvableExecutable() {
            @Override
            File getExecutable() {
                scopedResolver.getPath(dnl)
            }
        }
    }

    private final Project project
    private final DownloaderFactory factory
    private final DownloadedExecutable resolver
    private static final String NAME = 'version'
}
