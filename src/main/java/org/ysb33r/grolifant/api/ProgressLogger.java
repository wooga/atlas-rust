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
package org.ysb33r.grolifant.api;

/**
 * Allows implementation against
 *
 * @since 0.4
 */
public interface ProgressLogger extends BaseProgressLogger {
    /**
     * Allow logging to start
     *
     * <p> Any attempt to log before this will result in an exception.
     */
    void started();

    /**
     * Prevent further logging.
     *
     * <p> Any attempt to log after this will result in an exception.
     */
    void completed();
}
