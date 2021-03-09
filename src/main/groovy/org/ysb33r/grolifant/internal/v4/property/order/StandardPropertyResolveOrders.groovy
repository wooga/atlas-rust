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
package org.ysb33r.grolifant.internal.v4.property.order

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.api.v4.PropertyResolveOrder

/** Standard property resolve orders
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.15.0
 */
@CompileStatic
class StandardPropertyResolveOrders {
    /** Resolves a property by looking a the project first, then the system, then
     * the environment. Environmental variables will be uppercased and dots replaced with
     * underscores.
     *
     */
    static class ProjectSystemEnvironment implements PropertyResolveOrder {
        @Override
        String resolve(Project project, String name) {
            project.properties[name] ?: System.getProperty(name, System.getenv(PropertyNaming.asEnvVar(name)))
        }
    }

    /** Resolves a property by looking a the project first, then the system, then
     * the environment. Environmental variables will be uppercased and dots replaced with
     * underscores.
     *
     */
    static class SystemEnvironmentProject implements PropertyResolveOrder {
        @Override
        String resolve(Project project, String name) {
            System.getProperty(name, System.getenv(PropertyNaming.asEnvVar(name))) ?: project.properties[name]
        }
    }
}
