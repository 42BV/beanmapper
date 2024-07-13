package io.beanmapper.exceptions;

public class FieldShadowingException extends BeanMappingException {

    private static final String MESSAGE_TEMPLATE = "Field Shadowing detected; one or more fields are shadowed by occurrence(s) of BeanProperty. %s";

    public FieldShadowingException(String message) {
        super(MESSAGE_TEMPLATE.formatted(message));
    }
}
