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

import static org.ysb33r.grolifant.api.v4.StringUtils.stringize

/** Base class for implementing common functionality for GitLab & GitHub
 *
 * @since 0.17.0
 */
@CompileStatic
abstract class AbstractCloudGit implements CloudGitConfigurator {

    URI baseUri

    /** Obtains the URI for the archive of the repository.
     *
     * @return Repo URI
     */
    @Override
    URI getArchiveUri() {
        new URI(
            baseUri.scheme,
            baseUri.rawAuthority,
            baseUri.path.empty ? "/${archivePath}" : "${baseUri.path}/${archivePath}",
            null,
            null
        )
    }

    /** Get the organisation
     *
     * @return Organisation as a string
     */
    String getOrganisation() {
        stringize(this.org)
    }

    /** Set the organisation.
     *
     * @param org Anything convertible using {@link org.ysb33r.grolifant.api.StringUtils#stringize}
     */
    @Override
    void setOrganisation(Object org) {
        this.org = org
    }

    /** Get the repository
     *
     * @return Repository as a string
     */
    String getRepository() {
        stringize(this.repo)
    }

    /** Set the repository
     *
     * @param repo Anything convertible using {@link org.ysb33r.grolifant.api.StringUtils#stringize}
     */
    @Override
    void setRepository(Object repo) {
        this.repo = repo
    }

    /** Set the branch.
     *
     * Overrides {@link #setTag} and {@link #setCommit}.
     *
     * @param branch Anything convertible using {@link org.ysb33r.grolifant.api.StringUtils#stringize}
     */
    @Override
    void setBranch(Object branch) {
        this.identifier = branch
    }

    /** Set the tag.
     *
     * Overrides {@link #setBranch} and {@link #setCommit}.
     *
     * @param tag Anything convertible using {@link org.ysb33r.grolifant.api.StringUtils#stringize}
     */
    @Override
    void setTag(Object tag) {
        this.identifier = tag
    }

    /** Set the commit to use.
     *
     * Overrides {@link #setTag} and {@link #setBranch}.
     *
     * @param commit Anything convertible using {@link org.ysb33r.grolifant.api.StringUtils#stringize}
     */
    @Override
    void setCommit(Object commit) {
        this.identifier = commit
    }

    /** Obtains the name of the Git service.
     *
     * @return Service name.
     */
    @Override
    String getName() {
        this.name
    }

    /** Content as a string
     *
     * @return Equivalent of {@link #getArchiveUri}.
     */
    @Override
    String toString() {
        archiveUri
    }

    /** Constructs a representation of a cloud Git provider
     *
     * @param name Name of provider
     * @param baseUri Base URI to access provider
     */
    protected AbstractCloudGit(final String name, String baseUri) {
        this.name = name
        this.baseUri = baseUri.toURI()
    }

    /** Returns an identifier of an instance in the repository.
     *
     * @return A branch, tag or commit.
     */
    protected String getIdentifier() {
        stringize(this.identifier)
    }

    /** Calculates an archive path for the specific repository type.
     *
     * @return Returns a path that can be used to locate the archive.
     *   This path relative to the {@link #getBaseUri}.
     */
    abstract protected String getArchivePath()

    private Object identifier
    private Object org
    private Object repo
    private final String name
}
