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
import org.ysb33r.grolifant.api.errors.ConfigurationException

import static groovy.lang.Closure.DELEGATE_FIRST

/** Methods for dealing with closures
 *
 * @since 0.3
 */
@CompileStatic
class ClosureUtils {

    /** Configure this item using a closure
     *
     * @param item Item to configure
     * @param cfg Configuration closure to use.
     */
    static void configureItem(Object item, Closure cfg) {
        Closure runner = (Closure) (cfg.clone())
        runner.delegate = item
        runner.resolveStrategy = DELEGATE_FIRST
        if (runner.maximumNumberOfParameters == 0) {
            runner()
        } else if (runner.maximumNumberOfParameters > 1) {
            throw new ConfigurationException(
                'Cannot use this closure for configuration as it has more than one input parameter'
            )
        } else {
            runner(item)
        }
    }
}
