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
package org.ysb33r.grolifant.internal.v4.downloader

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.wrapper.Download
import org.gradle.wrapper.IDownload
import org.ysb33r.grolifant.api.BaseProgressLogger

import java.lang.reflect.Constructor

/**
 * @since 0.8
 */
@CompileStatic
class Downloader {

    static final String INSTALLER_VERSION = '1.0'

    final IDownload downloader
    final BaseProgressLogger progressLogger

    @CompileDynamic
    @SuppressWarnings('CouldBeSwitchStatement')
    static Downloader create(final String distributionName, Project project) {
        boolean quiet = project.logging.level < LogLevel.INFO
        IDownload downloader
        BaseProgressLogger progressLogger

        Class<?> wrapperLoggerClass = null
        try {
            wrapperLoggerClass = Downloader.classLoader.loadClass('org.gradle.wrapper.Logger')
        } catch (ClassNotFoundException) {
        }

        Download.constructors.findResult { Constructor ctor ->
            if (ctor.parameterTypes == [wrapperLoggerClass, String, String] as Class[]) {
                // Gradle 2.3+
                Object wrapperLogger = wrapperLoggerClass.constructors.findResult { Constructor loggerCtor ->
                    loggerCtor.newInstance(quiet)
                }

                progressLogger = [log: { String msg -> wrapperLogger.log(msg) }] as BaseProgressLogger
                downloader = ctor.newInstance(wrapperLogger, distributionName, INSTALLER_VERSION)
            } else if (ctor.parameterTypes == [org.gradle.api.logging.Logger, String, String] as Class[]) {
                progressLogger = new Progress(quiet)
                downloader = ctor.newInstance(project.logger, distributionName, INSTALLER_VERSION)
            } else if (ctor.parameterTypes == [String, String] as Class[]) {
                progressLogger = new Progress(quiet)
                downloader = ctor.newInstance(distributionName, INSTALLER_VERSION)
            }
        }

        new Downloader(downloader, progressLogger)
    }

    private Downloader(IDownload d, BaseProgressLogger bpl) {
        this.downloader = d
        this.progressLogger = bpl
    }

    private static class Progress implements BaseProgressLogger {
        private final boolean quiet

        Progress(boolean quiet) {
            this.quiet = quiet
        }

        void log(String message) {
            if (!quiet) {
                println message
            }
        }
    }

}
