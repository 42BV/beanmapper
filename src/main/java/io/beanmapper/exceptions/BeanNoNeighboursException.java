package io.beanmapper.exceptions;

public class BeanNoNeighboursException extends BeanMappingException {

    private static final String ERROR = "Could not find neighbouring classes for target.";

    public BeanNoNeighboursException() {
        super(ERROR);
    }
}
