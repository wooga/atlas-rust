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

import org.gradle.process.ProcessForkOptions;

import java.util.List;
import java.util.Map;

/** Methods for setting executables and arguments.
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.17.0
 */
public interface MutableToolExecSpec extends MutableBaseExecSpec {
    /** Lazy-evaluated version of the exe
     *
     * @return Lazy-evaluated version of the exe or {@code null} if not configured.
     */
    ResolvableExecutable getResolvableExecutable();

    /** Set the exe to use.
     *
     * <p> This variant of the method has been introduced to cope with the API change in Gradle 4.0.
     *
     * @param exe Executable as String representation
     */
    void setExecutable(String exe);

    /** Use a key-value approach to setting the exe.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys.
     * Implementations should use {@link ResolverFactoryRegistry} to add more keys.
     *
     * @param exe Key-value setting exe (with optional extra keys)
     */
    void setExecutable(Map<String, Object> exe);

    /** Set the exe to use.
     *
     * @param resolver An implementation of {@link ResolvableExecutable}
     */
    void setExecutable(ResolvableExecutable resolver);

    /** Use a key-value approach to setting the exe.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys.
     * Implementations should use {@link ResolverFactoryRegistry} to add more keys.
     *
     * @param exe Key-value setting exe (with optional extra keys)
     */
    void executable(Map<String, Object> exe);

    /** Set the exe to use.
     *
     * @param resolver An implementation of {@link ResolvableExecutable}
     * @return This object as an instance of {@link ProcessForkOptions}
     */
    ProcessForkOptions executable(ResolvableExecutable resolver);

    /** Replace the tool-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    void setExeArgs(Iterable<?> args);

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Iterable<?> args);

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Object... args);

    /** Any arguments specific to the tool in use
     *
     * @return Arguments to the tool. Can be empty, but never null.
     */
    List<String> getExeArgs();
}
