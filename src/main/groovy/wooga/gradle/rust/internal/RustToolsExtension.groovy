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

import groovy.transform.InheritConstructors
import org.gradle.api.Project
import org.gradle.api.Task
import org.ysb33r.grolifant.api.v4.exec.AbstractToolExtension
import org.ysb33r.grolifant.api.v4.exec.ResolveExecutableByVersion

class RustToolsExtension extends AbstractToolExtension {

    static final String RUST_DEFAULT = '1.50.0'
    private static final Map<String, Object> SEARCH_PATH = [search: 'rustc']

    static Map<String, Object> searchPath() {
        SEARCH_PATH
    }

    RustToolsExtension(Project project) {
        super(project)
        addVersionResolver(project)
    }

    RustToolsExtension(Task task, String projectExtName) {
        super(task, projectExtName)
        addVersionResolver(project)
    }

    private void addVersionResolver(Project project) {
        def downloaderFactory = new ResolveExecutableByVersion.DownloaderFactory<RustInstaller>() {
            @Override
            RustInstaller create(Map<String, Object> options, String version, Project p) {
                new RustInstaller(p, version)
            }
        }

        def resolver = new ResolveExecutableByVersion.DownloadedExecutable<RustInstaller>() {
            @Override
            File getPath(RustInstaller downloader) {
                downloader.getRustcExecutablePath()
            }
        }

        def resolverFactory = new ResolveExecutableByVersion(project, downloaderFactory, resolver)
        getResolverFactoryRegistry().registerExecutableKeyActions(resolverFactory)
    }
}
