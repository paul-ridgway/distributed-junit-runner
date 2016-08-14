package io.ridgway.paul.tests.utils;

public interface RunListenerTransport {

    enum Event {
        TEST_RUN_STARTED,
        TEST_RUN_FINISHED,
        TEST_STARTED,
        TEST_FINISHED,
        TEST_FAILURE,
        TEST_ASSUMPTION_FAILURE,
        TEST_IGNORED
    }

    void sendEvent(Event event, byte[] data) throws Exception;

}
