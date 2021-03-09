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
package org.ysb33r.grolifant.api.v4.wrapper.script

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.ysb33r.grolifant.api.errors.WrapperCreationException
import org.ysb33r.grolifant.internal.v4.wrappers.ToolWrapper

import java.util.concurrent.Callable
import java.util.function.Function

/** An abstract base class for creating tasks that create script wrappers.
 *
 * @since 0.17.0
 */
@CompileStatic
class AbstractScriptWrapperTask extends DefaultTask {
    @TaskAction
    void exec() {
        validate()
        Function<Collection<File>,Action<CopySpec>> copyAndSubstitute = copyAndSubstituteAction
        Set<File> templates = this.prepareTemplates.call()
        project.copy(copyAndSubstitute.apply(templates))
        if (deleteTemplatesAfterUsage) {
            templates*.delete()
        }
    }

    protected AbstractScriptWrapperTask() {
        super()
    }

    /** Directory for writing wrapper files to.
     *
     * @return Directory
     */
    @Internal
    protected File getWrapperDestinationDir() {
        project.projectDir
    }

    /** Use wrapper templates from resources classpath
     *
     * @param templateResourcePath
     * @param templateMapping
     */
    protected void useWrapperTemplatesInResources(
        final String templateResourcePath,
        final Map<String, String> templateMapping
    ) {
        deleteTemplatesAfterUsage = true
        prepareTemplates = {
            ToolWrapper.copyWrappersFromResources(templateResourcePath, templateMapping)
        } as Callable<Set<File>>
    }

    /** If the default of {@link org.apache.tools.ant.filters.ReplaceTokens} is used, this method
     * will return the start token delimiter.
     *
     * The default is {@code @}. Implementors should override this method.
     *
     * @return Start token.
     */
    @Internal
    protected String getBeginToken() {
        '@'
    }

    /** If the default of {@link org.apache.tools.ant.filters.ReplaceTokens} is used, this method
     * will return the end token delimiter.
     *
     * The default is {@code @}. Implementors should override this method.
     *
     * @return End token.
     */
    @Internal
    protected String getEndToken() {
        '@'
    }

    /** If the default of {@link org.apache.tools.ant.filters.ReplaceTokens} is used, this method
     * will return the collection of tokens.
     *
     * The default is an empty map. Implementors should override this method.
     *
     * @return End token.
     */
    @Internal
    protected Map<String, String> getTokenValuesAsMap() {
        [:]
    }

    /** Whether templates should be deleted after usage.
     * This might be necessary if templates are copied to a temporary space.
     *
     * @param del {@code true} if templates should be deleted.
     */
    protected void setDeleteTemplatesAfterUsage(boolean del) {
        this.deleteTemplatesAfterUsage = del
    }

    private enum CopyFilter {
        REPLACETOKENS
    }

    @SuppressWarnings('DuplicateStringLiteral')
    private void validate() {
        if (!prepareTemplates) {
            throw new WrapperCreationException(
                'Method for preparing templates has not been specified. If you see this error in a Gradle plugin, ' +
                    'then please contact the maintainers of the plugin as something has possibly not been setup ' +
                    'correctly in the plugin.'
            )
        }

        if (!copyFilterType) {
            throw new WrapperCreationException(
                'Method for determining copy filter types has not been specified. ' +
                    'If you see this error in a Gradle plugin, ' +
                    'then please contact the maintainers of the plugin as something has possibly not been setup ' +
                    'correctly in the plugin.'
            )
        }
    }

    @SuppressWarnings('DuplicateStringLiteral')
    private Function<Collection<File>,Action<CopySpec>> getCopyAndSubstituteAction() {
        switch (copyFilterType) {
            case CopyFilter.REPLACETOKENS:
                return { Collection<File> templates ->
                    ToolWrapper.wrapperCopyAction(
                        templates,
                        wrapperDestinationDir,
                        beginToken,
                        endToken,
                        tokenValuesAsMap
                    )
                } as Function<Collection<File>,Action<CopySpec>>
            default:
                throw new WrapperCreationException(
                    "${copyFilterType} for template value substitution has not been implemented. " +
                    'If you see this error in a Gradle plugin, ' +
                        'then please contact the maintainers of the plugin as something has possibly not been setup ' +
                        'correctly in the plugin.'
                )
        }
    }

    private Callable<Set<File>> prepareTemplates
    private boolean deleteTemplatesAfterUsage = false
    private final CopyFilter copyFilterType = CopyFilter.REPLACETOKENS
}
