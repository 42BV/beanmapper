package io.beanmapper.exceptions;

public class RecordMappingToIntermediaryException extends RuntimeException {

    public RecordMappingToIntermediaryException(Class<?> sourceClass, Throwable cause) {
        super("Record %s could not be mapped to intermediary-class. %s".formatted(sourceClass.getName(),
                cause.getMessage()), cause);
    }

}
