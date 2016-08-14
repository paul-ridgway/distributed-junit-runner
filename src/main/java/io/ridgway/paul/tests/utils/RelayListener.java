package io.ridgway.paul.tests.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.List;

public class RelayListener extends RunListener {

    private final List<RunListener> runListeners = new ArrayList<>();

    public void addRunListener(final RunListener runListener) {
        runListeners.add(runListener);
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
        for (final RunListener runListener : runListeners) {
            runListener.testRunStarted(description);
        }
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        for (final RunListener runListener : runListeners) {
            runListener.testRunFinished(result);
        }
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        for (final RunListener runListener : runListeners) {
            runListener.testStarted(description);
        }
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        for (final RunListener runListener : runListeners) {
            runListener.testFinished(description);
        }
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        for (final RunListener runListener : runListeners) {
            runListener.testFailure(failure);
        }
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        for (final RunListener runListener : runListeners) {
            runListener.testAssumptionFailure(failure);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        for (final RunListener runListener : runListeners) {
            runListener.testIgnored(description);
        }
    }

}
