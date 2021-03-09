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
package org.ysb33r.grolifant.internal.v4.wrappers

import groovy.transform.CompileStatic
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Action
import org.gradle.api.file.CopySpec
import org.ysb33r.grolifant.internal.v4.Transform

/** Implementation utilities for creating tool wrappers
 *
 * @author Schalk W. Cronjé
 *
 * @since 0.14
 */
@CompileStatic
class ToolWrapper {

    /** Copy wrapper templates from resources into a temporary directory
     *
     * @param templateResourcePath Classpath resource to search for templates.
     * @param templateMapping A map of the resource template file name to the final file name
     * @return A collection of template locations that can be transformed using token substitution.
     */
    static Set<File> copyWrappersFromResources(
        final String templateResourcePath,
        final Map<String, String> templateMapping
    ) {
        File templateLocation = File.createTempDir('org_ysb33r_grolifant_internal_wrappers', '')
        Transform.toSet(templateMapping.keySet()) { String template ->
            File templateFile = new File(templateLocation, templateMapping[template])
            ToolWrapper.getResourceAsStream("${templateResourcePath}/${template}").withCloseable { input ->
                templateFile.withOutputStream { output ->
                    output << (InputStream) input
                }
            }
            templateFile
        }
    }

    /** Configures an action for transforming a collection files using Ant-style replace tokens filter.
     *
     * @param templateFiles Collection of files to be transformed
     * @param destDir Destination directory
     * @param beginToken Starting token delimiter
     * @param endToken Ending token delimiter
     * @param tokens Map of token values.
     * @return An {@link Action} for configuring a {@link CopySpec}.
     */
    static Action<CopySpec> wrapperCopyAction(
        Collection<File> templateFiles,
        File destDir,
        String beginToken,
        String endToken,
        Map<String, String> tokens
    ) {
        new Action<CopySpec>() {
            @Override
            void execute(CopySpec copySpec) {
                copySpec.identity {
                    from templateFiles
                    into destDir
                    filter ReplaceTokens, beginToken: beginToken, endToken:endToken, tokens: tokens
                    fileMode = 0755
                }
            }
        }
    }
}
