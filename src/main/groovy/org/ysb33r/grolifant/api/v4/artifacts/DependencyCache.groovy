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
package org.ysb33r.grolifant.api.v4.artifacts

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.api.CheckSumVerification
import org.ysb33r.grolifant.internal.v4.downloader.ArtifactDownloader
import org.ysb33r.grolifant.internal.v4.downloader.ArtifactDownloader.RequiresDownload
import org.ysb33r.grolifant.internal.v4.downloader.Downloader

/** A custom implementation of a dependency cache when the one from Gradle
 * might not be directly accessible.
 *
 * @since 0.17.0
 */
@CompileStatic
class DependencyCache  {

    static interface PostDownloadProcessor {
        void process(File artifact)
    }

    static interface CacheRefresh {
        void refresh(URI downloadURI, File localPath)
    }

    @SuppressWarnings('ParameterCount')
    DependencyCache(
        final Project project,
        final String cacheName,
        final String cacheRelativePath,
        final CacheRefresh requiresDownload,
        final CheckSumVerification checksumVerification,
        final PostDownloadProcessor postprocessor
    ) {
        this.project = project
        this.downloader = Downloader.create(cacheName, project)
        this.relativePath = cacheRelativePath
        this.downloadRoot = new File(project.gradle.gradleUserHomeDir, cacheRelativePath)
        this.requiresDownload = requiresDownload
        this.checkSumVerification = checkSumVerification
        this.postDownloadProcessor = postprocessor
    }

    /** Extract an artifact from cache.
     *
     * If the artifact is not available it will be downloaded.
     *
     * The download process is aware of Gradle offline mode and might throw an exception if Gradle is offline and the
     * artifact is not in the cache.
     *
     * @param downloadUri URI for downloading thew artifact
     * @param artifactPath Relative path below root to be used.
     * @param artifactFileName filename of artifact as it should be stored.
     * @return Parent file of artifact.
     */
    File getFromCache(final URI downloadUri, final String artifactPath, final String artifactFileName) {
        RequiresDownload downloadAgain = new RequiresDownload() {
            @Override
            boolean download(URI downloadURI, File localPath) {
                !localPath.file || requiresDownload.refresh(downloadUri, localPath)
            }
        }

        ArtifactDownloader artifactDownloader = new ArtifactDownloader(
            downloadUri,
            downloadRoot,
            artifactPath,
            downloadAgain,
            checkSumVerification
        )

        File artifact = artifactDownloader.getFromCache(
            artifactFileName,
            project.gradle.startParameter.offline,
            this.downloader
        )

        if (postDownloadProcessor) {
            postDownloadProcessor.process(artifact)
        }

        artifact.parentFile
    }

    /** Sets a root directory for the cache.
     *
     * If not supplied the default is to use the Gradle User Home.
     * This method is provided for convenience and is mostly only used for testing
     * purposes.
     *
     * The folder will be created at download time if it does not exist.
     *
     * Setting this is not concurrent-safe, so should only set set when the cache is created and not when
     * the cache is used for downloading items.
     *
     * @param downloadRootDir Any writeable directory on the filesystem.
     */
    void setDownloadRoot(File downloadRootDir) {
        this.downloadRoot = new File(downloadRootDir, this.relativePath)
    }

    private final Project project
    private final Downloader downloader
    private final String relativePath
    private final CacheRefresh requiresDownload
    private final CheckSumVerification checkSumVerification
    private final PostDownloadProcessor postDownloadProcessor
    private File downloadRoot

}
