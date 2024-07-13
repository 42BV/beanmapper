package io.beanmapper.exceptions;

public class DuplicateBeanPropertyTargetException extends BeanMappingException {

    private static final String MESSAGE_TEMPLATE = "Multiple BeanProperty-annotations for a single property, contain reference to the same target-class. %s";

    public DuplicateBeanPropertyTargetException(String message) {
        super(MESSAGE_TEMPLATE.formatted(message));
    }
}
