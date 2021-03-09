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

/**
 * Provides a fixed name usable as a key.
 *
 * @since 0.4
 */
public interface NamedResolvedExecutableFactory extends ResolvedExecutableFactory {
    /**
     * Returns a name that can be used as a key into a {@link ResolverFactoryRegistry}.
     *
     * @return Key name
     */
    String getName();
}
