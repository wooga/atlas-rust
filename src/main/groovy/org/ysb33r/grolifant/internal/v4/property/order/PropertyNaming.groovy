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

/** Utilities for property name conversions.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.15.0
 */
@CompileStatic
class PropertyNaming {

    /** Converts a property name to an equivalent environmental name
     *
     * Will uppercase all characters according to US locale and replace all dots with underscores.
     * @param name Property name
     * @return Equivalent environmental name.
     */
    static String asEnvVar(String name) {
        name.toUpperCase(Locale.US).replaceAll(~/[-\.]/, '_')
    }
}
