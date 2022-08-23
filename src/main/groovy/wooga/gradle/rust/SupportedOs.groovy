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

package wooga.gradle.rust

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.core.OperatingSystem

import static wooga.gradle.rust.SupportedAbi.*
import static wooga.gradle.rust.SupportedArch.*

/** Operating systems supported by Rust
 *
 * @since 0.1
 */
@CompileStatic
enum SupportedOs {

    // NOTE: When listing ABIs, the default ABI for the platform must be the first in the list.
    FREEBSD('freebsd', 'unknown', [X86_64]),
    LINUX(
            'linux',
            'unknown',
            [AARCH_64, ARM, MIPS, MIPS_64, MIPS_64_EL, MIPS_EL, PPC, PPC_64, PPC_64_LE, S390, X86, X86_64],
            [GNU, GNU_EABI, GNU_EABIHF, GNU_ABI64]
    ),
    MACOS('darwin', 'apple', [X86_64, X86, AARCH_64]),
    NETBSD('netbsd', 'unknown', [X86_64]),
    WINDOWS('windows', 'pc', [X86, X86_64], [MSVC, GNU])

    final String name
    final String platform

    boolean hasAbiFlavours() {
        !abiFlavours.empty
    }

    boolean validAbi(abi) {
        (hasAbiFlavours() && abiFlavours.contains(abi)) || (!hasAbiFlavours() && abi == null)
    }

    boolean validArch(arch) {
        archList.contains(arch)
    }

    static SupportedOs fromOS(OperatingSystem os) {
        if (os.isFreeBSD()) {
            FREEBSD
        } else if (os.isLinux()) {
            LINUX
        } else if (os.isMacOsX()) {
            MACOS
        } else if (os.isNetBSD()) {
            NETBSD
        } else if (os.isWindows()) {
            WINDOWS
        } else {
            throw new UnsupportedConfigurationException("Could not determine a supported Rust operating system from '${os.name}'")
        }
    }

    private SupportedOs(
            final String name,
            final String platform,
            final List<SupportedArch> archList,
            final List<SupportedAbi> abis = null
    ) {
        this.name = name
        this.platform = platform
        this.archList = archList
        this.abiFlavours = abis ?: ([] as List<SupportedAbi>)
    }

    private final List<SupportedArch> archList
    private final List<SupportedAbi> abiFlavours

    @Override
    String toString() {
        return "${name}-${platform}"
    }
}
