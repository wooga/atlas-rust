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

/** Various utilities dealing with key-value pairs.
 *
 * @since 0.3
 */
@CompileStatic
class MapUtils {
    /** Evaluates a map of objects to a map of strings.
     *
     * Anything value that can be evaluated by {@link StringUtils#stringize(Object)} is
     * evaluated
     *
     * @param props Map that will be evaluated
     * @return Converted{@code Map<String,String>}
     */
    static Map<String, String> stringizeValues(Map<String, Object> props) {
        props.collectEntries { String key, Object value ->
            [(key): StringUtils.stringize(value)]
        } as Map<String, String>
    }
}
