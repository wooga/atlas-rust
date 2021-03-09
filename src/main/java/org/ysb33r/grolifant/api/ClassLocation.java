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
package org.ysb33r.grolifant.api;

import java.io.File;
import java.net.URL;

/** Describes the location of a class.
 *
 * @since 0.9
 */
public interface ClassLocation {

    /** If the class is located on the filesystem or in a JAR this will be the location.
     *
     * @return Location of class or {@code null} if class is in a runtime module.
     *
     */
    File getFile();

    /** If the class is located in a runtime module
     *
     * This method always returns {@code null} on JDK7 or JDK8.
     *
     * @return URI of runtime or {@code null} is class is not in a runtime module.
     */
    URL getRuntime();
}
