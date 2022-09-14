package io.beanmapper.dynclass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Utility test class to spin up threads with a runnable and wait for them to finish.
 */
public abstract class AbstractConcurrentTest {

    protected void run(int threads, Runnable r) throws InterruptedException {
        Thread[] tr = new Thread[threads];
        for (int t = 0; t < tr.length; t++) {
            tr[t] = new Thread(r);
        }
        for (Thread thread : tr) {
            thread.start();
        }
        for (Thread thread : tr) {
            thread.join();
        }
    }
}
