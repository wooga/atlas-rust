//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2018
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================
//

package wooga.gradle.rust.internal

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.ysb33r.grolifant.api.OperatingSystem
import org.ysb33r.grolifant.api.errors.DistributionFailedException
import org.ysb33r.grolifant.api.v4.AbstractDistributionInstaller
import wooga.gradle.rust.SupportedAbi
import wooga.gradle.rust.SupportedArch
import wooga.gradle.rust.SupportedOs
import wooga.gradle.rust.UnsupportedConfigurationException

import java.nio.file.Files
import java.nio.file.Paths

import static wooga.gradle.rust.SupportedAbi.*
import static wooga.gradle.rust.SupportedArch.*
import static wooga.gradle.rust.SupportedOs.*

/** Installs Rust distributions.
 *
 * @since 0.1
 */
@CompileStatic
class RustInstaller extends AbstractDistributionInstaller {

    static final String DISTPATH = 'native'
    static final OperatingSystem OS = OperatingSystem.current()
    static final OperatingSystem.Arch ARCH = OS.getArch()
    static final String INSTALLDIR_POSTFIX = '.i'

    /** The URI where to retrieve Rust binaries.
     * This is a global setting which is not meant to be overridden direcrly by the user.
     */
    static String baseURI = System.getProperty('org.ysb33r.gradle.rust.releases.uri') ?: 'https://static.rust-lang.org/dist'

    /** Returns the default ABI for a given operating system & architecture combination.
     *
     * @param os Supported Rust operating system
     * @param arch Supported Rust architecture
     * @return Default ABI or {@code null} is platform does not have ABI flavours
     */
    static SupportedAbi defaultAbi(SupportedOs os, SupportedArch arch) {
        switch (os) {
            case LINUX:
                switch (arch) {
                    case ARM:
                        return GNU_EABI
                    case MIPS_64:
                    case MIPS_64_EL:
                        return GNU_ABI64
                    default:
                        return GNU
                }
            case WINDOWS:
                return MSVC
            default:
                if (!os.hasAbiFlavours()) {
                    return null
                } else {
                    throw new UnsupportedConfigurationException(os, arch)
                }
        }
    }

    /** Creates a default Rust installer for the platform the build is running on.
     *
     * Architecture model, ABI and operating system is derived from the current OS and architecture
     * the build is running on.
     *
     * @param project Project thie installer is attached to.
     * @param version Version of Rust to install
     * @theow {@link UnsupportedConfigurationException} if operating system, architecture or ABI cannot be
     * deduced from current build.
     */
    RustInstaller(Project project, final String version) {
        super('Rust Distribution', version, DISTPATH, project)

        this.rustOs = fromOS(OS)
        this.rustArch = fromArch(ARCH)
        this.installerPackageExtension = (OS.windows) ? ".msi" : ".tar.gz"
        this.rustcPathPart = (OS.windows) ? "bin/rustc.exe" : "bin/rustc"
        this.cargoPathPart = (OS.windows) ? "bin/cargo.exe" : "bin/cargo"
        if (!rustOs.validArch(rustArch)) {
            throw new UnsupportedConfigurationException(rustOs, rustArch)
        }

        this.rustAbi = defaultAbi(rustOs, rustArch)

        if (!rustOs.validAbi(rustAbi)) {
            throw new UnsupportedConfigurationException(rustOs, rustAbi)
        }
    }

    /** Installs a specific rust distribution to be used for cross-compilation purposes.
     *
     * @param project Project thie installer is attached to.
     * @param version Version of Rust to install
     * @param os Operating system to install for.
     * @param arch Architecture to install for.
     * @param abi ABI to support. (Can be null for platforms which do not have ABI flavours.
     *
     * @throw {@link UnsupportedConfigurationException} is architecture or ABI does not match provided
     * operating system.
     */
    RustInstaller(Project project,
                  final String version, final SupportedOs os, final SupportedArch arch, final SupportedAbi abi) {
        super('Rust Distribution', version, DISTPATH, project)

        if (!os.validArch(arch)) {
            throw new UnsupportedConfigurationException(os, arch)
        }

        if (!os.validAbi(abi)) {
            throw new UnsupportedConfigurationException(os, abi)
        }

        this.rustOs = os
        this.rustArch = arch
        this.rustAbi = abi
    }

    /** Obtains a download URI for Rust distribution given a specific version.
     *
     * @param version Rust version
     * @return Download URI.
     */
    @Override
    URI uriFromVersion(String version) {
        "${baseURI}/rust-${version}-${rustArch}-${rustOs.platform}-${rustOs.name}${abiString}${installerPackageExtension}".toURI()
    }

    /** Returns the location of the {@code cargo executable}.
     *
     * @return Location of {@code cargo} or {@code null} if not found.
     */
    File getCargoExecutablePath() {
        getBinary(cargoPathPart)
    }

    /** Returns the location of the {@code rustc executable}.
     *
     * @return Location of {@code rustc} or {@code null} if not found.
     */
    File getRustcExecutablePath() {
        getBinary(rustcPathPart)
    }

    File getHomePath() {
        distributionRoot
    }

    @Override
    protected File getAndVerifyDistributionRoot(File distDir, String distributionDescription) {
        List<File> dirs = listDirs(distDir)
        if (dirs.isEmpty()) {
            throw new DistributionFailedException("Rust distribution '${distributionDescription}' does not contain any directories. Expected to find 1 or directories.")
        }
        if (dirs.size() > 2) {
            throw new DistributionFailedException("Rust distribution '${distributionDescription} contains too many directories. Expected to find 1 or 2 directories.")
        }

        File unpackedRoot = dirs.find { File it ->
            !it.name.endsWith(INSTALLDIR_POSTFIX)
        }

        File installRoot = getRustInstallDir(unpackedRoot)
        installRust(unpackedRoot, installRoot)
        installRoot
    }

    @Override
    protected void unpack(File srcArchive, File destDir) {
        if (OS.windows) {
            final String name = srcArchive.name.toLowerCase()
            if (name.endsWith('.msi')) {
                def dest = new File(srcArchive.parentFile, srcArchive.name.replace(".msi", ""))
                dest.mkdirs()
                dest = new File(dest, "install.msi")
                Files.copy(Paths.get(srcArchive.path), Paths.get(dest.path))
                return
            }
            throw new IllegalArgumentException("${name} is not a supported archive type")
        }
        super.unpack(srcArchive, destDir)
    }

    private static File getRustInstallDir(File unpackedDir) {
        new File(unpackedDir.parentFile, "${unpackedDir.name}${INSTALLDIR_POSTFIX}")
    }

    private void installRust(File unpackedDir, File installDir) {
        def project = this.project
        if (!new File(installDir, cargoPathPart).exists() || !new File(installDir, rustcPathPart).exists()) {
            if (OS.windows) {
                project.exec(new Action<ExecSpec>() {
                    @Override
                    void execute(ExecSpec exec) {
                        exec.with {
                            executable("msiexec.exe")
                            args("/I")
                            args("${unpackedDir}\\install.msi")
                            args("/QN")
                            args("/L*!", "${unpackedDir}\\install_log.log")
                            args("INSTALLDIR=${installDir.absolutePath}")
                            args('ALLUSERS=2', 'MSIINSTALLPERUSER=1')
                            args('ADDDEFAULT=Rustc,Cargo,Std')
                        }

                    }
                })
            } else {
                ExecResult r = project.exec(new Action<ExecSpec>() {
                    @Override
                    void execute(ExecSpec exec) {
                        exec.with {
                            executable("${unpackedDir}/install.sh")
                            args("--destdir=${installDir.absolutePath}")
                            args("--prefix=/")
                            args("--without=rust-docs")
                            if (project.logging.level >= LogLevel.INFO) {
                                args("--verbose")
                            }
                        }
                    }
                })
            }
            installDir.mkdirs()

            println("done")
        } else {
            project.logger.debug("Not installing Rust again as it already exists in ${installDir}")
        }

    }

    private File getBinary(final String relativePath) {
        File binary = new File(distributionRoot, relativePath)
        binary.exists() ? binary : null
    }

    private String getAbiString() {
        "${rustOs.hasAbiFlavours() ? '-' + rustAbi : ''}"
    }

    private final SupportedArch rustArch
    private final SupportedOs rustOs
    private final SupportedAbi rustAbi
    private final String cargoPathPart
    private final String rustcPathPart
    private final String installerPackageExtension
}
