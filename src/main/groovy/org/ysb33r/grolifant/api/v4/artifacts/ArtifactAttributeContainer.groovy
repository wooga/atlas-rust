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
package org.ysb33r.grolifant.api.v4.artifacts

import groovy.transform.CompileStatic
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer

/** Implementation of {@link org.gradle.api.attributes.AttributeContainer}
 *
 * @since 0.17.0
 */
@CompileStatic
class ArtifactAttributeContainer implements AttributeContainer {
    /**
     * Returns the set of attribute keys of this container.
     * @return the set of attribute keys.
     */
    @Override
    Set<Attribute<?>> keySet() {
        this.attrs.keySet()
    }

    /**
     * Sets an attribute value. It is not allowed to use <code>null</code> as
     * an attribute value.
     * @param < T >  the type of the attribute
     * @param key the attribute key
     * @param value the attribute value
     * @return this container
     */
    @Override
    def <T> AttributeContainer attribute(Attribute<T> attribute, T t) {
        attrs.put(attribute, t)
        this
    }

    /**
     * Returns the value of an attribute found in this container, or <code>null</code> if
     * this container doesn't have it.
     * @param < T >  the type of the attribute
     * @param key the attribute key
     * @return the attribute value, or null if not found
     */
    @Override
    def <T> T getAttribute(Attribute<T> attribute) {
        (T) attrs[attribute]
    }

    /**
     * Returns true if this container is empty.
     * @return true if this container is empty.
     */
    @Override
    boolean isEmpty() {
        attrs.empty
    }

    /**
     * Tells if a specific attribute is found in this container.
     * @param key the key of the attribute
     * @return true if this attribute is found in this container.
     */
    @Override
    boolean contains(Attribute<?> attribute) {
        attrs.containsKey(attribute)
    }

    @Override
    AttributeContainer getAttributes() {
        this
    }

    void copyTo(ArtifactAttributeContainer other) {
        other.attrs.putAll(this.attrs)
    }

    private final TreeMap<Attribute<?>, Object> attrs = [:] as TreeMap<Attribute<?>, Object>
}
