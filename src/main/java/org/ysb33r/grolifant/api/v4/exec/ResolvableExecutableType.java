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
package org.ysb33r.grolifant.api.v4.exec;

import org.gradle.api.provider.Provider;

/**
 * @author Schalk W. Cronjé
 *
 * @since 0.14
 */
public interface ResolvableExecutableType {
    /**
     * A textual name of the type of executable resolver.
     *
     * @return Codename for resolver
     */
    String getType();

    /**
     * Provided value for resolving an executable.
     * <p>
     * In case of a version this could be a versoin number, or for a hard-coded path, the path to the
     * executable.
     *
     * @return A provider to the value that will be used to eventually resolve the executable.
     */
    Provider<String> getValue();
}
