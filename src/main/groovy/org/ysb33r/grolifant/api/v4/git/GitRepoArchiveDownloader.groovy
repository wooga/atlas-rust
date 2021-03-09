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
package org.ysb33r.grolifant.api.v4.git

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.ysb33r.grolifant.api.errors.DistributionFailedException
import org.ysb33r.grolifant.api.v4.FileUtils
import org.ysb33r.grolifant.internal.v4.downloader.ArtifactDownloader
import org.ysb33r.grolifant.internal.v4.downloader.Downloader

import static org.ysb33r.grolifant.api.v4.FileUtils.listDirs
import static org.ysb33r.grolifant.api.v4.UriUtils.safeUri

/** Downloads an archive of a Git repository.
 *
 * @since 0.17.0
 */
@CompileStatic
class GitRepoArchiveDownloader {

    @SuppressWarnings('DuplicateStringLiteral')
    GitRepoArchiveDownloader(final CloudGitDescriptor descriptor, final Project project) {
        this.gitDescriptor = descriptor
        this.project = project
        this.downloader = Downloader.create(
            "${gitDescriptor.name}:${gitDescriptor.organisation}/${gitDescriptor.repository}",
            project
        )
        this.unpacker = new ArtifactDownloader.ArtifactUnpacker() {
            @Override
            @CompileDynamic
            void unpack(File source, File destDir) {
                final FileTree archiveTree = project.zipTree(source)
                project.copy {
                    from archiveTree
                    into destDir
                }
            }
        }

        this.verifyRoot = new ArtifactDownloader.ArtifactRootVerification() {
            @Override
            File verify(File unpackedRoot) {
                List<File> dirs = listDirs(unpackedRoot)
                if (dirs.empty) {
                    throw new DistributionFailedException(
                        "Download for '${safeUri(gitDescriptor.archiveUri)}' does not contain any directories. " +
                            'Expected to find exactly 1 directory.'
                    )
                }
                if (dirs.size() != 1) {
                    throw new DistributionFailedException(
                        "Download for '${safeUri(gitDescriptor.archiveUri)}' contains too many directories. " +
                            'Expected to find exactly 1 directory.'
                    )
                }
                dirs[0]
            }
        }
    }

    /** Returns the location which is the top or home folder for a distribution.
     *
     *  This value is affected by {@link #setDownloadRoot(java.io.File)} and
     *  the parameters passed in during construction time.
     *
     * @return Location of the distribution.
     */
    File getArchiveRoot() {
        ArtifactDownloader repoDownloader = new ArtifactDownloader(
            gitDescriptor.archiveUri,
            this.downloadRoot ?: project.gradle.gradleUserHomeDir,
            FileUtils.toSafeFile(
                "${gitDescriptor.name.toLowerCase()}-cache",
                gitDescriptor.organisation.toLowerCase(),
                gitDescriptor.repository.toLowerCase()
            ).path,
            verifyRoot,
            unpacker,
            null
        )

        repoDownloader.getFromCache(
            safeUri(gitDescriptor.archiveUri).toString(),
            project.gradle.startParameter.offline,
            this.downloader
        )
    }

    /** Sets a download root directory for the distribution.
     *
     * If not supplied the default is to use the Gradle User Home.
     * This method is provided for convenience and is mostly only used for testing
     * purposes.
     *
     * The folder will be created at download time if it does not exist.
     *
     * @param downloadRootDir Any writeable directory on the filesystem.
     */
    void setDownloadRoot(File downloadRootDir) {
        this.downloadRoot = downloadRootDir
    }

    private final Project project
    private final CloudGitDescriptor gitDescriptor
    private final ArtifactDownloader.ArtifactUnpacker unpacker
    private final ArtifactDownloader.ArtifactRootVerification verifyRoot
    private final Downloader downloader
    private File downloadRoot
}
