package io.beanmapper.exceptions;

public class BeanInstantiationException extends BeanMappingException {

    public static final String ERROR = "Not possible to instantiate class %s";

    public BeanInstantiationException(Class classToInstantiate, Throwable rootCause) {
        super(String.format(ERROR, classToInstantiate.getName()), rootCause);
    }

}
