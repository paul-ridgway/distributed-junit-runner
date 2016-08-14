package io.ridgway.paul.tests.manager;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class JUnitResultFormatterAsRunListener extends RunListener {
    protected final JUnitResultFormatter formatter;
    private ByteArrayOutputStream stdout, stderr;
    private PrintStream oldStdout, oldStderr;
    private int problem;
    private long startTime;

    public JUnitResultFormatterAsRunListener(final JUnitResultFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        formatter.startTestSuite(new JUnitTest(description.getDisplayName()));
        formatter.startTest(new DescriptionAsTest(description));
        problem = 0;
        startTime = System.currentTimeMillis();

        this.oldStdout = System.out;
        this.oldStderr = System.err;
        System.setOut(new PrintStream(stdout = new ByteArrayOutputStream()));
        System.setErr(new PrintStream(stderr = new ByteArrayOutputStream()));
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        System.out.flush();
        System.err.flush();
        System.setOut(oldStdout);
        System.setErr(oldStderr);

        formatter.setSystemOutput(stdout.toString());
        formatter.setSystemError(stderr.toString());
        formatter.endTest(new DescriptionAsTest(description));

        final JUnitTest suite = new JUnitTest(description.getDisplayName());
        suite.setCounts(1, problem, 0);
        suite.setRunTime(System.currentTimeMillis() - startTime);
        formatter.endTestSuite(suite);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        testAssumptionFailure(failure);
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        problem++;
        formatter.addError(new DescriptionAsTest(failure.getDescription()), failure.getException());
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        super.testIgnored(description);
    }
}
