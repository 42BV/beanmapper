package io.beanmapper.utils;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanMapperLogger {

    private static final Logger log = LoggerFactory.getLogger(BeanMapperLogger.class);

    private static final String LOG_TEMPLATE = "{} Performed operation in {}ms.";

    public static void log(String message) {
        log.debug(message);
    }

    public static void log(String message, Object... args) {
        log.debug(message, args);
    }

    public static void warn(String message) {
        log.warn(message);
    }

    public static void warn(String message, Object... args) {
        log.warn(message, args);
    }

    public static void error(String message) {
        log.error(message);
    }

    public static void error(String message, Object... args) {
        log.error(message, args);
    }

    public static void logTimed(String message, Runnable runnable) {
        Stopwatch stopwatch = Stopwatch.create();
        runnable.run();
        log.debug(LOG_TEMPLATE, message, stopwatch.stop());
    }

    public static <T> T logTimed(String message, Supplier<T> supplier) {
        Stopwatch stopwatch = Stopwatch.create();
        T result = supplier.get();
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
