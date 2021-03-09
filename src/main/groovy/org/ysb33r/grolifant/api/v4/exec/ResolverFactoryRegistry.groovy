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
package org.ysb33r.grolifant.api.v4.exec

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.ysb33r.grolifant.api.v4.StringUtils
import org.ysb33r.grolifant.api.errors.ExecConfigurationException
import org.ysb33r.grolifant.internal.v4.execspec.ResolveExecutableFromPath
import org.ysb33r.grolifant.internal.v4.execspec.ResolveExecutableInSearchPath

import java.util.concurrent.Callable

/** A registry of factories for locating executables.
 *
 * @since 0.17.0
 */
@CompileStatic
class ResolverFactoryRegistry implements ExternalExecutable, ExternalExecutableType {

    /** Create a registry of factories for locating executables
     *
     * @param project Associated project for resolving path relative to the project directory.
     */
    @SuppressWarnings('UnnecessaryCast')
    ResolverFactoryRegistry(Project project) {
        executableKeyActions = [
            'path'  : new ResolveExecutableFromPath(project),
            'search': ResolveExecutableInSearchPath.INSTANCE
        ] as Map<String, ResolvedExecutableFactory>
        this.project = project
    }

    /** Register more ways of locating executables.
     *
     * @param key The key that is used to indicate the resolver method
     * @param factory A factory that will be called be the value associated with the key.
     */
    void registerExecutableKeyActions(final String key, final ResolvedExecutableFactory factory) {
        executableKeyActions.put(key, factory)
    }

    /** Register more ways of locating executables.
     *
     * @param factory A factory that will be called be the value associated with the key.
     */
    void registerExecutableKeyActions(final org.ysb33r.grolifant.api.v4.exec.NamedResolvedExecutableFactory factory) {
        executableKeyActions.put(factory.name, factory)
    }

    /** Use a key-value approach to finding the exe.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys.
     * Implementations should use {@link #registerExecutableKeyActions} to add more keys.
     *
     * @param exe Key-value setting exe (with optional extra keys)
     * @return A resolved exe
     * @throw ExecConfigurationException if no keys are valid, or more than one key is valid.
     */
    ResolvableExecutable getResolvableExecutable(Map<String, Object> exe) {
        String exeKey = findValidKey(exe)
        Map<String, Object> options = [:]
        options.putAll(exe)
        options.remove(exeKey)
        executableKeyActions[exeKey].build(options, exe[exeKey])
    }

    ResolvableExecutableType getResolvableExecutableType(Map<String, Object> exe) {
        String exeKey = findValidKey(exe)
        new ResolvableExecutableType() {
            @Override
            String getType() {
                exeKey
            }

            @Override
            Provider<String> getValue() {
                project.providers.provider({
                    StringUtils.stringize(exe[exeKey])
                } as Callable<String>)
            }
        }
    }

    /** Look for exactly one valid key in the supplied map.
     *
     * @param exe List of keys to search.
     * @return The valid key
     * @throw ExecConfigurationException if no keys are valid, or more than one key is valid.
     */
    String findValidKey(Map<String, Object> exe) {
        Set<String> validKeys = executableKeyActions.keySet()
        Set<String> candidateKeys = exe.keySet()

        Set<String> found = candidateKeys.findAll { String candidate ->
            validKeys.find { String validKey ->
                candidate == validKey
            }
        } as Set<String>
        if (found.empty) {
            throw new ExecConfigurationException("No valid keys found in ${candidateKeys}")
        }
        if (found.size() > 1) {
            throw new ExecConfigurationException("More than one key found: ${found}")
        }
        found[0]
    }

    private final Map<String, org.ysb33r.grolifant.api.v4.exec.ResolvedExecutableFactory> executableKeyActions
    private final Project project
}
