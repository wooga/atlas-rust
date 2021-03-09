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
package org.ysb33r.grolifant.api.v4.git

import groovy.transform.CompileStatic

/** Describes a GitLab archive.
 *
 * @since 0.17.0
 */
@CompileStatic
class GitLabArchive extends org.ysb33r.grolifant.api.v4.git.AbstractCloudGit {

    GitLabArchive() {
        super('GitLab', 'https://gitlab.com')
    }

    /** Calculates an archive path for the specific repository type.
     *
     * @return Returns a path that can be used to locate the archive.
     *   This path relative to the {@link #getBaseUri}.
     */
    @Override
    protected String getArchivePath() {
        final String repo = repository
        final String id = identifier
        "${organisation}/${repo}/-/archive/${id}/${repo}-${id}.zip"
    }
}
