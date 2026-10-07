package org.labs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Table {
    public final int programmersNumber;
    public final BitSet spoons;
    public final AtomicInteger dishesRemain;
    public final AtomicInteger waitersRemain;
    public final BitSet dishes;
    public final ReentrantLock lock;
    public final Comparator<Integer> comparator;
    public final Queue<Integer> waitingDish;
    public final List<Integer> eatenDishes;
    public final Condition hungryProgrammer;
    public final List<Condition> programmersConditions;


    public Table(int programmersNumber, int dishesTotal, int waiters) {
        this.programmersNumber = programmersNumber;
        this.spoons = new BitSet(programmersNumber);
        this.dishesRemain = new AtomicInteger(dishesTotal);
        this.waitersRemain = new AtomicInteger(waiters);
        this.dishes = new BitSet(programmersNumber);
        this.lock = new ReentrantLock();
        this.eatenDishes = new ArrayList<>(Collections.nCopies(programmersNumber, 0));
        this.comparator = Comparator.comparingInt(eatenDishes::get).thenComparingInt(Integer::intValue);
        this.waitingDish = new PriorityQueue<>(programmersNumber, comparator);
        this.hungryProgrammer = lock.newCondition();
        this.programmersConditions = IntStream.range(0, programmersNumber)
                .mapToObj(_ -> lock.newCondition()).collect(Collectors.toList());
    }

    public int getLeftSpoon(int programmerPosition) {
        return programmerPosition;
    }

    public int getRightSpoon(int programmerPosition) {
        return (programmerPosition + 1) % programmersNumber;
    }

    public enum State {
        EATING,
        SERVING,
        WAITING,
    }
}
