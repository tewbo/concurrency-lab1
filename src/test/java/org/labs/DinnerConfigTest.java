package org.labs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DinnerConfigTest {
    @ParameterizedTest
    @CsvSource({
            "1, 10, 1, 0, 0",
            "0, 10, 1, 0, 0",
            "2, -1, 1, 0, 0",
            "2, 10, 0, 0, 0",
            "2, 10, 1, -1, 0",
            "2, 10, 1, 0, -1",
    })
    void rejectsInvalidValues(int programmers, int dishes, int waiters, long maxEatingMillis, long maxServingMillis) {
        assertThrows(IllegalArgumentException.class,
                () -> new DinnerConfig(programmers, dishes, waiters, maxEatingMillis, maxServingMillis));
    }

    @Test
    void acceptsMinimalValues() {
        assertDoesNotThrow(() -> new DinnerConfig(2, 0, 1, 0, 0));
    }
}
