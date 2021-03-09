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
package org.ysb33r.grolifant.api

import groovy.transform.CompileStatic
import org.gradle.wrapper.ExclusiveFileAccessManager

import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/** Provides co-operative exclusive file access.
 *
 * <p> This is typically used for files that might be shared between Gradle instances.
 *
 * @since 0.5
 */
@CompileStatic
class ExclusiveFileAccess {

    /** Create exclusive access guard and monitor.
     *
     * @param timeoutMs Time (in milliseconds) to wait for a file to become available before giving up.
     * @param pollIntervalMs Polling interval (in milliseconds).
     */
    ExclusiveFileAccess(int timeoutMs, int pollIntervalMs) {
        accessManager = new ExclusiveFileAccessManager(timeoutMs, pollIntervalMs)
    }

    /** Lock access to a file and perform action.
     *
     * @param onFile File to wait for & lock access to.
     * @param runner Actions to execute whilst access is held.
     */
    public <T> T access(final File onFile, final Closure<T> runner) {
        this.access(onFile, runner as Callable)
    }

    /** Lock access to a file and perform action.
     *
     * @param onFile File to wait for & lock access to.
     * @param runner Actions to execute whilst access is held.
     */
    public <T> T access(final File onFile, final Callable<T> runner) {
        final String key = onFile.canonicalPath
        Lock lock = accessMap.computeIfAbsent(key) {
            new ReentrantReadWriteLock()
        }.writeLock()
        lock.lock()
        try {
            accessManager.access(onFile, runner)
        } finally {
            lock.unlock()
        }
    }

    private final ExclusiveFileAccessManager accessManager

    @SuppressWarnings('FieldName')
    private static final ConcurrentHashMap<String, ReadWriteLock> accessMap = []
}
