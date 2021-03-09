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

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern

/** A class for comparing versions.
 *
 * A major parts of the logic in this class is based upon {@link org.gradle.util.GradleVersion}.
 *
 * @since 0.9
 */
@CompileStatic
@EqualsAndHashCode
class Version implements Comparable<Version> {

    /** The initial parse is used to extract major, minor, patch and any extras.
     *
     */
    static
    final Pattern INITIAL_PARSER = ~/(\p{Digit}+)\.(\p{Digit}+)((?:\.)(\p{Digit}+))?((?:[-_])([\p{Alnum}._\-\+]+))?/

    /** Extract bits out of the extra section.
     *
     */
    static final Pattern EXTRAS_PARSER = ~/((?i:(alpha|beta|rc|m|milestone))((?:[.-_]?)(\p{Digit}+))?)/

    /** Regex to identify extras as SNAPSHOT.
     *
     */
    static final Pattern SNAPSHOT_PARSER = ~/(SNAPSHOT|\p{Digit}{14}([-+]\p{Digit}{4})?)/

    /** Patterns for identifying extras which also contains a SNAPSHOT.
     *
     */
    static final Pattern EXTRAS_WITH_SNAPSHOT_PARSER = ~/${EXTRAS_PARSER.pattern()}(?:-)${SNAPSHOT_PARSER.pattern()}/

    /** The version the class was created with
     *
     */
    final String version

    /** The major version of the version.
     *
     * Never {@code null}.
     */
    final int major

    /** The minor part of the version.
     *
     * Never {@code null}.
     */
    final int minor

    /** The patch part of the version.
     *
     * Can be {@code null}.
     */
    final Integer patch

    /** Whether the version is a snapshot.
     *
     */
    final boolean snapshot = false

    /** Whether the version is an alpha.
     *
     */
    final boolean alpha = false

    /** Whether the version is a beta.
     *
     */
    final boolean beta = false

    /** Whether the version is an RC.
     *
     */
    final boolean rc = false

    /** Whether the version is a milestone.
     *
     */
    final boolean milestone = false

    final String snapShotSequence
    final Integer alphaSequence
    final Integer betaSequence
    final Integer rcSequence
    final Integer milestoneSequence

    static class Parts {
        Integer major
        Integer minor
        Integer patch
        String snapshot
        Integer alpha
        Integer beta
        Integer rc
        Integer milestone
    }

    static class ParseException extends RuntimeException {
        ParseException(final String msg) {
            super(msg)
        }

        ParseException(final String msg, Throwable e) {
            super(msg, e)
        }
    }

    /** Creates a new version instance from a parse-able string.
     *
     * @param ver Version string to parse.
     * @return Version instance.
     * @throw {@link Version.ParseException} is parsing failed
     */
    static Version of(final String ver) {
        new Version(ver, defaultParser(ver))
    }

    /** Creates a new version instance from a parse-able string using a custom parser
     *
     * @param ver Version string to parse.
     * @param parser Custom parser to use
     * @return Version instance.
     * @throw {@link Version.ParseException} is parsing failed
     */
    static Version of(final String ver, Function<String, Parts> parser) {
        new Version(ver, parser.apply(ver))
    }

    /** The default parser will look for something that roughly matches the
     * following regex: {@code \d+\.\d+(\.\d+)?(-(alpha|beta|rc)\.\d+)?(-(SNAPSHOT|\d{14}))?}.
     *
     * @param ver Version to parse
     * @return Parsed components
     * @throws {@link Version.ParseException} if the version could nto be parsed.
     */
    static Parts defaultParser(final String ver) {
        Parts parts = new Parts()
        Matcher matcher = ver =~ INITIAL_PARSER
        if (!matcher.matches()) {
            throw new Version.ParseException("Cannot decompose '${ver}' into version parts.")
        }

        extractMajorMinorRevision(parts, matcher)
        String extras = extractExtras(matcher)

        if (!extras) {
            return parts
        }

        Matcher extrasMatcher = extras =~ EXTRAS_PARSER
        if (extrasMatcher.matches()) {
            extractClassifier(parts, extrasMatcher)
            return parts
        }

        extrasMatcher = extras =~ SNAPSHOT_PARSER
        if (extrasMatcher.matches()) {
            extractSnapshot(parts, extrasMatcher)
            return parts
        }

        extrasMatcher = extras =~ EXTRAS_WITH_SNAPSHOT_PARSER
        if (extrasMatcher.matches()) {
            extractClassifier(parts, extrasMatcher)
            extractSnapshot(parts, extrasMatcher, 4)
            return parts
        }

        throw new Version.ParseException("Cannot decompose classifier '${extras}'.")
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param rhs the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    @Override
    int compareTo(final Version rhs) {
        if (major != rhs.major) {
            return major <=> rhs.major
        }

        if (minor != rhs.minor) {
            return minor <=> rhs.minor
        }

        if (patch != rhs.patch) {
            if (patch == null && rhs.patch != null) {
                return -1
            }
            if (patch != null && rhs.patch == null) {
                return 1
            }
            return patch <=> rhs.patch
        }

        // alpha < beta < milestone << rc
        int alphaC = compareStage(alphaSequence, rhs.alphaSequence)
        if (alphaC != 0) {
            return alphaC
        }

        int betaC = compareStage(betaSequence, rhs.betaSequence)
        if (betaC != 0) {
            return betaC
        }

        int mC = compareStage(milestoneSequence, rhs.milestoneSequence)
        if (mC != 0) {
            return mC
        }

        int rcC = compareStage(rcSequence, rhs.rcSequence)
        if (rcC != 0) {
            return rcC
        }

        // If only one is a snapshot the snapshot is always older,
        // because a full release comes after a snapshot
        if (snapshot && !rhs.snapshot) {
            return -1
        } else if (!snapshot && rhs.snapshot) {
            return 1
        } else if (snapshot && rhs.snapshot) {
            return snapShotSequence <=> rhs.snapShotSequence
        }

        0
    }

    /** Creates an instance of a Version object.
     *
     * @param ver Version string that was used
     * @param parts Decomposed parts
     */
    protected Version(final String ver, final Parts parts) {
        this.version = ver
        this.major = parts.major
        this.minor = parts.minor
        this.patch = parts.patch

        if (parts.snapshot != null) {
            this.snapshot = true
            this.snapShotSequence = parts.snapshot
        }

        if (parts.alpha != null) {
            this.alpha = true
            this.alphaSequence = parts.alpha
        }

        if (parts.beta != null) {
            this.beta = true
            this.betaSequence = parts.beta
        }

        if (parts.milestone != null) {
            this.milestone = true
            this.milestoneSequence = parts.milestone
        }

        if (parts.rc != null) {
            this.rc = true
            this.rcSequence = parts.rc
        }
    }

    @CompileDynamic
    static private void extractMajorMinorRevision(final Parts parts, final Matcher matcher) {
        parts.major = extractInteger(matcher[0][1], 'major part')
        parts.minor = extractInteger(matcher[0][2], 'minor part')

        if (matcher[0][4]) {
            parts.patch = extractInteger(matcher[0][4], 'patch part')
        }
    }

    @CompileDynamic
    static private String extractExtras(Matcher matcher) {
        matcher[0][6]
    }

    @CompileDynamic
    static private void extractClassifier(final Parts parts, final Matcher matcher) {
        if (matcher[0][1]) {
            Integer index = matcher[0][4] ? extractInteger(matcher[0][4], 'alpha/beta/rc/milestone part') : 0
            switch (matcher[0][2].toLowerCase()) {
                case 'rc':
                    parts.rc = index
                    break
                case 'alpha':
                    parts.alpha = index
                    break
                case 'beta':
                    parts.beta = index
                    break
                case 'm':
                case 'milestone':
                    parts.milestone = index
            }
        }
    }

    @CompileDynamic
    static private void extractSnapshot(final Parts parts, final Matcher matcher, int offset = 0) {
        if (matcher[0][1 + offset] == 'SNAPSHOT') {
            parts.snapshot = ''
        } else {
            parts.snapshot = matcher[0][1 + offset]
        }
    }

    @SuppressWarnings('CatchThrowable')
    static private Integer extractInteger(final String text, final String failurePart) {
        try {
            Integer.parseInt(text)
        } catch (Throwable t) {
            throw new Version.ParseException("Could not extract ${failurePart}", t)
        }
    }

    private int compareStage(Integer lhs, Integer rhs) {
        if (lhs != rhs) {
            if (lhs == null && rhs != null) {
                return 1
            }
            if (lhs != null && rhs == null) {
                return -1
            }
            return lhs <=> rhs
        }
        0
    }
}
