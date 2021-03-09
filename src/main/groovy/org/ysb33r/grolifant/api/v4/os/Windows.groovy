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
package org.ysb33r.grolifant.api.v4.os

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.OperatingSystem

import java.util.regex.Pattern

import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.AMD64
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.DOT_BAT
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.DOT_EXE
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.I386
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.UNKNOWN
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.X86
import static org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants.X86_64

/** Windows implementation of {@code OperatingSystem}.
 *
 */
@CompileStatic
class Windows extends OperatingSystem {
    public static final OperatingSystem INSTANCE = new Windows()

    /** Name of environmental variable that holds a list of the file extensions considered to be executable
     */
    final String pathextVar = 'PATHEXT'

    /** Returns Windows system search path environmental variable name.
     */
    final String pathVar = 'Path'

    /** Return Windows string that is used to suffix to shared libraries
     */
    final String sharedLibrarySuffix = '.dll'

    /** Windows string that is used to suffix to static libraries
     */
    final String staticLibrarySuffix = '.lib'

    /** Confirms this is a representation of the Microsoft Windows operating system.
     *
     * @return {@code true}
     */
    @Override
    boolean isWindows() { true }

    @Override
    String getExecutableName(String executablePath) {
        withSuffix(executablePath, DOT_EXE)
    }

    @Override
    List<String> getExecutableNames(String executablePath) {
        List<String> executableNames = []
        if (extensionPos(executablePath) >= 0) {
            executableNames.add executablePath
        } else {
            String pathext = System.getenv(pathextVar)
            if (pathext != null && pathext.length() > 0) {
                for (String entry : pathext.split(Pattern.quote(pathSeparator))) {
                    executableNames.add withSuffix(executablePath, entry)
                }
            } else {
                // Fall back to legacy behavior
                executableNames.add withSuffix(executablePath, DOT_EXE)
            }
        }
        executableNames
    }

    /** Returns Windows-specific decorated script name.
     *
     * @param scriptPath Name of script.
     * @return Returns a {@code .bat} based name.
     */
    @Override
    String getScriptName(String scriptPath) {
        withSuffix(scriptPath, DOT_BAT)
    }

    /** Returns Windows shared library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Shared library name with {@code .dll} extension
     */
    @Override
    String getSharedLibraryName(String libraryName) {
        withSuffix(libraryName, sharedLibrarySuffix)
    }

    /** Returns OS-specific static library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Static library name.
     */
    @Override
    String getStaticLibraryName(String libraryName) {
        withSuffix(libraryName, staticLibrarySuffix)
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
                return Arch.X86_64
            case I386:
                return Arch.X86
            default:
                return Arch.UNKNOWN
        }
    }

    /** Architecture underlying the operating system
     *
     * @return {@code amd64} or {@code i386}
     */
    @Override
    String getArchStr() {
        switch (OS_ARCH) {
            case X86_64:
            case AMD64:
                return AMD64
            case X86:
                return I386
            default:
                return UNKNOWN
        }
    }

    protected Windows() {
        super()
    }

    private String withSuffix(final String executablePath, final String extension) {
        executablePath.toLowerCase().endsWith(extension) ?
            executablePath :
            "${removeExtension(executablePath)}${extension}"
    }

    private int extensionPos(final String executablePath) {
        int fileNameStart = Math.max(executablePath.lastIndexOf('/'), executablePath.lastIndexOf('\\'))
        int extensionPos = executablePath.lastIndexOf('.')

        (extensionPos > fileNameStart) ? extensionPos : -1
    }

    private String removeExtension(final String executablePath) {
        int extensionPos = this.extensionPos(executablePath)

        (extensionPos >= 0) ? executablePath[0..extensionPos] : executablePath
    }

}
