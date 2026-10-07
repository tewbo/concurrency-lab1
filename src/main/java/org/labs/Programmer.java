package org.labs;

import java.util.concurrent.Callable;

public record Programmer(int position, Table table, long maxEatingMillis) implements Callable<Void> {

    @Override
    public Void call() throws InterruptedException {
        while (table.waitForDish(position)) {
            table.waitForTurn(position);
            eat();
            table.finishEating(position);
        }
        return null;
    }

    private void eat() throws InterruptedException {
        Spoon leftSpoon = table.leftSpoon(position);
        Spoon rightSpoon = table.rightSpoon(position);
        leftSpoon.take(position);
        rightSpoon.take(position);
        Pause.randomPause(maxEatingMillis);
        rightSpoon.release(position);
        leftSpoon.release(position);
    }
}
