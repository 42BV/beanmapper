package io.beanmapper.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanMapperPerformanceLogger {

    private static final Logger log = LoggerFactory.getLogger(BeanMapperPerformanceLogger.class);
    private static final String LOG_TEMPLATE = "Performed operation in {}ms. ({})";

    public static void runTimed(String taskName, Runnable task) {
        Stopwatch stopwatch = Stopwatch.create();
        task.run();
        log.debug(LOG_TEMPLATE, stopwatch.stop(), taskName);
    }

    public static <T> T runTimed(String taskName, Supplier<T> task) {
        Stopwatch stopwatch = Stopwatch.create();
        T result = task.get();
        log.debug(LOG_TEMPLATE, stopwatch.stop(), taskName);
        return result;
    }

    public static <T> T runTimed(Supplier<T> task, String unformattedTaskName, Object... messageArguments) {
        if (log.isDebugEnabled()) {
            String taskName = unformattedTaskName.formatted(messageArguments);
            return runTimed(taskName, task);
        }
        return task.get();
    }

    private static class Stopwatch {

        private final Instant started;

        Stopwatch() {
            this.started = Instant.now();
        }

        public static Stopwatch create() {
            return new Stopwatch();
        }

        public double stop() {
            return Duration.between(started, Instant.now()).toNanos() / 1_000_000.0;
        }
    }
}
