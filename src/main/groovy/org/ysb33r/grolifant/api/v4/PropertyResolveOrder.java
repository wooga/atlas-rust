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
package org.ysb33r.grolifant.api.v4;

import org.gradle.api.Project;

/** Resolves a property within a certain order
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.15.0
 */
public interface PropertyResolveOrder {

    /** Resolves the property within the context of a Gradle project
     *
     * @param project Contextual project
     * @param name Project name
     * @return Resolved property value or {@code null}.
     */
    String resolve(Project project, String name);
}
