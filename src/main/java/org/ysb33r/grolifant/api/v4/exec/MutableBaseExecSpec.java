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

import org.gradle.process.BaseExecSpec;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface MutableBaseExecSpec extends BaseExecSpec {
    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec ignoreExitValue(boolean flag);

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec standardInput(InputStream inputStream);

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec standardOutput(OutputStream outputStream);

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec errorOutput(OutputStream outputStream);

    /** Set the working directory for the execution.
     *
     * <p> This version has been introduced to deal with the API change in Gradle 4.0.
     *
     * @param workDir Working directory as a {@code java.io.File} instance.
     */
    void setWorkingDir(File workDir);
}
