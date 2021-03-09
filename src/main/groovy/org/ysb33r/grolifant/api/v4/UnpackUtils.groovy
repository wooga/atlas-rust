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
import org.gradle.api.Project
import org.ysb33r.grolifant.api.OperatingSystem

/** Utilities to deal with unpacking certain formats.
 *
 * @since 0.6
 */
@CompileStatic
class UnpackUtils {

    /** Unpack a DMG image on MacOs.
     *
     * <p> NOOP on other operating systems.
     *
     * @param project Project instance that unpacking is related to.
     * @param tempPrefix A preifx for the temporrary directory that will be used.
     * @param srcArchive DMG archive to unpack
     * @param relPath Relative path within the DMG to unpack.
     * @param destDir Directory to unpack into.
     *
     * @since 0.6
     */
    static void unpackDmgOnMacOsX(
        final Project project,
        final String tempPrefix,
        final File srcArchive,
        final String relPath,
        final File destDir
    ) {
        if (OperatingSystem.current().macOsX) {
            final File mountRoot = File.createTempDir(tempPrefix, '$$$')
            final File mountedPath = new File(mountRoot, srcArchive.name)
            mountedPath.mkdirs()
            mountDMG(project, srcArchive, mountedPath)
            try {
                copyDMGFiles(project, mountedPath, destDir, relPath)
            } finally {
                unmountDMG(project, mountedPath)
                mountedPath.deleteDir()
            }
        }
    }

    @CompileDynamic
    private static void mountDMG(final Project project, final File srcArchive, final File mountedPath) {
        project.exec {
            executable HDIUTIL
            args 'attach', srcArchive.absolutePath, '-nobrowse', '-readonly'
            args '-mountpoint', mountedPath.absolutePath
        }
    }

    @CompileDynamic
    private static void copyDMGFiles(
        Project project,
        final File mountedPath,
        final File destDir,
        final String relPath
    ) {
        project.copy {
            from "${mountedPath}/${relPath}", {
                include '**'
            }
            into "${destDir}/${relPath}"
        }
    }

    @CompileDynamic
    private static void unmountDMG(final Project project, final File mountedPath) {
        project.exec {
            executable HDIUTIL
            args 'detach', mountedPath.absolutePath
            ignoreExitValue = true
        }
    }

    private final static String HDIUTIL = 'hdiutil'
}
