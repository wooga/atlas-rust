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
public interface CloudGitDescriptor {
    /**
     * Obtains the name of the Git service.
     *
     * @return Service name.
     */
    String getName();

    /**
     * Obtains the URI for the archive of the repository.
     *
     * @return Repo URI
     */
    URI getArchiveUri();

    /**
     * Organisation on Git cloud service
     *
     * @return Organisation
     */
    String getOrganisation();

    /**
     * Repository on Git cloud service
     *
     * @return Repository
     */
    String getRepository();
}
