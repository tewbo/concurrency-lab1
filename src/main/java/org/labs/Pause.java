package org.labs;

import java.util.concurrent.ThreadLocalRandom;

public final class Pause {
    private Pause() {
    }

    public static void randomPause(long maxMillis) throws InterruptedException {
        if (maxMillis > 0) {
            Thread.sleep(ThreadLocalRandom.current().nextLong(maxMillis + 1));
        }
    }
}
