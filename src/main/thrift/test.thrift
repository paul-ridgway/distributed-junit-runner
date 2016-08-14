namespace java io.ridgway.paul.tests.api

enum Event {
    TEST_RUN_STARTED,
    TEST_RUN_FINISHED,
    TEST_STARTED,
    TEST_FINISHED,
    TEST_FAILURE,
    TEST_ASSUMPTION_FAILURE,
    TEST_IGNORED
}

exception EventException {
    1: string message
}

service TestService {

    string getNext(
        1: string workerId,
    );

    void sendEvent(
        1: Event event,
        2: string data
    ) throws (
        1: EventException execption
    )


}