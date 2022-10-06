package io.beanmapper.exceptions;

public class RecordInstantiationException extends RecordMappingException {
    public RecordInstantiationException(Class<? extends Record> recordClass, Class<?> sourceClass, String message, Throwable cause) {
        super(recordClass, sourceClass, message, cause);
    }
}
