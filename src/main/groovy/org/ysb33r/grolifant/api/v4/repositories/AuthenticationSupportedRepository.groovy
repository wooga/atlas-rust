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
package org.ysb33r.grolifant.api.v4.repositories

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.AuthenticationContainer
import org.gradle.api.artifacts.repositories.AuthenticationSupported
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.credentials.Credentials
import org.gradle.internal.reflect.DirectInstantiator
import org.ysb33r.grolifant.internal.v4.LegacyLevel

/** Base class for creating repository types that optionally support authentication.
 *
 * @since 0.17.0
 */
@CompileStatic
class AuthenticationSupportedRepository implements ArtifactRepository, AuthenticationSupported {
    @Override
    void credentials(Class<? extends Credentials> aClass) {

    }

    /** Repository name
     */
    String name

    /** Base class constructor
     *
     * @param project Project this will be associated with.
     */
    protected AuthenticationSupportedRepository(Project project) {
        this.authentications = createAuthenticationContainer()
    }

    @Override
    PasswordCredentials getCredentials() {
        ifPasswordCredentials
    }

    @Override
    void credentials(Action<? super PasswordCredentials> action) {
        PasswordCredentials pw = ifPasswordCredentials
        action.execute(pw)
    }

    @Override
    def <T extends Credentials> T getCredentials(Class<T> aClass) {
        if (this.repCredentials == null) {
            this.repCredentials = aClass.newInstance()
        }

        try {
            aClass.cast(this.repCredentials)
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Current credentials are not of type ${aClass}", e)
        }
    }

    @Override
    def <T extends Credentials> void credentials(Class<T> aClass, Action<? super T> action) {
        T pw = getCredentials(aClass)
        action.execute(pw)
    }

    @Override
    void authentication(Action<? super AuthenticationContainer> action) {
        action.execute(this.authentications)
    }

    @Override
    AuthenticationContainer getAuthentication() {
        this.authentications
    }

    /** Check is any credentials has been accessed.
     *
     * @return {@code true} is any credentials has been accessed
     */
    boolean hasCredentials() {
        this.repCredentials != null
    }

    /** This is currently a NOOP.
     *
     * @param action
     */
    @Override
    void content(Action action) {
    }

    private initCredentialsIfNull() {
        if (this.repCredentials == null) {
            this.repCredentials = new org.ysb33r.grolifant.api.v4.repositories.SimplePasswordCredentials()
        }
    }

    private PasswordCredentials getIfPasswordCredentials() {
        initCredentialsIfNull()
        if (this.repCredentials instanceof PasswordCredentials) {
            (PasswordCredentials) this.repCredentials
        } else {
            throw new IllegalArgumentException('Current credentials do not implemented PasswordCredentials')
        }
    }

    @CompileDynamic
    static private AuthenticationContainer createAuthenticationContainer() {
        if (LegacyLevel.PRE_5_1) {
            new org.gradle.internal.authentication.DefaultAuthenticationContainer(DirectInstantiator.INSTANCE)
        } else {
            // I'm sorry, this is ugly, but this a way to load the API change in Gradle 5.1, but still keep backwards
            // compatibility to older Gradle versions.
            // If https://github.com/gradle/gradle/issues/729 is implemented we can fix this issue.
            Class decorator = AuthenticationSupportedRepository.classLoader.loadClass(
                'org.gradle.api.internal.CollectionCallbackActionDecorator'
            )
            new org.gradle.internal.authentication.DefaultAuthenticationContainer(
                DirectInstantiator.INSTANCE,
                decorator.NOOP
            )
        }
    }
    private Credentials repCredentials
    private final AuthenticationContainer authentications
}
