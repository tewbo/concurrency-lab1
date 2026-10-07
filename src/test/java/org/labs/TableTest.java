package org.labs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(5)
class TableTest {
    @Test
    void kitchenGivesExactlyDishesNumber() {
        Table table = new Table(2, 3, 1);

        assertTrue(table.takeDishFromKitchen());
        assertTrue(table.takeDishFromKitchen());
        assertTrue(table.takeDishFromKitchen());
        assertFalse(table.takeDishFromKitchen());
    }

    @Test
    void servedProgrammerGetsDish() throws InterruptedException {
        Table table = new Table(2, 1, 1);

        table.serveDish(0);

        assertTrue(table.waitForDish(0));
    }

    @Test
    void programmerLeavesWhenNoWaitersRemain() throws InterruptedException {
        Table table = new Table(2, 0, 1);

        table.finishServing();

        assertFalse(table.waitForDish(0));
    }

    @Test
    void finishEatingCountsDish() throws InterruptedException {
        Table table = new Table(3, 1, 1);

        eatOnce(table, 0);

        assertEquals(List.of(1, 0, 0), table.eatenDishes());
    }

    @Test
    void moreFedProgrammerWaitsForHungrierNeighbours() throws Exception {
        Table table = new Table(3, 10, 1);
        eatOnce(table, 0);
        table.serveDish(0);
        table.waitForDish(0);

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<?> turn = executor.submit(() -> {
                table.waitForTurn(0);
                return null;
            });
            assertThrows(TimeoutException.class, () -> turn.get(100, TimeUnit.MILLISECONDS));

            eatOnce(table, 1);
            assertThrows(TimeoutException.class, () -> turn.get(100, TimeUnit.MILLISECONDS));

            eatOnce(table, 2);
            turn.get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    void finishedNeighbourDoesNotBlockTurn() throws InterruptedException {
        Table table = new Table(2, 10, 1);
        eatOnce(table, 0);
        table.finishServing();
        assertFalse(table.waitForDish(1));

        table.serveDish(0);
        assertTrue(table.waitForDish(0));
        table.waitForTurn(0);
    }

    private static void eatOnce(Table table, int programmer) throws InterruptedException {
        table.serveDish(programmer);
        table.waitForDish(programmer);
        table.waitForTurn(programmer);
        table.finishEating(programmer);
    }
}
