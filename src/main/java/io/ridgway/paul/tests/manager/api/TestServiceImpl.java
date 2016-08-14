package io.ridgway.paul.tests.manager.api;

import io.ridgway.paul.tests.api.Event;
import io.ridgway.paul.tests.api.EventException;
import io.ridgway.paul.tests.api.NoJobsException;
import io.ridgway.paul.tests.api.TestService;
import io.ridgway.paul.tests.manager.TestManager;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServiceImpl implements TestService.Iface {

    private static final Logger L = LoggerFactory.getLogger(TestServiceImpl.class);
    private final TestManager testManager;

    public TestServiceImpl(final TestManager testManager) {
        this.testManager = testManager;
    }

    @Override
    public String getNext(final String workerId) throws NoJobsException {
        L.debug("getNext, workerId: {}", workerId);
        return testManager.getNext().orElseThrow(NoJobsException::new);
    }

    @Override
    public void sendEvent(final Event event, final String data) throws EventException {
        final byte[] bytes = Base64.decodeBase64(data);
        L.debug("sendEvent, event: {}, data: {} bytes", event, bytes.length);
        try {
            testManager.getRunListenerTransport().sendEvent(event, bytes);
        } catch (final Exception e) {
            L.error("Error handling sendEvent: {}", e.getMessage(), e);
            throw new EventException(e.getMessage());
        }
    }
}
