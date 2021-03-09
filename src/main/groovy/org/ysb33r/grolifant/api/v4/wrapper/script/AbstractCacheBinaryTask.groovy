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
package org.ysb33r.grolifant.api.v4.wrapper.script

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.ysb33r.grolifant.api.OperatingSystem

import java.time.ZonedDateTime
import java.util.concurrent.Callable

import static org.ysb33r.grolifant.api.v4.FileUtils.projectCacheDirFor

/** Base class for tasks that cache arbitrary binaries.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.17.0
 */
@CompileStatic
abstract class AbstractCacheBinaryTask extends DefaultTask {

    @OutputFile
    Provider<File> getLocationPropertiesFile() {
        this.locationPropertiesFile
    }

    void setLocationPropertiesFile(Object o) {
        switch (o) {
            case Provider:
                this.locationPropertiesFile = (Provider<File>) o
                break
            default:
                this.locationPropertiesFile = project.providers.provider({
                    project.file(o)
                } as Callable<File>)
        }
    }

    @Input
    Map<String, String> getCachedBinaryProperties() {
        Map<String, String> props = [
            APP_VERSION : binaryVersion,
            APP_LOCATION: binaryLocation
        ]
        props.putAll(additionalProperties)
        props
    }

    @TaskAction
    void exec() {
        File propsFile = locationPropertiesFile.get()
        OperatingSystem.current().windows ?
            writeWindowsPropertiesBatFile(propsFile, cachedBinaryProperties) :
            writePropertiesShellFile(propsFile, cachedBinaryProperties)
    }

    /**
     *
     * @param locationPropertiesDefaultName The default name for the location properties file.
     *   Can include a relative path
     */
    protected AbstractCacheBinaryTask(String locationPropertiesDefaultName) {
        this.locationPropertiesFile = project.providers.provider({
            new File(
                projectCacheDirFor(project),
                locationPropertiesDefaultName
            )
        } as Callable<File>)
    }

    /** Returns additional properties to be added to the cached binary properties file.
     *
     * The default implementation returns an empty map.
     *
     * @return Additional properties. Can be empty, but nevber {@code null}.
     */
    @Input
    protected Map<String, String> getAdditionalProperties() {
        [:]
    }

    /** Writes a Windows batch file of property values that can be included by a wrapper script.
     *
     * Property values that contain spaces will be wrapped in double quotes.
     *
     * @param destFile Destination file (assumes parent path exists)
     * @param allprops Properties to write to batch file.
     */
    @SuppressWarnings('DuplicateStringLiteral')
    protected void writeWindowsPropertiesBatFile(File destFile, final Map<String, String> allprops) {
        destFile.withWriter { writer ->
            writer.println "@rem ${propertiesDescription}"
            writer.println "@rem Generated ${ZonedDateTime.now()}"
            allprops.each { k, v ->
                if (v.contains(' ')) {
                    writer.println "set ${k}=\"${v}\""
                } else {
                    writer.println "set ${k}=${v}"
                }
            }
        }
    }

    /** Write a shell file of properties.
     *
     * The default implementation simply write a Java properties file as it is compatible enough with
     * POSIX shell files.
     *
     * @param destFile Destination file (assumes parent path exists)
     * @param allprops Properties to write to dot-include file.
     */
    protected void writePropertiesShellFile(File destFile, final Map<String, String> allprops) {
        Properties props = new Properties()
        props.putAll(allprops)
        destFile.withWriter { Writer w ->
            props.store(w, propertiesDescription)
        }
    }

    /** Obtains location of executable binary or script
     *
     * @return Location of executable as a string
     */
    @Input
    abstract protected String getBinaryLocation()

    /** Obtains version of binary or script
     *
     * @return Version as a string. Can be {@code null}.
     */
    @Input
    abstract protected String getBinaryVersion()

    /** Obtains a description to be added to the cached binary properties file.
     *
     * @return Description. Never {@code null}.
     */
    @Input
    abstract protected String getPropertiesDescription()

    private Provider<File> locationPropertiesFile
}
