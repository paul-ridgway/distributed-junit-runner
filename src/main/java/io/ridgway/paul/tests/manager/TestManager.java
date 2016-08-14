package io.ridgway.paul.tests.manager;

import io.ridgway.paul.tests.utils.RelayListener;
import io.ridgway.paul.tests.utils.RunListenerDecoder;
import io.ridgway.paul.tests.utils.RunListenerTransport;
import io.ridgway.paul.tests.utils.Sleep;
import org.junit.internal.TextListener;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TestManager {

    private static final Logger L = LoggerFactory.getLogger(TestManager.class);

    private final Result result = new Result();
    private final ConcurrentLinkedDeque<Class> testClasses = new ConcurrentLinkedDeque<>();

    private final RelayListener relayListener;
    private final RunListenerTransport runListenerTransport;

    private volatile boolean running = false;

    public TestManager() {
        relayListener = new RelayListener();
        relayListener.addRunListener(result.createListener());
        relayListener.addRunListener(new TextListener(System.err));
        runListenerTransport = new RunListenerDecoder(relayListener);
    }

    public void addTests(final Set<Class> classes) {
        testClasses.addAll(classes);
    }

    private void join(final Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            L.info("Interrupted: {}", e.getMessage(), e);
        }
    }

    public Result run() {
        running = true;
        while(!testClasses.isEmpty()) {
            L.info("Remaining tests: {}", testClasses.size());
            Sleep.ms(1000);
        }
        return result;
    }

    public Class getNext() {
        if (running) {
            return testClasses.poll();
        }
        return null;
    }

    public RunListenerTransport getRunListener() {
        return runListenerTransport;
    }

}


