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
package org.ysb33r.grolifant.api.v4

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

import org.ysb33r.grolifant.api.errors.ChecksumCreationException

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/** Dealing with URIs
 *
 */
@CompileStatic
class UriUtils {

    /** Attempts to convert object to a URI.
     *
     * Closures can be passed and will be evaluated. Result will then be converted to a URI.
     *
     * @param uriThingy Anything that could be converted to a URI
     * @return URI object
     */
    static URI urize(final Object uriThingy) {
        switch (uriThingy) {
            case URI:
                return (URI) uriThingy
            case Closure:
                return urize(((Closure) uriThingy).call())
            default:
                if (uriThingy.metaClass.respondsTo(uriThingy, 'toURI')) {
                    convertWithNativeUriMethod(uriThingy)
                } else {
                    urize(StringUtils.stringize(uriThingy))
                }
        }
    }

    /** Get final package or directory name from a URI
     *
     * @param uri
     * @return Last part of URI path.
     *
     * @since 0.5
     */
    @SuppressWarnings('UnnecessarySubstring')
    static String getPkgName(final URI uri) {
        final String path = uri.path
        int p = path.lastIndexOf('/')
        (p < 0) ? path : path.substring(p + 1)
    }

    /** Creates a SHA-256 has of a URI.
     *
     * @param uri URI to hash
     * @return
     *
     * @since 0.5
     */
    static String hashURI(final URI uri) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance('SHA256')
            messageDigest.update(uri.toString().bytes)
            new BigInteger(1, messageDigest.digest()).toString(36)
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksumCreationException('Could not create SHA-256 checksum of URI', e)
        }
    }

    /** Create a URI where the user/password is masked out.
     *
     * @param uri Original URI
     * @return URI with no credentials.
     * @since 0.8
     */
    static URI safeUri(URI uri) {
        new URI(uri.scheme, null, uri.host, uri.port, uri.path, uri.query, uri.fragment)
    }

    /** Helper method to call toURI method on object
     *
     * @param uriThingy Object to convert to URI
     * @return URI
     */
    @CompileDynamic
    private static URI convertWithNativeUriMethod(Object uriThingy) {
        uriThingy.toURI()
    }
}
