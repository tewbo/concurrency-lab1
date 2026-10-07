package org.labs;

import java.util.concurrent.atomic.AtomicInteger;

public final class Spoon {
    private static final int FREE = -1;

    private final int position;
    private final AtomicInteger owner = new AtomicInteger(FREE);

    public Spoon(int position) {
        this.position = position;
    }

    public void take(int programmer) {
        if (!owner.compareAndSet(FREE, programmer)) {
            throw new IllegalStateException("Programmer " + programmer + " tried to take spoon " + position
                    + " which is held by programmer " + owner.get());
        }
    }

    public void release(int programmer) {
        if (!owner.compareAndSet(programmer, FREE)) {
            throw new IllegalStateException("Programmer " + programmer + " tried to release spoon " + position
                    + " which is held by programmer " + owner.get());
        }
    }
}
