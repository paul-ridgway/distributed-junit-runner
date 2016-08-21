package io.ridgway.paul.tests.executor;

import io.ridgway.paul.tests.api.TestService;
import io.ridgway.paul.tests.utils.Sleep;
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
    public static final int RECONNECT_WAIT_MS = 5000;

    private final TSocket socket;
    private final TestService.Client serviceClient;

    public Client(final String host, final int port) {
        socket = new TSocket(host, port);
        final TFramedTransport transport = new TFramedTransport(socket);
        final TProtocol protocol = new TBinaryProtocol(transport);
        serviceClient = new TestService.Client(protocol);
    }

    private void connect() throws TTransportException {
        L.info("Connecting...");
        socket.open();
        L.info("Connected");
    }

    private void preExecute() {
        while (!socket.isOpen()) {
            L.info("Socket is not open, connecting");
            try {
                connect();
            } catch (final TTransportException e) {
                L.warn("Error connecting: {}. Wait...", e.getMessage());
                Sleep.ms(RECONNECT_WAIT_MS);
            }
        }
    }

    public void executeVoid(final VoidClientFunction function) throws TException, ConnectionException {
        connectAndExecute(client -> {
            function.execute(client);
            return null;
        });
    }

    public <R> R execute(final ClientFunction<R> function) throws TException, ConnectionException {
        return connectAndExecute(function);
    }

    //TODO: Add/pass retry/timeout options
    private <R> R connectAndExecute(final ClientFunction<R> function) throws TException, ConnectionException {
        synchronized (socket) {
            preExecute();
            try {
                return function.execute(serviceClient);
            } catch (final TTransportException e) {
                socket.close();
                L.warn("Exception executing: {}", e.getMessage());
                throw new ConnectionException(e.getMessage(), e);
            }
        }
    }

}


