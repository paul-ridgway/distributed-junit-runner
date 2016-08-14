package io.ridgway.paul.tests.utils;

public class Sleep {

    private Sleep() {
    }

    public static void ms(final long ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException ignored) {
        }
    }

}
