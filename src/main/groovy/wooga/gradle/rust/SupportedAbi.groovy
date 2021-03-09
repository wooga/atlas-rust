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

/** ABIs supported by Rust.
 *
 * @since 0.1
 */
@CompileStatic
enum SupportedAbi {

    GNU('gnu'),
    GNU_EABI('gnueabi'),
    GNU_EABIHF('gnueabihf'),
    GNU_ABI64('gnuabi64'),
    MSVC('msvc')

    final String name

    private SupportedAbi( final String name) {
        this.name = name
    }

    @Override
    String toString() {
        name
    }
}
