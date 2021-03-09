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

import java.util.Map;

/**
 * Creates a {@link ResolvableExecutable}.
 *
 * @since 0.3
 */
public interface ResolvedExecutableFactory {
    /**
     * Creates {@link ResolvableExecutable} from a specific input.
     * @param options A map with options
     * @param from An object that can be used to resolve an exe. It is up to the implementation to decide whether the
     *        object is of an appropriate type.
     * @return The resolved exe.
     */

    ResolvableExecutable build(Map<String, Object> options, Object from);
}
