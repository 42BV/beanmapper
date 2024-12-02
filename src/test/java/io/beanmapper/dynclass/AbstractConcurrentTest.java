package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

    protected void runVirtual(int threads, Runnable r) throws InterruptedException {
        // Run runnables as Virtual Threads
        Thread[] tr = new Thread[threads];
        for (int t = 0; t < tr.length; t++) {
            tr[t] = Thread.ofVirtual().name("VirtualThread %d".formatted(t)).unstarted(r);
        }

        for (Thread thread : tr) {
            assertTrue(thread.isVirtual());
            thread.start();
        }

        for (Thread thread : tr) {
            thread.join();
        }
    }
}
