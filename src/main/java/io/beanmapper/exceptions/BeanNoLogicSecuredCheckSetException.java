package io.beanmapper.exceptions;

import io.beanmapper.utils.BeanMapperLogger;

public class BeanNoLogicSecuredCheckSetException extends IllegalArgumentException {

    public BeanNoLogicSecuredCheckSetException(String message) {
        super(message);
        BeanMapperLogger.error(message);
    }
}
