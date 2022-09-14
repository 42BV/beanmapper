package io.beanmapper.exceptions;

public class BeanDynamicClassGenerationException extends RuntimeException {

    public static final String ERROR = "Error creating dynamic class for %s with key %s";

    public BeanDynamicClassGenerationException(Throwable rootCause, Class<?> baseClass, String key) {
        super(String.format(ERROR, baseClass.getName(), key), rootCause);
    }

}
