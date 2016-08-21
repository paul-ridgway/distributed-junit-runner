package io.ridgway.paul.tests.executor;

import com.google.common.base.Throwables;
import io.ridgway.paul.tests.api.NoJobsException;
import io.ridgway.paul.tests.utils.RunListenerEncoder;
import io.ridgway.paul.tests.utils.Sleep;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutor {

    private static final Logger L = LoggerFactory.getLogger(TestExecutor.class);

    private final ExecutorThread executorThread = new ExecutorThread();

    private final JUnitCore jUnitCore = new JUnitCore();
    private final RunListenerEncoder runListenerEncoder;
    private final Client client;
    private volatile boolean running = false;

    public TestExecutor(final String host, final int port) throws TTransportException {
        client = new Client(host, port);
        client.connect();

        this.runListenerEncoder = new RunListenerEncoder((event, data) -> client.executeVoid(c -> c.sendEvent(event, new String(Base64.encodeBase64(data)))));
        jUnitCore.addListener(runListenerEncoder);
    }

    public void start() {
        L.info("start");
        running = true;
        executorThread.start();
    }

    //TODO: Add shutdown listener
    public void shutdown() {
        L.info("shutdown");
        running = false;
        executorThread.interrupt();
        try {
            executorThread.join();
        } catch (final InterruptedException e) {
            L.info("Interrupted...");
        }
    }

    private void runTest(final String className) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (final ClassNotFoundException e) {
            //TODO: Handle better
            throw Throwables.propagate(e);
        }
        L.info("Running test on: {}", clazz.getName());
        jUnitCore.run(clazz);
    }

    private class ExecutorThread extends Thread {

        ExecutorThread() {
            setName("TestExecutor-" + getId());
        }

        @Override
        public void run() {
            while (running) {
                L.info("Getting next...");
                try {
                    final String next = client.execute(c -> c.getNext("test"));
                    L.info("Next: {}", next);
                    runTest(next);
                } catch (final NoJobsException ignored) {
                    L.info("Nothing to do...");
                    Sleep.ms(1000);
                } catch (final TException e) {
                    L.error("Error: {}", e.getMessage(), e);
                    Sleep.ms(1000);
                }
            }
        }
    }

}

