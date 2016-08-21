package io.ridgway.paul.tests.executor;

import io.ridgway.paul.tests.api.TestService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static final Logger L = LoggerFactory.getLogger(Client.class);

    private final TSocket socket;
    private final TestService.Client serviceClient;

    public Client(final String host, final int port) {
        socket = new TSocket(host, port);
        final TFramedTransport transport = new TFramedTransport(socket);
        final TProtocol protocol = new TBinaryProtocol(transport);
        serviceClient = new TestService.Client(protocol);
    }

    public void connect() throws TTransportException {
        L.info("Connecting...");
        socket.open();
        L.info("Connected");
    }

    public void executeVoid(final VoidClientFunction function) throws TException {
        function.execute(serviceClient);
    }

    public <R> R execute(final ClientFunction<R> function) throws TException {
        return function.execute(serviceClient);
    }

}


