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
import groovy.transform.PackageScope
import org.apache.commons.io.FileSystemUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTree
import org.gradle.internal.os.OperatingSystem
import org.tukaani.xz.XZInputStream
import org.ysb33r.grolifant.api.BaseProgressLogger
import org.ysb33r.grolifant.api.CheckSumVerification
import org.ysb33r.grolifant.api.errors.ChecksumFailedException
import org.ysb33r.grolifant.api.errors.DistributionFailedException
import org.ysb33r.grolifant.internal.v4.downloader.ArtifactDownloader
import org.ysb33r.grolifant.internal.v4.downloader.Downloader
import org.ysb33r.grolifant.internal.v4.msi.LessMSIUnpackerTool

import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

import static org.ysb33r.grolifant.api.v4.UriUtils.safeUri

/** Common functionality to be able to download a SDK and use it within Gradle
 *
 */
@CompileStatic
abstract class AbstractDistributionInstaller {

    static final boolean IS_WINDOWS = OperatingSystem.current().windows

    /** Creates a download URI from a given distribution version
     *
     * @param version Version of the distribution to download
     * @return
     */
    abstract URI uriFromVersion(final String version)

    /** Set candidate name for SdkMan if the latter should be searched for installed versions
     *
     * @param sdkCandidateName SDK Candidate name. This is the same names that will be shown when
     *   running {@code sdk list candidates} on the script-line.
     */
    void setSdkManCandidateName(final String sdkCandidateName) {
        this.sdkManCandidateName = sdkCandidateName
    }

    /** Add patterns for files to be marked exe,
     *
     * Calling this method multiple times simply appends for patterns
     * @param relPaths One or more ANT-stype include patterns
     */
    void addExecPattern(String... relPaths) {
        this.execPatterns.addAll(relPaths as List)
    }

    /** Set a checksum that needs to be verified against downloaded archive
     *
     * @param cs SHA-256 Hex-encoded checksum
     */
    void setChecksum(final String cs) {
        if (cs.length() != 64 || !(cs ==~ /[\p{Digit}\p{Alpha}]{64}/)) {
            throw new IllegalArgumentException('Not a valid SHA-256 checksum')
        }
        this.checksum = cs.toLowerCase()
    }

    /** Returns the location which is the top or home folder for a distribution.
     *
     *  This value is affected by {@link #setDownloadRoot(java.io.File)} and
     *  the parameters passed in during construction time.
     *
     * @return Location of the distribution.
     */
    File getDistributionRoot() {
        // tag::download_logic[]
        File location = locateDistributionInCustomLocation(distributionVersion) // <1>

        if (location == null && this.sdkManCandidateName) { // <2>
            location = distFromSdkMan
        }

        location ?: distFromCache // <3>
        // end::download_logic[]
    }

    /** Override this method to provide alternative means to look for distributions.
     *
     * @version Version of distribution to locate
     *
     * @return Location of distribution or null if none were found.
     */
    @SuppressWarnings('UnusedMethodParameter')
    File locateDistributionInCustomLocation(final String version) {
        null
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

    /** Attempts to locate distribution in the list of SdkMan candidates.
     *
     * @return Location of the distribution if found in the candidate area.
     */
    @PackageScope
    File getDistFromSdkMan() {
        File sdkCandidate = new File(
            "${System.getProperty('user.home')}/.sdkman/${sdkManCandidateName}/${distributionVersion}"
        )

        sdkCandidate.exists() && sdkCandidate.directory ? sdkCandidate : null
    }

    /** Creates a distribution it it does not exist already.
     *
     * @return Location of distribution
     */
    @PackageScope
    File getDistFromCache() {
        URI distUri = uriFromVersion(distributionVersion)
        String textUri = safeUri(distUri)
        String chsum = this.checksum

        AbstractDistributionInstaller callback = this

        ArtifactDownloader.ArtifactRootVerification verifyRoot = new ArtifactDownloader.ArtifactRootVerification() {
            @Override
            File verify(File unpackedRoot) {
                callback.getAndVerifyDistributionRoot(unpackedRoot, textUri)
            }
        }

        ArtifactDownloader.ArtifactUnpacker unpacker = new ArtifactDownloader.ArtifactUnpacker() {
            @Override
            void unpack(File source, File destDir) {
                callback.unpack(source, destDir)
            }
        }

        CheckSumVerification verifyChecksum = new CheckSumVerification() {

            @Override
            void verify(File downloadedTarget) {
                callback.verifyDownloadChecksum(textUri, downloadedTarget, chsum)
            }

            /** Returns the checksum in question.
             *
             * @return Checksum. Can be {@code null}.
             */
            @Override
            String getChecksum() {
                chsum
            }
        }

        ArtifactDownloader downloader = new ArtifactDownloader(
            distUri,
            downloadRoot ?: project.gradle.gradleUserHomeDir,
            basePath,
            verifyRoot,
            unpacker,
            verifyChecksum
        )

        downloader.getFromCache(
            "${distributionName}:${distributionVersion}",
            project.gradle.startParameter.offline,
            this.downloader
        )
    }

    /** Returns the logger currently in use.
     *
     * @return Wrapper logger instance
     */
    protected BaseProgressLogger getLogger() {
        this.downloader.progressLogger
    }

    /** Creates setup for installing to a local cache.
     *
     * @param distributionName Descriptive name of the distribution
     * @param distributionVersion Version of the distribution to obtain
     * @param basePath Relative path below Gradle User Home to create cache for all version of this distribution type.
     * @param project Gradle project that this downloader is attached to.
     */
    protected AbstractDistributionInstaller(
        final String distributionName,
        final String distributionVersion,
        final String basePath,
        final Project project
    ) {
        this.distributionName = distributionName
        this.distributionVersion = distributionVersion
        this.project = project
        this.basePath = basePath
        this.downloader = Downloader.create("${distributionName}:${distributionVersion}", project)
    }

    /** Validates that the unpacked distribution is good.
     *
     * <p> The default implementation simply checks that only one directory should exist and then uses that.
     * You should override this method if your distribution in question does not follow the common practice of one
     * top-level directory.
     *
     * @param distDir Directory where distribution was unpacked to.
     * @param distributionDescription A descriptive name of the distribution
     * @return The directory where the real distribution now exists. In the default implementation it will be
     *   the single directory that exists below {@code distDir}.
     *
     * @throw {@link org.ysb33r.grolifant.api.errors.DistributionFailedException} if distribution failed to
     *   meet criteria.
     */
    protected File getAndVerifyDistributionRoot(final File distDir, final String distributionDescription) {
        List<File> dirs = listDirs(distDir)
        if (dirs.empty) {
            throw new DistributionFailedException("${distributionName} '${distributionDescription}' " +
                'does not contain any directories. Expected to find exactly 1 directory.'
            )
        }
        if (dirs.size() != 1) {
            throw new DistributionFailedException(
                "${distributionName} '${distributionDescription} contains too many directories. " +
                    'Expected to find exactly 1 directory.'
            )
        }
        dirs[0]
    }

    /** Verifies the checksum (if provided) of a newly downloaded distribution archive.
     *
     * <p> Only SHA-256 is supported at this point in time.
     *
     * @param sourceUrl The URL/URI where it was downloaded from
     * @param localCompressedFile The location of the downloaded archive
     * @param expectedSum The expected checksum. Can be null in which case no checks will be performed.
     *
     * @throw {@link org.ysb33r.grolifant.api.errors.ChecksumFailedException} if the checksum did not match
     */
    protected void verifyDownloadChecksum(
        final String sourceUrl, final File localCompressedFile, final String expectedSum) {
        if (expectedSum != null) {
            String actualSum = calculateSha256Sum(localCompressedFile)
            if (this.checksum != actualSum) {
                localCompressedFile.delete()
                throw new ChecksumFailedException(
                    distributionName,
                    sourceUrl,
                    localCompressedFile,
                    expectedSum,
                    actualSum
                )
            }
        }
    }

    /** Provides a list of directories below an unpacked distribution
     *
     * @param distDir Unpacked distribution directory
     * @return List of directories. Can be empty is nothing was unpacked or only files exist within the
     *   supplied directory.
     */
    protected List<File> listDirs(File distDir) {
        FileUtils.listDirs(distDir)
    }

    /** Unpacks a downloaded archive.
     *
     * <p> The default implementation supports the following formats:
     *
     * <ul>
     *   <li>zip</li>
     *   <li>tar</li>
     *   <li>tar.gz & tgz</li>
     *   <li>tar.bz2 & tbz</li>
     *   <li>tar.xz</li>
     * </ul>
     *
     * <p> If you need MSI support you need to override this method and call out to the
     * provided {@link #unpackMSI} method yourself.
     *
     * @param srcArchive The location of the download archive
     * @param destDir The directory where the archive needs to be unpacked into
     */
    @CompileDynamic
    protected void unpack(final File srcArchive, final File destDir) {
        final FileTree archiveTree = compressedTree(srcArchive)
        final List<String> patterns = this.execPatterns

        final Action<FileCopyDetails> setExecMode = { FileCopyDetails fcd ->
            if (!fcd.directory) {
                fcd.mode = fcd.mode | 0111
            }
        }

        project.copy {
            from archiveTree
            into destDir

            if (!IS_WINDOWS && !patterns.empty) {
                filesMatching(patterns, setExecMode)
            }
        }
    }

    /** Returns the attached project
     *
     * @return Attached project instance
     */
    protected Project getProject() {
        this.project
    }

    /** Provides the capability of unpacking an MSI file under Windows by calling out to {@code msiexec}.
     *
     * <p> {@code msiexec} will be located via the system search path.
     *
     * @param srcArchive The location of the download MSI
     * @param destDir The directory where the MSI needs to be unpacked into
     * @param env Environment to use. Can be null or empty in which case a default environment will be used
     */
    protected void unpackMSI(File srcArchive, File destDir, final Map<String, String> env) {
        if (IS_WINDOWS) {
            new LessMSIUnpackerTool(project).unpackMSI(srcArchive, destDir, env)
        } else {
            throw new DistributionFailedException('MSI unpacking is only supported under Windows')
        }
    }

    private String calculateSha256Sum(final File file) {
        file.withInputStream { InputStream content ->
            MessageDigest digest = MessageDigest.getInstance('SHA-256')
            content.eachByte(4096) { bytes, len -> digest.update(bytes, 0, len) }
            digest.digest().encodeHex().toString()
        }
    }

    private FileTree compressedTree(final File srcArchive) {
        final String name = srcArchive.name.toLowerCase()
        if (name.endsWith('.zip')) {
            return project.zipTree(srcArchive)
        } else if (name.endsWith('.tar')) {
            return project.tarTree(srcArchive)
        } else if (name.endsWith('.tar.gz') || name.endsWith('.tgz')) {
            return project.tarTree(project.resources.gzip(srcArchive))
        } else if (name.endsWith('.tar.bz2') || name.endsWith('.tbz')) {
            return project.tarTree(project.resources.bzip2(srcArchive))
        } else if (name.endsWith('.tar.xz')) {
            final File unpackedXZTar = File.createTempFile(
                srcArchive.name.replaceAll(~/.xz$/, ''),
                '$$$'
            )
            unpackedXZTar.withOutputStream { OutputStream xz ->
                srcArchive.withInputStream { tarXZ ->
                    new XZInputStream(tarXZ).withStream { strm ->
                        xz << strm
                    }
                }
            }
            return project.tarTree(unpackedXZTar)
        } else if(name.endsWith('.msi')) {
            def dest = new File(srcArchive.parentFile, srcArchive.name.replace(".msi", ""))
            dest.mkdirs()
            dest = new File(dest, "install.msi")
            Files.copy(Paths.get(srcArchive.path), Paths.get(dest.path))
            return project.fileTree(srcArchive)
        }

        throw new IllegalArgumentException("${name} is not a supported archive type")
    }

    private String sdkManCandidateName
    private String checksum
    private File downloadRoot
    private final String distributionName
    private final String distributionVersion
    private final Project project
    private final List<String> execPatterns = []
    private final String basePath
    private final Downloader downloader

}
