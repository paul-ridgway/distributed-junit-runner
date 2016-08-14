package io.ridgway.paul.tests.manager;

import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.junit.runner.Description;

public class DescriptionAsTest implements Test {
    private final Description description;

    public DescriptionAsTest(final Description description) {
        this.description = description;
    }

    public int countTestCases() {
        return 1;
    }

    public void run(final TestResult result) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@link JUnitResultFormatter} determines the test name by reflection.
     */
    public String getName() {
        return description.getDisplayName();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DescriptionAsTest that = (DescriptionAsTest) o;

        if (!description.equals(that.description)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
