/*
 * Copyright 2021 Wooga GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wooga.gradle.rust.internal

import org.gradle.api.Project
import org.ysb33r.grolifant.api.errors.ConfigurationException
import org.ysb33r.grolifant.api.v4.runnable.AbstractToolExtension
import org.ysb33r.grolifant.api.v4.runnable.ExecUtils
import org.ysb33r.grolifant.api.v4.runnable.ExecutableDownloader
import org.ysb33r.grolifant.loadable.v6.DefaultProjectOperations

class RustToolsExtension extends AbstractToolExtension {

    private static final Map<String, Object> SEARCH_PATH = [search: 'rustc']

    static Map<String, Object> searchPath() {
        SEARCH_PATH
    }

    RustToolsExtension(Project project) {
        super(new DefaultProjectOperations(project))
    }

    @Override
    protected String runExecutableAndReturnVersion() throws ConfigurationException {
        ExecUtils.parseVersionFromOutput(projectOperations, ["--version"], executable.get(), {
            it.split(' ')[1]
        })
    }

    @Override
    protected ExecutableDownloader getDownloader() {
        new RustInstaller(projectOperations)
    }
}
