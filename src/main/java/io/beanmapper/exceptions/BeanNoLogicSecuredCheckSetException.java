package io.beanmapper.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanNoLogicSecuredCheckSetException extends IllegalArgumentException {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public BeanNoLogicSecuredCheckSetException(String message) {
        super(message);
        logger.error(message);
    }
}
