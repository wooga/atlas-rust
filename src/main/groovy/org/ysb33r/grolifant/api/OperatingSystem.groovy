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
    This code was lifted from the Gradle org.gradle.internal.os.OperatingSystem class
    which is under the Apache v2.0 license. Original copyright from 2010 remains. Modifications
    from 2017+ are under the copyright and licensed mentioned above
*/
package org.ysb33r.grolifant.api

import groovy.transform.CompileStatic
import org.ysb33r.grolifant.api.os.FreeBSD
import org.ysb33r.grolifant.api.os.GenericUnix
import org.ysb33r.grolifant.api.os.Linux
import org.ysb33r.grolifant.api.os.MacOsX
import org.ysb33r.grolifant.api.os.NetBSD
import org.ysb33r.grolifant.api.os.Solaris
import org.ysb33r.grolifant.api.os.Windows
import org.ysb33r.grolifant.internal.v4.os.OperatingSystemConstants

import java.util.regex.Pattern

/**
 *
 */
@CompileStatic
abstract class OperatingSystem {

    /** Enumeration representing common hardware-operating system architectures.
     *
     */
    enum Arch {

        X86_64(OperatingSystemConstants.AMD64),
        X86(OperatingSystemConstants.I386),
        POWERPC('ppc'),
        SPARC('sparc'),
        UNKNOWN('(unknown)')

        private Arch(final String id) {
            this.id = id
        }

        private final String id
    }

    /** The short name for the current operating system.
     * Possibly the same as {@code System.getProperty( "os.name" )}.
     */
    final String name = OS_NAME

    /** The version for the current operating system.
     */
    final String version = OS_VERSION

    /** Name of environmental variable that holds the system search path
     */
    final String pathVar = 'PATH'

    /** Check is this is Microsoft Windows
     *
     * @return {@code true} if Windows
     */
    boolean isWindows() { false }

    /** Check is this is Apple Mac OS X
     *
     * @return {@code true} if Mac OS X
     */
    boolean isMacOsX() { false }

    /** Check is this is a Linux flavour
     *
     * @return {@code true} if any kind of Linux
     */
    boolean isLinux() { false }

    /** Check is this is FreeBSD
     *
     * @return {@code true} if FreeBSD
     */
    boolean isFreeBSD() { false }

    /** Check is this is NetBSD
     *
     * @return {@code true} if NetBSD
     */
    boolean isNetBSD() { false }

    /** Check is this is a Solaris flavour
     *
     * @return {@code true} if Solaris
     */
    boolean isSolaris() { false }

    /** Check is this is any kind of Unix-like O/S
     *
     * @return {@code true} if any kind of Unix-like O/S
     */
    boolean isUnix() { false }

    /** The character used to separate elements in a system search path
     *
     * @return OS-specific separator.
     */
    String getPathSeparator() {
        File.pathSeparator
    }

    /** Stringize implementation
     *
     * @return Name, Version and Architecture
     */
    String toString() {
        "${name ?: ''} ${version ?: ''} ${arch ?: ''}"
    }

    /** Locates the given exe in the system path.
     * @param name Name of exe to search for.
     * @return Executable location of {@code null} if not found.
     */
    File findInPath(String name) {
        for (String exeName : getExecutableNames(name)) {
            if (exeName.contains(pathSeparator)) {
                File candidate = new File(exeName)
                if (candidate.file) {
                    return candidate
                }
            } else {
                for (File dir : (path)) {
                    File candidate = new File(dir, exeName)
                    if (candidate.file) {
                        return candidate
                    }
                }
            }
        }
        null
    }

    /** List of system search paths
     *
     * @return List of entries (can be empty).
     */
    List<File> getPath() {
        List<File> entries = []
        String path = System.getenv(pathVar)
        if (path != null) {
            for (String entry : path.split(Pattern.quote(pathSeparator))) {
                entries.add(new File(entry))
            }
        }
        entries
    }

    /** Find all files in system search path of a certain name.
     *
     * @param name Name to look for
     * @return List of files
     */
    List<File> findAllInPath(String name) {
        List<File> all = []

        for (File dir : (path)) {
            File candidate = new File(dir, name)
            if (candidate.file) {
                all.add(candidate)
            }
        }

        all
    }

    /** Returns OS-specific decorated exe name.
     *
     * @param executablePath Name of exe
     * @return Returns an appropriately decorated exe
     * @deprecated Use{ @link #getExecutableNames(String) } instead
     */
    @Deprecated
    abstract String getExecutableName(String executablePath)

    /** Returns list of possible OS-specific decorated exe names.
     *
     * @param executablePath Name of exe
     * @return Returns a list of possible appropriately decorated exes
     */
    abstract List<String> getExecutableNames(String executablePath)

    /** Architecture underlying the operating system
     *
     * @return Architecture type. Returns {@code OperatingSystem.Arch.UNKNOWN} is it cannot be identified. In that a
     *   caller might need to use {@link #getArchStr()} to help with identification.
     */
    abstract OperatingSystem.Arch getArch()

    /** Architecture underlying the operating system
     *
     * @return Architecture string
     */
    abstract String getArchStr()

    /** OS-dependent string that is used to suffix to shared libraries
     *
     * @return Shared library suffix
     */
    abstract String getSharedLibrarySuffix()

    /** OS-dependent string that is used to suffix to static libraries
     *
     * @return Static library suffix
     */
    abstract String getStaticLibrarySuffix()

    /** Returns OS-specific decorated script name.
     *
     * @param scriptPath Name of script
     * @return Returns an appropriately decorated script name
     *
     * @deprecated Since under some OS (especially, Windows), from practical point of view, there is no distinction
     * between executables and scripts
     */
    @Deprecated
    abstract String getScriptName(String scriptPath)

    /** Returns OS-specific shared library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Shared library name.
     */
    abstract String getSharedLibraryName(String libraryName)

    /** Returns OS-specific static library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Static library name.
     */
    abstract String getStaticLibraryName(String libraryName)

    protected OperatingSystem() {
    }

    /** Returns a representation of the operating system that the JVM currently runs on.
     *
     * @return An object implementing an extension of {@link #OperatingSystem}.
     */
    // tag::check_os[]
    static OperatingSystem current() {
        if (OS_NAME.contains('windows')) {
            return Windows.INSTANCE
        } else if (OS_NAME.contains('mac os x') || OS_NAME.contains('darwin') || OS_NAME.contains('osx')) {
            return MacOsX.INSTANCE
        } else if (OS_NAME.contains('linux')) {
            return Linux.INSTANCE
        } else if (OS_NAME.contains('freebsd')) {
            return FreeBSD.INSTANCE
        } else if (OS_NAME.contains('sunos') || OS_NAME.contains('solaris')) {
            return Solaris.INSTANCE
        } else if (OS_NAME.contains('netbsd')) {
            return NetBSD.INSTANCE
        }

        // Not strictly true, but a good guess
        GenericUnix.INSTANCE
    }
    // end::check_os[]

    protected static final String OS_NAME = System.getProperty('os.name').toLowerCase()
    protected static final String OS_ARCH = System.getProperty('os.arch').toLowerCase()
    protected static final String OS_VERSION = System.getProperty('os.version').toLowerCase()

}
