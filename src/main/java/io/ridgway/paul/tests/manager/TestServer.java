package io.ridgway.paul.tests.manager;

import io.ridgway.paul.tests.api.TestService;
import io.ridgway.paul.tests.manager.api.TestServiceImpl;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {

    private static final Logger L = LoggerFactory.getLogger(TestServer.class);

    private static final int CLIENT_TIMEOUT_MILLIS = 60000;
    private static final int MAX_FRAME_SIZE = 1024 * 1024;
    private final TNonblockingServer server;
    private final Thread serverThread;

    //TODO: Re-throw exception
    public TestServer(final int port, final TestManager testManager) throws TTransportException {
        L.info("init, port: {}", port);
        final TestServiceImpl service = new TestServiceImpl(testManager);
        final TestService.Processor<TestServiceImpl> processor = new TestService.Processor<>(service);
        final TProcessorFactory processorFactory = new TProcessorFactory(processor);
        final TNonblockingServerSocket transport = new TNonblockingServerSocket(port, CLIENT_TIMEOUT_MILLIS);
        final TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        final TTransportFactory transportFactory = new TFramedTransport.Factory(MAX_FRAME_SIZE);
        final TNonblockingServer.Args args = new TNonblockingServer.Args(transport)
                .processorFactory(processorFactory)
                .protocolFactory(protocolFactory)
                .transportFactory(transportFactory);
        server = new TNonblockingServer(args);
        serverThread = new Thread(server::serve);
    }

    public void start() {
        L.info("start");
        serverThread.start();
    }

    public void shutdown() {
        L.info("shutdown");
        server.stop();
        try {
            L.info("shutdown - join");
            serverThread.join();
        } catch (final InterruptedException e) {
            L.info("Interrupted: {}", e.getMessage(), e);
        }
        L.info("shutdown complete");
    }

}
