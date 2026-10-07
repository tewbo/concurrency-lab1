package org.labs;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(10)
class DinnerTest {
    @ParameterizedTest
    @CsvSource({
            "2, 1000, 1",
            "2, 1000, 3",
            "3, 0, 2",
            "5, 1000, 5",
            "7, 2000, 2",
            "10, 2000, 1",
            "10, 7, 3",
    })
    void everyDishIsEatenExactlyOnce(int programmers, int dishes, int waiters) throws InterruptedException {
        List<Integer> eatenDishes = Dinner.serve(new DinnerConfig(programmers, dishes, waiters));

        assertEquals(programmers, eatenDishes.size());
        assertEquals(dishes, eatenDishes.stream().mapToInt(Integer::intValue).sum());
    }

    @ParameterizedTest
    @CsvSource({
            "2, 1000, 1",
            "5, 1000, 5",
            "10, 2000, 1",
            "10, 14, 1",
    })
    void neighboursEatAlmostEqually(int programmers, int dishes, int waiters) throws InterruptedException {
        List<Integer> eatenDishes = Dinner.serve(new DinnerConfig(programmers, dishes, waiters));

        for (int programmer = 0; programmer < programmers; programmer++) {
            int neighbour = (programmer + 1) % programmers;
            int difference = Math.abs(eatenDishes.get(programmer) - eatenDishes.get(neighbour));
            assertTrue(difference <= 2,
                    "Programmers " + programmer + " and " + neighbour + " ate unequally: " + eatenDishes);
        }
    }

    @Test
    void everyoneEatsAlmostEquallyWithReadmeSetup() throws InterruptedException {
        List<Integer> eatenDishes = Dinner.serve(new DinnerConfig(7, 7000, 2));

        int spread = Collections.max(eatenDishes) - Collections.min(eatenDishes);
        assertTrue(spread <= 2, "Programmers ate unequally: " + eatenDishes);
    }

    @RepeatedTest(3)
    void neighboursNeverShareSpoonWhenEatingTakesTime() throws InterruptedException {
        List<Integer> eatenDishes = Dinner.serve(new DinnerConfig(5, 50, 2, 2, 1));

        assertEquals(50, eatenDishes.stream().mapToInt(Integer::intValue).sum());
    }
}
