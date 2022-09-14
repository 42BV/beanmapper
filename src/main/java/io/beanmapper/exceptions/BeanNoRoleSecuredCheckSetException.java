package io.beanmapper.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanNoRoleSecuredCheckSetException extends IllegalArgumentException {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public BeanNoRoleSecuredCheckSetException(String message) {
        super(message);
        logger.error(message);
    }
}
