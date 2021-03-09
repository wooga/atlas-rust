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
package org.ysb33r.grolifant.api.v4.git;

import java.net.URI;

/**
 * A description of a Git repository from a cloud provider
 *
 * @since 0.17.0
 */
public interface CloudGitConfigurator extends CloudGitDescriptor {
    void setBaseUri(URI uri);

    /**
     * Set the organisation.
     *
     * @param org Anything convertible using {@link org.ysb33r.grolifant.api.v4.StringUtils#stringize}
     */
    void setOrganisation(final Object org);

    /**
     * Set the repository
     *
     * @param repo Anything convertible using {@link org.ysb33r.grolifant.api.v4.StringUtils#stringize}
     */
    void setRepository(final Object repo);

    /**
     * Set the branch.
     * <p>
     * Overrides {@link #setTag} and {@link #setCommit}.
     *
     * @param branch Anything convertible using {@link org.ysb33r.grolifant.api.v4.StringUtils#stringize}
     */
    void setBranch(final Object branch);

    /**
     * Set the tag.
     * <p>
     * Overrides {@link #setBranch} and {@link #setCommit}.
     *
     * @param tag Anything convertible using {@link org.ysb33r.grolifant.api.v4.StringUtils#stringize}
     */
    void setTag(final Object tag);

    /**
     * Set the commit to use.
     * <p>
     * Overrides {@link #setTag} and {@link #setBranch}.
     *
     * @param commit Anything convertible using {@link org.ysb33r.grolifant.api.v4.StringUtils#stringize}
     */
    void setCommit(final Object commit);
}
