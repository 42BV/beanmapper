package io.beanmapper.exceptions;

public class RecordConstructorNotFoundException extends RuntimeException {

    public <R extends Record> RecordConstructorNotFoundException(Class<R> recordClass, String message, Throwable cause) {
        super("Constructor for record %s could not be found.%n%s".formatted(recordClass.getName(), message), cause);
    }
}
