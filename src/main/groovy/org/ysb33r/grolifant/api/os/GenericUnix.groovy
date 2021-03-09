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
/*
    This code is based upon code from the Gradle org.gradle.internal.os.OperatingSystem class
    which is under the Apache v2.0 license. Original copyright from 2010 remains. Modifications
    from 2017+ are under the copyright and licensed mentioned above
*/
package org.ysb33r.grolifant.api.os

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.OperatingSystem

import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.AMD64
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.I386
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.X86
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.X86_64

/** Generic Unix-like implementation of {@code OperatingSystem}.
 *
 * Also serves as a base class for specific Unix-like implementations.
 */
@CompileStatic
class GenericUnix extends OperatingSystem {
    static final OperatingSystem INSTANCE = new GenericUnix()

    /** Return Unix-like string that is used to suffix to shared libraries
     */
    @SuppressWarnings('GetterMethodCouldBeProperty')
    @Override
    String getSharedLibrarySuffix() {
        '.so'
    }

    /** Default Unix-like string that is used to suffix to static libraries
     */
    final String staticLibrarySuffix = '.a'

    /** Implementation-specific free-form architecture string.
     *
     * Effectively reports the same as {@code System.getProperty( "os.arch" )}
     */
    @SuppressWarnings('GetterMethodCouldBeProperty')
    @Override
    String getArchStr() {
        OS_ARCH
    }

    /** Confirms that this is a Unix-like operating system.
     *
     * @return {@code true}
     */
    @Override
    boolean isUnix() { true }

    /** Given a base string, returns the Unix exe name.
     *
     * @param executablePath A base path name
     * @return Returns the provided base path name
     */
    @Override
    String getExecutableName(final String executablePath) {
        executablePath
    }

    /** Given a base string, returns the Unix exe name.
     *
     * @param executablePath A base path name
     * @return Returns the provided base path name
     */
    @Override
    List<String> getExecutableNames(final String executablePath) {
        [executablePath]
    }

    /** Returns OS-specific decorated script name.
     *
     * @param scriptPath Name of script
     * @return Returns an appropriately decorated script name
     */
    @Override
    String getScriptName(String scriptPath) {
        scriptPath
    }

    /** Returns OS-specific shared library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Shared library name.
     */
    @Override
    String getSharedLibraryName(String libraryName) {
        getLibraryName(libraryName, sharedLibrarySuffix)
    }

    /** Architecture underlying the operating system
     *
     * @return Architecture type. Returns {@code OperatingSystem.Arch.UNKNOWN} is it cannot be identified. In that a
     *   caller might need to use {@link #getArchStr()} to help with identification.
     */
    @Override
    Arch getArch() {
        switch (archStr) {
            case AMD64:
            case X86_64:
                return Arch.X86_64
            case I386:
            case X86:
                return Arch.X86
            case 'ppc':
            case 'powerpc':
                return Arch.POWERPC
            case 'sparc':
                return Arch.SPARC
            default:
                return Arch.UNKNOWN
        }
    }

    /** Returns OS-specific static library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Static library name.
     */
    @Override
    String getStaticLibraryName(String libraryName) {
        getLibraryName(libraryName, staticLibrarySuffix)
    }

    protected GenericUnix() {
        super()
    }

    @SuppressWarnings('UnnecessarySubstring')
    private String getLibraryName(String libraryName, String suffix) {
        if (libraryName.endsWith(suffix)) {
            return libraryName
        }

        int pos = libraryName.lastIndexOf('/')
        if (pos >= 0) {
            "${libraryName.substring(0, pos + 1)}lib${libraryName.substring(pos + 1)}${suffix}"
        } else {
            "lib${libraryName}${suffix}"
        }
    }

}
