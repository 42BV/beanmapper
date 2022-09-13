package io.beanmapper.dynclass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Utility test class to spin up threads with a runnable and wait for them to finish.
 */
public abstract class AbstractConcurrentTest {

    protected boolean run(int threads, Runnable r) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(threads);
        for (int index = 0; index < threads; index++) {
            service.submit(r);
        }
        service.shutdown();
        return service.awaitTermination(10L, TimeUnit.SECONDS);
    }

}
