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
package org.ysb33r.grolifant.internal.v4.msi

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.api.v4.AbstractDistributionInstaller
import org.ysb33r.grolifant.api.errors.DistributionFailedException

/** Downloads an MSI to use at a later stage.
 *
 * @since 0.4
 */
@CompileStatic
class LessMSIUnpackerTool extends AbstractDistributionInstaller {

    public static final String LESS_MSI_EXE = 'lessmsi.exe'
    public static final String LESSMSI_VERSION = '1.6.1'
    public static final String LESSMSI_DOWNLOAD_URI = 'https://github.com/activescott/lessmsi/releases/download'

    /** Creates setup for installing to a local cache.
     *
     * @param project Gradle project that this downloader is attached to.
     */
    LessMSIUnpackerTool(Project project) {
        super(
            'lessmsi',
            System.getProperty('org.ysb33r.gradle.olifant.lessmsi.version') ?: LESSMSI_VERSION,
            'native-binaries/lessmsi',
            project
        )
    }

    /** Creates a download URI from a given distribution version
     *
     * @param version Version of the distribution to download
     * @return
     */
    @Override
    @SuppressWarnings('LineLength')
    URI uriFromVersion(String version) {
        "${System.getProperty('org.ysb33r.gradle.olifant.lessmsi.uri') ?: LESSMSI_DOWNLOAD_URI}/v${version}/lessmsi-v${version}.zip".toURI()
    }

    /** Returns the path to the {@code lessmsi} exe.
     * Will force a download if not already downloaded.
     *
     * @return Location of {@code lessmsi} or null if not a supported operating system.
     */
    File getLessMSIExecutablePath() {
        File root = distributionRoot
        root ? new File(root, LESS_MSI_EXE) : null
    }

    /** Unpacks an MSI given the {@code lessmsi} exe downloaded by this incantation.
     *
     * @param srcArchive Location of MSI
     * @param destDir Directory to unpack to
     * @param env Environment to use when unpacking. If null or empty will add {@code TEMP}, {@code TMP}
     *   from Gradle environment.
     */
    void unpackMSI(File srcArchive, File destDir, final Map<String, String> env) {
        if (env) {
            doUnpackMSI(srcArchive, destDir, env)
        } else {
            doUnpackMSI(srcArchive, destDir, [
                TMP : System.getenv('TMP'),
                TEMP: System.getenv('TEMP')
            ])
        }
    }

    /** Validates that the unpacked distribution is good.
     *
     * @param distDir Directory where distribution was unpacked to.
     * @param distributionDescription A descriptive name of the distribution
     * @return {@code distDir} as {@code Packer} distributions contains only a single exe.
     *
     * @throw {@link org.ysb33r.grolifant.api.errors.DistributionFailedException} if distribution failed to
     *   meet criteria.
     */
    @Override
    protected File getAndVerifyDistributionRoot(File distDir, String distributionDescription) {
        File checkFor = new File(distDir, LESS_MSI_EXE)

        if (!checkFor.exists()) {
            throw new DistributionFailedException(
                "${checkFor.name} not found in downloaded ${distributionDescription} distribution."
            )
        }

        distDir
    }

    @CompileDynamic
    private doUnpackMSI(File srcArchive, File destDir, Map<String, String> env) {
        project.exec {
            executable lessMSIExecutablePath
            cmdArgs 'x', srcArchive.absolutePath, destDir.absolutePath
            environment env
        }
    }
}
