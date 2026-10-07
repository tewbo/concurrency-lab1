package org.labs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

public class Waiters {
    public final int waitersNumber;
    public final ExecutorService executorService;
    public final Table table;

    public Waiters(int waitersNumber, int dishesNumber, Table table) {
        this.waitersNumber = waitersNumber;
        this.executorService = Executors.newFixedThreadPool(waitersNumber);
        this.table = table;
    }

    public void serve() {
        for (int i = 0; i < waitersNumber; i++) {
            executorService.submit(this::task);
        }
    }

    private void task() {
        int hungryProgrammer;
        while ((table.dishesRemain.getAndUpdate(x -> x - 1)) > 0) {
            table.lock.lock();
            try {
                while (table.waitingDish.isEmpty()) {
                    try {
                        table.hungryProgrammer.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                hungryProgrammer = table.waitingDish.poll();
            } finally {
                table.lock.unlock();
            }
            Pause.pause(20);

            table.lock.lock();
            try {
                table.dishes.set(hungryProgrammer, true);
                table.programmersConditions.get(hungryProgrammer).signal();
            } finally {
                table.lock.unlock();
            }
        }
        table.lock.lock();
        try {
            table.waitersRemain.decrementAndGet();
            table.programmersConditions.forEach(Condition::signal);
        } finally {
            table.lock.unlock();
        }
    }
}
