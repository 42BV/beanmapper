package io.beanmapper.exceptions;

public class BeanNoTargetException extends BeanMappingException {

    public static final String ERROR = "Either an instantiated target, or a target class must be passed. Unable to map";

    public BeanNoTargetException() {
        super(ERROR);
    }

}
