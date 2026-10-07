package org.labs;

import java.util.List;

public class Main {
    private static final int DEFAULT_PROGRAMMERS_NUMBER = 7;
    private static final int DEFAULT_DISHES_NUMBER = 100_000;
    private static final int DEFAULT_WAITERS_NUMBER = 2;
    private static final long DEFAULT_MAX_EATING_MILLIS = 0;
    private static final long DEFAULT_MAX_SERVING_MILLIS = 0;

    public static void main(String[] args) throws InterruptedException {
        DinnerConfig config = new DinnerConfig(
                intArgument(args, 0, DEFAULT_PROGRAMMERS_NUMBER),
                intArgument(args, 1, DEFAULT_DISHES_NUMBER),
                intArgument(args, 2, DEFAULT_WAITERS_NUMBER),
                longArgument(args, 3, DEFAULT_MAX_EATING_MILLIS),
                longArgument(args, 4, DEFAULT_MAX_SERVING_MILLIS));

        System.out.println("Starting dinner: " + config);
        long startNanos = System.nanoTime();
        List<Integer> eatenDishes = Dinner.serve(config);
        long elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000;

        for (int i = 0; i < eatenDishes.size(); i++) {
            System.out.println("Programmer " + i + " ate " + eatenDishes.get(i) + " dishes");
        }
        System.out.println("Dinner finished in " + elapsedMillis + " ms");
    }

    private static int intArgument(String[] args, int index, int defaultValue) {
        return index < args.length ? Integer.parseInt(args[index]) : defaultValue;
    }

    private static long longArgument(String[] args, int index, long defaultValue) {
        return index < args.length ? Long.parseLong(args[index]) : defaultValue;
    }
}
