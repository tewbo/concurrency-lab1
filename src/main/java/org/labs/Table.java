package org.labs;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public final class Table {
    private final int programmersNumber;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition hungryProgrammer = lock.newCondition();
    private final List<Condition> hasDish;
    private final List<Condition> hasPriorityToEat;
    private final List<Spoon> spoons;
    private final int[] eatenDishes;
    private final BitSet dishes;
    private final BitSet finished;
    private final Comparator<Integer> eatingOrder;
    private final Queue<Integer> waitingForDish;
    private int dishesRemain;
    private int waitersRemain;

    public Table(int programmersNumber, int dishesNumber, int waitersNumber) {
        this.programmersNumber = programmersNumber;
        this.hasDish = newConditions(programmersNumber);
        this.hasPriorityToEat = newConditions(programmersNumber);
        this.spoons = IntStream.range(0, programmersNumber).mapToObj(Spoon::new).toList();
        this.eatenDishes = new int[programmersNumber];
        this.dishes = new BitSet(programmersNumber);
        this.finished = new BitSet(programmersNumber);
        this.eatingOrder = Comparator.<Integer>comparingInt(programmer -> eatenDishes[programmer])
                .thenComparingInt(Integer::intValue);
        this.waitingForDish = new PriorityQueue<>(programmersNumber, eatingOrder);
        this.dishesRemain = dishesNumber;
        this.waitersRemain = waitersNumber;
    }

    public Spoon leftSpoon(int programmer) {
        return spoons.get(programmer);
    }

    public Spoon rightSpoon(int programmer) {
        return spoons.get(rightNeighbour(programmer));
    }

    public boolean waitForDish(int programmer) throws InterruptedException {
        lock.lock();
        try {
            waitingForDish.add(programmer);
            hungryProgrammer.signal();
            while (!dishes.get(programmer)) {
                if (waitersRemain == 0) {
                    finished.set(programmer);
                    signalNeighbours(programmer);
                    return false;
                }
                hasDish.get(programmer).await();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void waitForTurn(int programmer) throws InterruptedException {
        lock.lock();
        try {
            waitForNeighbour(programmer, leftNeighbour(programmer));
            waitForNeighbour(programmer, rightNeighbour(programmer));
        } finally {
            lock.unlock();
        }
    }

    public void finishEating(int programmer) {
        lock.lock();
        try {
            eatenDishes[programmer]++;
            dishes.clear(programmer);
            signalNeighbours(programmer);
        } finally {
            lock.unlock();
        }
    }

    public boolean takeDishFromKitchen() {
        lock.lock();
        try {
            if (dishesRemain == 0) {
                return false;
            }
            dishesRemain--;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int takeOrder() throws InterruptedException {
        lock.lock();
        try {
            while (waitingForDish.isEmpty()) {
                hungryProgrammer.await();
            }
            return waitingForDish.remove();
        } finally {
            lock.unlock();
        }
    }

    public void serveDish(int programmer) {
        lock.lock();
        try {
            dishes.set(programmer);
            hasDish.get(programmer).signal();
        } finally {
            lock.unlock();
        }
    }

    public void finishServing() {
        lock.lock();
        try {
            waitersRemain--;
            hasDish.forEach(Condition::signal);
        } finally {
            lock.unlock();
        }
    }

    public List<Integer> eatenDishes() {
        lock.lock();
        try {
            return Arrays.stream(eatenDishes).boxed().toList();
        } finally {
            lock.unlock();
        }
    }

    private void waitForNeighbour(int programmer, int neighbour) throws InterruptedException {
        while (!finished.get(neighbour) && eatingOrder.compare(programmer, neighbour) > 0) {
            hasPriorityToEat.get(neighbour).signal();
            hasPriorityToEat.get(programmer).await();
        }
    }

    private void signalNeighbours(int programmer) {
        hasPriorityToEat.get(leftNeighbour(programmer)).signal();
        hasPriorityToEat.get(rightNeighbour(programmer)).signal();
    }

    private int leftNeighbour(int programmer) {
        return (programmer + programmersNumber - 1) % programmersNumber;
    }

    private int rightNeighbour(int programmer) {
        return (programmer + 1) % programmersNumber;
    }

    private List<Condition> newConditions(int count) {
        return IntStream.range(0, count).mapToObj(_ -> lock.newCondition()).toList();
    }
}
