package org.labs;

public record DinnerConfig(
        int programmersNumber,
        int dishesNumber,
        int waitersNumber,
        long maxEatingMillis,
        long maxServingMillis
) {
    public DinnerConfig {
        if (programmersNumber < 2) {
            throw new IllegalArgumentException(
                    "At least 2 programmers are required, got " + programmersNumber);
        }
        if (dishesNumber < 0) {
            throw new IllegalArgumentException(
                    "Dishes number must be non-negative, got " + dishesNumber);
        }
        if (waitersNumber < 1) {
            throw new IllegalArgumentException(
                    "At least 1 waiter is required, got " + waitersNumber);
        }
        if (maxEatingMillis < 0) {
            throw new IllegalArgumentException(
                    "Max eating time must be non-negative, got " + maxEatingMillis);
        }
        if (maxServingMillis < 0) {
            throw new IllegalArgumentException(
                    "Max serving time must be non-negative, got " + maxServingMillis);
        }
    }

    public DinnerConfig(int programmersNumber, int dishesNumber, int waitersNumber) {
        this(programmersNumber, dishesNumber, waitersNumber, 0, 0);
    }
}
