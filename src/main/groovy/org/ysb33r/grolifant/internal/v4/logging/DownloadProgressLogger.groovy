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
package org.ysb33r.grolifant.internal.v4.logging

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.ysb33r.grolifant.api.ProgressLogger

/**
 * <p> This was built from an idea conceived by Michel Kremer -
 * {@link https://github.com/michel-kraemer/gradle-download-task/blob/master/src/main/java/de/undercouch/gradle/tasks/download/internal/ProgressLoggerWrapper.java}.
 *
 * @since 0.4
 */
@SuppressWarnings('LineLength')
@CompileStatic
class DownloadProgressLogger implements ProgressLogger {

    /** Creates a basic progress logger that can bew used for downloading etc.
     *
     * @param project Project that this logger will be attached to.
     * @param text Set description and logging header.
     */
    DownloadProgressLogger(final Project project, final String text) {
        this.logger = findFactoryFor(project, findLoggerFactory()).invokeMethod('newOperation', this.class)
        if (this.logger) {
            configureLogger(text)
        } else {
            project.logger.debug(
                "Could not create a progress logger for ${text} - no progress feedback will be provided"
            )
        }
    }

    @Override
    void log(final String text) {
        this.logger?.invokeMethod('progress', text)
    }

    /** Allow logging to start
     *
     * <p> Any attempt to log before this will result in an exception.
     */
    @Override
    void started() {
        startLogging()
    }

    /** Prevent further logging.
     *
     * <p> Any attempt to log after this will result in an exception.
     */
    @Override
    void completed() {
        stopLogging()
    }

    @CompileDynamic()
    private void startLogging() {
        this.logger?.started()
    }

    @CompileDynamic()
    private void stopLogging() {
        this.logger?.completed()
    }

    @CompileDynamic
    private void configureLogger(final String text) {
        this.logger.invokeMethod('setDescription', text)

        if (this.logger.respondsTo(LOGGING_HEADER, String)) {
            this.logger.invokeMethod(LOGGING_HEADER, text)
        }
    }

    private static Object findFactoryFor(Project project, Class clazz) {
        if (clazz != null) {
            ((ProjectInternal) project).services.get(clazz)
        } else {
            null
        }
    }

    private static Class findLoggerFactory() {
        try {
            DownloadProgressLogger.classLoader.loadClass('org.gradle.internal.logging.progress.ProgressLoggerFactory')
        } catch (ClassNotFoundException e1) {
            try {
                DownloadProgressLogger.classLoader.loadClass('org.gradle.logging.ProgressLoggerFactory')
            } catch (ClassNotFoundException e2) {
                null
            }
        }
    }

    private final Object logger

    private static final String LOGGING_HEADER = 'setLoggingHeader'
}
