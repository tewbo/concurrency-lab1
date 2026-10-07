package org.labs;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Programmers {
    public final int programmersNumber;
    public final Table table;
    public final ExecutorService executorService;
    public final List<Condition> conditions;
    private final BitSet finished;

    public Programmers(int programmersNumber, Table table) {
        this.programmersNumber = programmersNumber;
        this.table = table;
        this.executorService = Executors.newFixedThreadPool(programmersNumber);
        this.conditions = IntStream.range(0, programmersNumber)
                .mapToObj(_ -> table.lock.newCondition())
                .collect(Collectors.toList());
        this.finished = new BitSet(programmersNumber);
    }

    public void start() {
        for (int i = 0; i < programmersNumber; i++) {
            executorService.submit(makeTask(i));
        }
    }

    private Runnable makeTask(int position) {
        return () -> {
            while (true) {
                table.lock.lock();
//                System.out.println(
//                        "Programmer " + position + " is waiting for dish, current eaten dishes: "
//                                + table.eatenDishes.get(position));
                try {
                    if (!table.dishes.get(position)) {
                        table.waitingDish.add(position);
                        table.hungryProgrammer.signal();
                    }
                    int leftNeighbour = getLeftNeighbour(position);
                    int rightNeighbour = getRightNeighbour(position);
                    while (!table.dishes.get(position)) {
                        if (table.waitersRemain.get() == 0) {
                            finished.set(position);
                            conditions.get(leftNeighbour).signal();
                            conditions.get(rightNeighbour).signal();
                            return;
                        }
                        table.hungryProgrammer.signal();
                        table.programmersConditions.get(position).await();
                    }
                    while (!finished.get(leftNeighbour) &&
                            table.comparator.compare(position, leftNeighbour) > 0) {
                        conditions.get(leftNeighbour).signal();
                        conditions.get(position).await();
                    }
                    while (!finished.get(rightNeighbour) &&
                            table.comparator.compare(position, rightNeighbour) > 0) {
                        conditions.get(rightNeighbour).signal();
                        conditions.get(position).await();
                    }

                    table.lock.unlock();
                    Pause.pause(20);
                    table.lock.lock();
                    table.eatenDishes.set(position, table.eatenDishes.get(position) + 1);
                    table.dishes.set(position, false);

                    conditions.get(leftNeighbour).signal();
                    conditions.get(rightNeighbour).signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    table.lock.unlock();
                }
            }
        };
    }

    private int getLeftNeighbour(int position) {
        return (position + programmersNumber - 1) % programmersNumber;
    }

    private int getRightNeighbour(int position) {
        return (position + 1) % programmersNumber;
    }
}
