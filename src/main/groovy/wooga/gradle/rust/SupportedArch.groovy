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

/** Architectures supported by Rust.
 *
 * @since 0.1
 */
@CompileStatic
enum SupportedArch {

    AARCH_64('aarch64'),
    ARM('arm'),
    X86('i686'),
    X86_64('x86_64'),
    MIPS('mips'),
    MIPS_EL('mips_el'),
    MIPS_64('mips64'),
    MIPS_64_EL('mips64el'),
    PPC('powerpc'),
    PPC_64('powerpc64'),
    PPC_64_LE('powerpc64le'),
    S390('s390x')

    final String name

    private SupportedArch(final String name) {
        this.name = name
    }

    @Override
    String toString() {
        name
    }

    static SupportedArch fromArch(OperatingSystem.Arch arch) {
        switch (arch) {
            case OperatingSystem.Arch.X86:
                return X86
            case OperatingSystem.Arch.X86_64:
                return X86_64
            case OperatingSystem.Arch.POWERPC:
                return PPC
            default:
                throw new UnsupportedConfigurationException("Could not determine a supported default Rust architecture system from '${arch.name()}'")
        }
    }
}
