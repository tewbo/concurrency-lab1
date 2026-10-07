package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Dinner {
    private Dinner() {
    }

    public static List<Integer> serve(DinnerConfig config) throws InterruptedException {
        Table table = new Table(config.programmersNumber(), config.dishesNumber(), config.waitersNumber());
        List<Callable<Void>> participants = new ArrayList<>();
        for (int i = 0; i < config.waitersNumber(); i++) {
            participants.add(new Waiter(table, config.maxServingMillis()));
        }
        for (int i = 0; i < config.programmersNumber(); i++) {
            participants.add(new Programmer(i, table, config.maxEatingMillis()));
        }
        runAll(participants);
        return table.eatenDishes();
    }

    private static void runAll(List<Callable<Void>> tasks) throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(tasks.size())) {
            CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
            tasks.forEach(completionService::submit);
            try {
                for (int i = 0; i < tasks.size(); i++) {
                    completionService.take().get();
                }
            } catch (ExecutionException e) {
                executor.shutdownNow();
                throw new IllegalStateException("Dinner participant failed", e.getCause());
            } catch (InterruptedException e) {
                executor.shutdownNow();
                throw e;
            }
        }
    }
}
