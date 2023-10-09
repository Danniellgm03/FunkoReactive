package org.funkoReactivo.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {

    private long id = 1L;

    private final Lock lock = new ReentrantLock(true);

    private static IdGenerator instance;

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }


    public Long getAndIncrementeId() {
        lock.lock();
        try {
            Long currentId = id;
            id++;
            return currentId;
        } finally {
            lock.unlock();
        }
    }

}
