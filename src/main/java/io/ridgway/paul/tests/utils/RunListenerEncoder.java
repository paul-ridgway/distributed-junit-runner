package io.ridgway.paul.tests.utils;

import com.google.common.base.Throwables;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RunListenerEncoder extends RunListener {

    private final RunListenerTransport runListenerTransport;

    public RunListenerEncoder(final RunListenerTransport runListenerTransport) {
        this.runListenerTransport = runListenerTransport;
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
        runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_RUN_STARTED, encode(description));
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_RUN_FINISHED, encode(result));
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_STARTED, encode(description));
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_FINISHED, encode(description));
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_FAILURE, encode(failure));
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        try {
            runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_ASSUMPTION_FAILURE, encode(failure));
        } catch (final Exception
                e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        runListenerTransport.sendEvent(RunListenerTransport.Event.TEST_IGNORED, encode(description));
    }

    private byte[] encode(final Serializable object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos)) {
            objectOutputStream.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}


