package io.beanmapper.exceptions;

public abstract class RecordMappingException extends RuntimeException {

    private static final String MESSAGE = "An error occurred while mapping to %s%s";

    protected RecordMappingException(final Class<? extends Record> recordClass, final Class<?> sourceClass, final String message, final Throwable cause) {
        super(MESSAGE.formatted(recordClass.getName(), "from %s.%n%s").formatted(sourceClass.getName(), message), cause);
    }

    protected RecordMappingException(final Class<? extends Record> recordClass, final String message) {
        super(MESSAGE.formatted(recordClass.getName(), ": %s".formatted(message)));
    }

}
