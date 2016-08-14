package io.ridgway.paul.tests.executor;

import io.ridgway.paul.tests.manager.TestManager;
import io.ridgway.paul.tests.utils.RelayListener;
import io.ridgway.paul.tests.utils.RunListenerEncoder;
import io.ridgway.paul.tests.utils.Sleep;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutor {

    private static final Logger L = LoggerFactory.getLogger(TestExecutor.class);

    private final ExecutorThread executorThread = new ExecutorThread();

    private final JUnitCore jUnitCore = new JUnitCore();
    private final TestManager testManager;
    private final RunListenerEncoder runListenerEncoder;
    private volatile boolean running = false;

    public TestExecutor(final TestManager testManager) {
        this.testManager = testManager;
        this.runListenerEncoder = new RunListenerEncoder(testManager.getRunListener());
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

    private void runTest(final Class clazz) {
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
                final Class next = testManager.getNext();
                if (next == null) {
                    L.info("Nothing to do...");
                    Sleep.ms(1000);
                } else {
                    L.info("Next: {}", next.getName());
                    runTest(next);
                }
            }
        }
    }

}

