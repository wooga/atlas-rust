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

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.internal.v4.property.order.StandardPropertyResolveOrders

/** Resolves properties in a certain i.e. like SprintBoot, but
 * less functionality to suite Gradle context.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.15.0
 */
@CompileStatic
class PropertyResolver {

    public static final PropertyResolveOrder PROJECT_SYSTEM_ENV =
        new StandardPropertyResolveOrders.ProjectSystemEnvironment()
    public static final PropertyResolveOrder SYSTEM_ENV_PROPERTY =
        new StandardPropertyResolveOrders.SystemEnvironmentProject()

    /** Creates a property resolver that will use {@link #PROJECT_SYSTEM_ENV} by default.
     *
     * @param project Project context in which to resolve properties.
     */
    PropertyResolver(Project project) {
        this.project = project
        this.order = PROJECT_SYSTEM_ENV
    }

    /** Creates a property resolver with a custom resolve order
     *
     * @param project  Project context in which to resolve properties.
     * @param order Custom property resolve order
     */
    PropertyResolver(Project project, PropertyResolveOrder order) {
        this.project = project
        this.order = order
    }

    /** Change the existing property order
     *
     * @param newOrder New property resolve order.
     */
    void order(PropertyResolveOrder newOrder) {
        this.order = newOrder
    }

    /** Gets a property.
     *
     * @param name Name of property to resolve
     * @return Resolved property or {@code null} if no property was found.
     */
    String get(final String name) {
        get(name, null, this.order)
    }

    /** Gets a property.
     *
     * @param name Name of property to resolve.
     * @param defaultValue Value to return if property cannot be resolved.
     * @return Resolved property or {@code defaultValue} if no property was found.
     */
    String get(final String name, final String defaultValue) {
        get(name, defaultValue, this.order)
    }

    /** Gets a property using a specific resolve order.
     *
     * @param name Name of property to resolve
     * @param order Resolve order
     * @return Resolved property or {@code null} if no property was found.
     */
    String get(final String name, PropertyResolveOrder order) {
        get(name, null, order)
    }

    /** Gets a property using a specific resolve order
     *
     * @param name Name of property to resolve.
     * @param defaultValue Value to resturn if property cannot be resolved.
     * @param order Resolve order.
     * @return Resolved property or {@code defaultValue} if no property was found.
     */
    String get(final String name, final String defaultValue, PropertyResolveOrder order) {
        order.resolve(project, name) ?: defaultValue
    }

    private final Project project
    private PropertyResolveOrder order
}
