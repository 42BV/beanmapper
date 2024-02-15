package io.beanmapper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanMapperTraceLogger {

    private static final Logger log = LoggerFactory.getLogger(BeanMapperTraceLogger.class);

    public static void log(String message) {
        log.trace(message);
    }

    public static void log(String message, Object... args) {
        log.trace(message, args);
    }
}
