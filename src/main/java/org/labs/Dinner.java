package org.labs;

import java.util.concurrent.TimeUnit;

public class Dinner {
    public static void startDinner(int programmersNumber, int dishesNumber, int waitersNumber)  {
        Table table = new Table(programmersNumber, dishesNumber, waitersNumber);
        Waiters waiters = new Waiters(waitersNumber, dishesNumber, table);
        Programmers programmers = new Programmers(programmersNumber, table);

        System.out.println("Starting dinner...");
        waiters.serve();
        programmers.start();

        waiters.executorService.shutdown();
        programmers.executorService.shutdown();
        try {
            if (!waiters.executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                waiters.executorService.shutdownNow();
                System.err.println("Waiters did not terminate");
            }
            if (!programmers.executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                programmers.executorService.shutdownNow();
                System.err.println("Programmers did not terminate");
            }
        } catch (InterruptedException e) {
            waiters.executorService.shutdownNow();
            programmers.executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < programmersNumber; i++) {
            System.out.println(table.eatenDishes.get(i));
        }

    }
}
