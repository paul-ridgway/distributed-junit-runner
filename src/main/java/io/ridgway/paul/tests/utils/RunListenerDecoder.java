package io.ridgway.paul.tests.utils;

import com.google.common.base.Throwables;
import io.ridgway.paul.tests.api.Event;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RunListenerDecoder implements RunListenerTransport {

    private final RunListener runListener;

    public RunListenerDecoder(final RunListener runListener) {
        this.runListener = runListener;
    }

    @Override
    public void sendEvent(final Event event, final byte[] data) throws Exception {
        switch (event) {
            case TEST_RUN_STARTED:
                runListener.testRunStarted(decode(data, Description.class));
                break;
            case TEST_RUN_FINISHED:
                runListener.testRunFinished(decode(data, Result.class));
                break;
            case TEST_STARTED:
                runListener.testStarted(decode(data, Description.class));
                break;
            case TEST_FINISHED:
                runListener.testFinished(decode(data, Description.class));
                break;
            case TEST_FAILURE:
                runListener.testFailure(decode(data, Failure.class));
                break;
            case TEST_ASSUMPTION_FAILURE:
                runListener.testAssumptionFailure(decode(data, Failure.class));
                break;
            case TEST_IGNORED:
                runListener.testIgnored(decode(data, Description.class));
                break;
        }
    }

    private <T> T decode(final byte[] data, final Class<T> clazz) {
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(data);
             final ObjectInputStream objectInputStream = new ObjectInputStream(bais)) {
            final Object obj = objectInputStream.readObject();
            if (obj.getClass().isAssignableFrom(clazz)) {
                //noinspection unchecked
                return (T) obj;
            }
            throw new ClassCastException("Cannot cast "+ obj.getClass().getName() + " to "+ clazz.getName());
        } catch (ClassNotFoundException | IOException e) {
            throw Throwables.propagate(e);
        }
    }

}


