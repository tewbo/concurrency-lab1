package org.labs;

public class Pause {
    public static void pause(long maxPauseTime) {
        long randomPauseTime = (long) (Math.random() * maxPauseTime);
        try {
            Thread.sleep(randomPauseTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
