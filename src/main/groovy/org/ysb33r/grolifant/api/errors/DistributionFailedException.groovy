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
package org.ysb33r.grolifant.api.errors

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.gradle.api.GradleException

/** Thrown when a distribution failed to unpack correctly or does not meet specific criteria.
 *
 */
@CompileStatic
@InheritConstructors
class DistributionFailedException extends GradleException implements GrolifantError {
}
