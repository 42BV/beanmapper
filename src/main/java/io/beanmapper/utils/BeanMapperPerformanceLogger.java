package io.beanmapper.utils;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanMapperPerformanceLogger {

    private static final Logger log = LoggerFactory.getLogger(BeanMapperPerformanceLogger.class);
    private static final String LOG_TEMPLATE = "{} Performed operation in {}ms.";

    public static void runTimedTask(String message, Runnable task) {
        Stopwatch stopwatch = Stopwatch.create();
        task.run();
        log.debug(LOG_TEMPLATE, message, stopwatch.stop());
    }

    public static <T> T runTimedTask(String message, Supplier<T> task) {
        Stopwatch stopwatch = Stopwatch.create();
        T result = task.get();
        log.debug(LOG_TEMPLATE, message, stopwatch.stop());
        return result;
    }

    private static class Stopwatch {

        private final long started;

        Stopwatch() {
            this.started = System.nanoTime();
        }

        public static Stopwatch create() {
            return new Stopwatch();
        }

        public double stop() {
            return (System.nanoTime() - this.started) / 1_000_000.0;
        }
    }

}
