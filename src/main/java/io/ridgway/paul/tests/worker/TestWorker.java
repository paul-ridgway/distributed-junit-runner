package io.ridgway.paul.tests.worker;

import io.ridgway.paul.tests.executor.TestExecutor;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestWorker {

    private static final Logger L = LoggerFactory.getLogger(TestWorker.class);

    public void run(final String host, final int port)
            throws IOException, URISyntaxException, TTransportException, ClassNotFoundException {
        L.info("Runner, host: {}, port: {}", host, port);

        //TODO: Move to method, better condition handling
        //TODO: Use in runner too?
        //TODO: Get jar from server?
//        final Set<URI> classPaths = ClasspathHelper.enumerateClasspath();
//        classPaths.forEach(uri -> L.info("Classpath contains: {}", uri));
//        classPaths.stream()
//                .filter(url -> url.equals(testJar.toURI()))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Test jar does not exist on classpath"));


        L.info("Running executor...");

        final TestExecutor testExecutor = new TestExecutor(host, port);
        testExecutor.start();
    }

}


