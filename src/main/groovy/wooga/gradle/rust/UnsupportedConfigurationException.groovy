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

/** The combination of operating system, architecture and/or ABI is not supported.
 *
 * @since 0.1
 */
@CompileStatic
class UnsupportedConfigurationException extends Exception {

    private final static MSG = 'is not a supported combination for Rust or this Gradle plugin'

    UnsupportedConfigurationException( final String msg ) {
        super(msg)
    }

    UnsupportedConfigurationException(SupportedOs os, SupportedArch arch ) {
        super("'${os.name}' + '${arch.name}' ${MSG}")
    }

    UnsupportedConfigurationException(SupportedOs os, SupportedAbi abi ) {
        super("'${os.name}' + '${abi.name ?: '(ABI not provided)'}' ${MSG}")
    }

    UnsupportedConfigurationException(SupportedOs os, SupportedArch arch, SupportedAbi abi) {
        super("'${os.name}' + '${arch.name}' + '${abi.name}' ${MSG}")

    }
}
