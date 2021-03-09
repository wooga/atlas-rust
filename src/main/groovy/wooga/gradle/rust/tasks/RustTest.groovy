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

package wooga.gradle.rust.tasks

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ClosureBackedAction
import wooga.gradle.rust.testing.RustTestReport
import wooga.gradle.rust.testing.internal.DefaultRustTestReport

import javax.inject.Inject
import java.util.regex.Matcher
import java.util.regex.Pattern

@CompileStatic
class RustTest extends AbstractRustLifecycleTask implements Reporting<RustTestReport> {

    private final RustTestReport reports

    @Internal
    @Override
    RustTestReport getReports() {
        return reports
    }

    @Override
    RustTestReport reports(Closure closure) {
        return reports(new ClosureBackedAction<RustTestReport>(closure));
    }

    @Override
    RustTestReport reports(Action<? super RustTestReport> configureAction) {
        configureAction.execute(reports)
        return reports
    }

    @Override
    protected String getCargoCommand() {
        'test'
    }

    private final Provider<List<String>> cargoCommandArguments

    @Override
    protected Provider<List<String>> getCargoCommandArguments() {
        cargoCommandArguments
    }

    @OutputFile
    File getRustTextReport() {
        reports.getTxt().destination
    }

    @Inject
    protected Instantiator getInstantiator() {
        throw new UnsupportedOperationException()
    }

    RustTest() {
        reports = instantiator.newInstance(DefaultRustTestReport.class, this)
        reports.txt.enabled = true

        cargoCommandArguments = project.provider({
            ["--", '--nocapture']
        })
    }

    @Override
    protected String postProcess(int exitValue, String out, String err) {
        String msg = ''
        def output = out
        if (!output.empty) {
            if (reports.getTxt().isEnabled()) {
                File report = reports.getTxt().destination
                report.parentFile.mkdirs()
                report.text = out
            }

            int numTests = 0
            int passed = 0
            int failed = 0
            int ignored = 0
            int measured = 0
            int filtered = 0
            output.eachLine { String line ->
                Matcher data = line =~ TEST_INDICATORS
                if (data.matches()) {
                    passed += matchAsInteger(data, 1)
                    failed += matchAsInteger(data, 2)
                    ignored += matchAsInteger(data, 3)
                    measured += matchAsInteger(data, 4)
                    filtered += matchAsInteger(data, 5)
                } else if (line.startsWith('running ')) {
                    numTests += line.replaceAll('running ', '').replaceAll(~/\s+tests?/, '').toInteger()
                }
            }

            if (numTests) {
                msg = "${numTests} tests completed, ${passed} passed; ${failed} failed; ${ignored} ignored; ${measured} measured; ${filtered} filtered out."
                if (exitValue) {
                    if (reports.txt.enabled) {
                        msg = """
                        ${msg}

                        Test report can be found at ${reports.txt.destination.toURI()}.
                        """.stripIndent().trim()
                    }

                }
            }
        } else {
            logger.error "Test task ${name} has produced no output."
        }

        msg
    }

    @CompileDynamic
    private int matchAsInteger(Matcher matcher, int index) {
        try {
            matcher[0][index].toInteger()
        } catch (Exception e) {
            logger.error "Could not interpret index ${index} from ${matcher[0]}"
            0
        }
    }

    /** Matches {@code test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured; 0 filtered out}.
     *
     */
    static private
    final Pattern TEST_INDICATORS = ~/^\w+\s+result: \w+\. (\d+) passed; (\d+) failed; (\d+) ignored; (\d+) measured; (\d+).+/
}
