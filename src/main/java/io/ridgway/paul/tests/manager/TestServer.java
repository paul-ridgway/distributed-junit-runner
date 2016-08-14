package io.ridgway.paul.tests.manager;

import io.ridgway.paul.tests.api.TestService;
import io.ridgway.paul.tests.manager.api.TestServiceImpl;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {

    private static final Logger L = LoggerFactory.getLogger(TestServer.class);

    private static final int CLIENT_TIMEOUT_MILLIS = 60000;
    private static final int MAX_FRAME_SIZE = 1024 * 1024;
    private static final int MAX_WORKER_THREADS = 32;
    private final TThreadPoolServer server;
    private final TestServiceImpl service;

    //TODO: Re-throw exception
    public TestServer(final int port, final TestManager testManager) throws TTransportException {
        L.info("init, port: {}", port);
        service = new TestServiceImpl(testManager);
        final TestService.Processor<TestServiceImpl> processor = new TestService.Processor<>(service);
        final TProcessorFactory processorFactory = new TProcessorFactory(processor);
        final TServerTransport transport = new TServerSocket(port, CLIENT_TIMEOUT_MILLIS);
        final TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        final TTransportFactory transportFactory = new TFramedTransport.Factory(MAX_FRAME_SIZE);
        final TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).minWorkerThreads(1)
                .maxWorkerThreads(MAX_WORKER_THREADS);
        server = new TThreadPoolServer(args);
    }

    public void start() {
        L.info("start");
        //TODO: Hold on to thread, interrupt and join on stop
        new Thread(server::serve).start();
    }

    public void stop() {
        L.info("stop");
        server.stop();
    }

}
