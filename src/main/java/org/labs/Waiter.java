package org.labs;

import java.util.concurrent.Callable;

public record Waiter(Table table, long maxServingMillis) implements Callable<Void> {

    @Override
    public Void call() throws InterruptedException {
        while (table.takeDishFromKitchen()) {
            int programmer = table.takeOrder();
            Pause.randomPause(maxServingMillis);
            table.serveDish(programmer);
        }
        table.finishServing();
        return null;
    }
}
