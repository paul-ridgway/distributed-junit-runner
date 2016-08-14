package io.ridgway.paul.tests.utils;

import io.ridgway.paul.tests.api.Event;

public interface RunListenerTransport {

    void sendEvent(Event event, byte[] data) throws Exception;

}
