package io.beanmapper.exceptions;

import io.beanmapper.utils.BeanMapperLogger;

public class BeanNoRoleSecuredCheckSetException extends IllegalArgumentException {

    public BeanNoRoleSecuredCheckSetException(String message) {
        super(message);
        BeanMapperLogger.error(message);
    }
}
