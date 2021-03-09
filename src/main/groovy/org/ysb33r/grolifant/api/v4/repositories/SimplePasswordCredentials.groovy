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
package org.ysb33r.grolifant.api.v4.repositories

import groovy.transform.CompileStatic
import org.gradle.api.artifacts.repositories.PasswordCredentials

/** Simple memory-based implementation of password credentials.
 *
 * Not the most secure, but I don;t think the interface lends itself
 * to be secure.
 *
 * @since 0.10
 */
@CompileStatic
class SimplePasswordCredentials implements PasswordCredentials {
    String username
    String password
}
