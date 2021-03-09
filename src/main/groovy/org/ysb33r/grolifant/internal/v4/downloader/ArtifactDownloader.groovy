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
package org.ysb33r.grolifant.internal.v4.downloader

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.wrapper.PathAssembler
import org.gradle.wrapper.WrapperConfiguration
import org.ysb33r.grolifant.api.ExclusiveFileAccess
import org.ysb33r.grolifant.api.CheckSumVerification
import org.ysb33r.grolifant.api.errors.DistributionFailedException

import java.util.concurrent.Callable

import static org.ysb33r.grolifant.api.v4.FileUtils.listDirs
import static org.ysb33r.grolifant.api.v4.UriUtils.safeUri

/**
 * @since 0.8
 */
@CompileStatic
class ArtifactDownloader {

    /** Verifies that the unpacked artifact root is sane.
     *
     */
    static interface ArtifactRootVerification {
        /** Verifies a artifact root
         *
         * @param unpackedRoot Directory where unpacked package is unpacked
         *   (or in case of a single file the parent directory).
         * @return The correctly verified root. (Could be a child of the unpacked root).
         * @throw Throws{@link DistributionFailedException} if verification failed
         */
        File verify(final File unpackedRoot)
    }

    /** Unpacks a downloaded artifact
     *
     */
    static interface ArtifactUnpacker {
        /** Unpacks the source archive
         *
         * @param source Source archive to unpack (Can also be a single non-archive file).
         * @param destDir Destination directory.
         */
        void unpack(File source, File destDir)
    }

    /** Indicates whether a download is required.
     *
     */
    static interface RequiresDownload {
        /** Indicates whether download is required.
         *
         * @param downloadURI URI where file is to be downloaded from
         * @param localPath Path to local file. (File does not need to exist locally).
         * @return {@b true} is the file should be downloaded.
         */
        boolean download(URI downloadURI, File localPath)
    }

    /** Creates an instance which takes care of the actual downloading and caching.
     *
     * @param downloadURI URI to download package from.
     * @param downloadRoot Base directory where to download to.
     * @param basePath Relative path to the downloadRoot.
     * @param verifyArtifactRoot Callback to verify the unpacked artifact. Never {@code null}.
     * @param verifyDownloadChecksum Callback to verify the checksum of the downloaded target.
     *   Can be {@code null}.
     */
    @SuppressWarnings('ParameterCount')
    ArtifactDownloader(
        final URI downloadURI,
        final File downloadRoot,
        final String basePath,
        final ArtifactRootVerification verifyArtifactRoot,
        final ArtifactUnpacker unpacker,
        final CheckSumVerification verifyDownloadChecksum
    ) {
        this.downloadURI = downloadURI
        this.downloadRoot = downloadRoot
        this.basePath = basePath
        this.verifyDownloadChecksum = verifyDownloadChecksum
        this.verifyArtifactRoot = verifyArtifactRoot
        this.unpacker = unpacker
        this.requiresDownload = DOWNLOAD_IF_NOT_EXISTS
    }

    /** Download an artifact without unpacking it.
     *
     * @param downloadURI URI to download package from.
     * @param downloadRoot Base directory where to download to.
     * @param basePath Relative path to the downloadRoot.
     * @param requiresDownload Indicates whether download is required.
     * @param verifyDownloadChecksum Callback to verify the checksum of the downloaded target.
     *   Can be {@code null}.
     */
    ArtifactDownloader(
        final URI downloadURI,
        final File downloadRoot,
        final String basePath,
        final RequiresDownload requiresDownload,
        final CheckSumVerification verifyDownloadChecksum
    ) {
        this.downloadURI = downloadURI
        this.downloadRoot = downloadRoot
        this.basePath = basePath
        this.verifyDownloadChecksum = verifyDownloadChecksum
        this.requiresDownload = requiresDownload
    }

    /** Creates a distribution/file it it does not exist already.
     *
     * @param description Name of the downloaded entity.
     * @param offlineMode Whether to operate in download mode.
     * @param downloadInstance Download & logger instances to use
     *
     * @return Location of distribution
     */
    File getFromCache(final String description, boolean offlineMode, final Downloader downloadInstance) {
        final WrapperConfiguration configuration = newWrapperConfiguration
        final PathAssembler pathAssembler = new PathAssembler(downloadRoot)
        final PathAssembler.LocalDistribution localDistribution = pathAssembler.getDistribution(configuration)
        final File distDir = localDistribution.distributionDir

        // This is not always compressed or a Zipfile. We still have to use the method as the original
        // Wrapper Configuration assumes that only ZIP files are downloaded.
        final File localDownloadedFile = localDistribution.zipFile

        Callable<File> downloadAction = new DownloadAction(
            offlineMode: offlineMode,
            description: description,
            distDir: distDir,
            localDownloadedFile: localDownloadedFile,
            markerFile: new File(localDownloadedFile.parentFile, localDownloadedFile.name + '.ok'),
            downloadURI: this.downloadURI,
            distributionUrl: configuration.distribution,
            verifyArtifactRoot: this.verifyArtifactRoot,
            requiresDownload: this.requiresDownload,
            downloadInstance: downloadInstance,
            verifyDownloadChecksum: this.verifyDownloadChecksum,
            unpacker: this.unpacker
        )
        exclusiveFileAccessManager.access(localDownloadedFile, downloadAction)
    }

    private WrapperConfiguration getNewWrapperConfiguration() {
        final WrapperConfiguration configuration = new WrapperConfiguration()
        configuration.distribution = this.downloadURI
        configuration.distributionPath = configuration.zipPath = basePath

        configChecksum = configuration
    }

    @CompileDynamic
    private WrapperConfiguration setConfigChecksum(WrapperConfiguration configuration) {
        if (verifyDownloadChecksum) {
            configuration.distributionSha256Sum = verifyDownloadChecksum.checksum
        }

        configuration
    }

    static private final RequiresDownload DOWNLOAD_IF_NOT_EXISTS = new RequiresDownload() {
        @Override
        boolean download(URI downloadURI, File localPath) {
            !localPath.file
        }
    }

    private final ExclusiveFileAccess exclusiveFileAccessManager = new ExclusiveFileAccess(120000, 200)
    private final URI downloadURI
    private final File downloadRoot
    private final String basePath
    private final CheckSumVerification verifyDownloadChecksum
    private final ArtifactUnpacker unpacker
    private final ArtifactRootVerification verifyArtifactRoot
    private final RequiresDownload requiresDownload

    private static class DownloadAction implements Callable<File> {

        boolean offlineMode
        String description
        File distDir
        File markerFile
        File localDownloadedFile
        URI downloadURI
        URI distributionUrl
        ArtifactRootVerification verifyArtifactRoot
        RequiresDownload requiresDownload
        Downloader downloadInstance
        CheckSumVerification verifyDownloadChecksum
        ArtifactUnpacker unpacker

        @Override
        File call() throws Exception {
            if (distDir.directory && markerFile.file) {
                return verifyArtifactRoot.verify(distDir)
            }

            if (requiresDownload.download(downloadURI, localDownloadedFile)) {
                if (offlineMode && distributionUrl.scheme != 'file') {
                    throw new DistributionFailedException("Cannot download ${description} as currently offline")
                }

                File tmpDownloadedFile = new File(
                    localDownloadedFile.parentFile, "${localDownloadedFile.name}.part"
                )
                tmpDownloadedFile.delete()
                downloadInstance.progressLogger.log("Downloading ${safeUri(distributionUrl)}")
                downloadInstance.downloader.download(distributionUrl, tmpDownloadedFile)
                tmpDownloadedFile.renameTo(localDownloadedFile)
            }

            List<File> topLevelDirs = listDirs(distDir)
            for (File dir : topLevelDirs) {
                downloadInstance.progressLogger.log("Deleting directory ${dir.absolutePath}")
                dir.deleteDir()
            }

            if (verifyDownloadChecksum) {
                verifyDownloadChecksum.verify(localDownloadedFile)
            }

            if (unpacker) {
                downloadInstance.progressLogger.log(
                    "Unpacking ${localDownloadedFile.absolutePath} to ${distDir.absolutePath}"
                )
                unpacker.unpack(localDownloadedFile, distDir)
            }

            File root = verifyArtifactRoot ? verifyArtifactRoot.verify(distDir) : distDir
            markerFile.createNewFile()

            root
        }
    }

}
