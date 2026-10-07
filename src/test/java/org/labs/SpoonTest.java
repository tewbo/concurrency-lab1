package org.labs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpoonTest {
    private final Spoon spoon = new Spoon(0);

    @Test
    void ownerCanTakeAndReleaseSpoon() {
        assertDoesNotThrow(() -> {
            spoon.take(1);
            spoon.release(1);
        });
    }

    @Test
    void spoonCanBeTakenAgainAfterRelease() {
        spoon.take(1);
        spoon.release(1);

        assertDoesNotThrow(() -> spoon.take(2));
    }

    @Test
    void takingHeldSpoonFails() {
        spoon.take(1);

        assertThrows(IllegalStateException.class, () -> spoon.take(2));
    }

    @Test
    void releasingSpoonHeldByAnotherProgrammerFails() {
        spoon.take(1);

        assertThrows(IllegalStateException.class, () -> spoon.release(2));
    }

    @Test
    void releasingFreeSpoonFails() {
        assertThrows(IllegalStateException.class, () -> spoon.release(1));
    }

    @Test
    @Timeout(5)
    void onlyOneOfConcurrentProgrammersGetsSpoon() throws Exception {
        int programmersNumber = 8;
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> attempts = new ArrayList<>();
        try (ExecutorService executor = Executors.newFixedThreadPool(programmersNumber)) {
            for (int programmer = 0; programmer < programmersNumber; programmer++) {
                int position = programmer;
                attempts.add(executor.submit(() -> {
                    start.await();
                    try {
                        spoon.take(position);
                        return true;
                    } catch (IllegalStateException e) {
                        return false;
                    }
                }));
            }
            start.countDown();

            int successes = 0;
            for (Future<Boolean> attempt : attempts) {
                if (attempt.get()) {
                    successes++;
                }
            }
            assertEquals(1, successes);
        }
    }
}
