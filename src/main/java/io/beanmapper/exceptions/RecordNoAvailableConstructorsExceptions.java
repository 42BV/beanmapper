package io.beanmapper.exceptions;

public class RecordNoAvailableConstructorsExceptions extends RecordMappingException {
    public RecordNoAvailableConstructorsExceptions(Class<? extends Record> recordClass, String message) {
        super(recordClass, message);
    }
}
