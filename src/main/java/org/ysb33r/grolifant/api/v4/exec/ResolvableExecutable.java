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

import java.io.File;

/**
 * Holds a reference to an exe that will only be made available when explicitly called.
 * <p>
 * This makes it possibly to resolve executables which only become available after packages have been
 * downloaded.
 *
 * @since 0.4
 */
public interface ResolvableExecutable {
    /**
     * Location of a tool exe.
     *
     * @return Full path to the tool exe
     */
    File getExecutable();
}
