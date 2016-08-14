package io.ridgway.paul.tests.executor;

import com.google.common.base.Throwables;
import io.ridgway.paul.tests.api.TestService;
import io.ridgway.paul.tests.utils.RunListenerEncoder;
import io.ridgway.paul.tests.utils.Sleep;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutor {

    private static final Logger L = LoggerFactory.getLogger(TestExecutor.class);

    private final ExecutorThread executorThread = new ExecutorThread();

    private final JUnitCore jUnitCore = new JUnitCore();
    private final RunListenerEncoder runListenerEncoder;
    private final TestService.Client client;
    private volatile boolean running = false;

    public TestExecutor() throws TTransportException {
        final TSocket socket = new TSocket("localhost", 10024);
        final TFramedTransport transport = new TFramedTransport(socket);

        //TODO: Move connection handling outside
        L.info("Connecting...");
        socket.open();

        L.info("Connected");

        final TProtocol protocol = new TBinaryProtocol(transport);

        client = new TestService.Client(protocol);

        this.runListenerEncoder = new RunListenerEncoder((event, data) -> client.sendEvent(event, new String(Base64.encodeBase64(data))));
        jUnitCore.addListener(runListenerEncoder);
    }

    public void start() {
        L.info("start");
        running = true;
        executorThread.start();
    }
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
                final String next;
                try {
                    next = client.getNext("test");
                } catch (TException e) {
                    //TODO: Move and handle better
                    L.error("Error: {}", e.getMessage(), e);
                    continue;
                }
                if (next == null) {
                    L.info("Nothing to do...");
                    Sleep.ms(1000);
                } else {
                    L.info("Next: {}", next);
                    runTest(next);
                }
            }
        }
    }

}

