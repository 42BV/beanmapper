package io.beanmapper.exceptions;

public class SourceFieldAccessException extends RecordMappingException {
    public SourceFieldAccessException(Class<? extends Record> recordClass, Class<?> sourceClass, String message, Throwable cause) {
        super(recordClass, sourceClass, message, cause);
    }
}
