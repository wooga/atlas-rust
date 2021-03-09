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
package org.ysb33r.grolifant.internal.v4

import groovy.transform.CompileStatic

import java.util.function.Function
import java.util.stream.Collectors

/** Transforms a collection to another collection.
 *
 * @since 0.12.1
 */
@CompileStatic
class Transform {
    static <I,O> List<O> toList(final Collection<I> collection, Function<I,O> tx ) {
        collection.stream().map(tx).collect(Collectors.toList())
    }

    static <I,O> Set<O> toSet(final Collection<I> collection, Function<I,O> tx ) {
        collection.stream().map(tx).collect(Collectors.toSet())
    }

    static <I,O> Set<O> toSet(final Iterable<I> collection, Function<I,O> tx ) {
        collection.toList().stream().map(tx).collect(Collectors.toSet())
    }
}
