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
 * A way to create a lazy-evaluated location of an external executable.
 *
 * @since 0.4
 */
public interface ExternalExecutable {
    /**
     * Lazy-evaluated version of the exe
     *
     * @param exe A map that contains items like {@code version : '1.2.3'} or {@code search : 'doxygen'}
     *
     * @return Lazy-evaluated version of the exe or {@code null} if not configured.
     */
    ResolvableExecutable getResolvableExecutable(final Map<String, Object> exe);
}
