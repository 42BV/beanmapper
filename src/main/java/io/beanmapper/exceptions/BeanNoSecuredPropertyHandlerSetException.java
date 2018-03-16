package io.beanmapper.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanNoSecuredPropertyHandlerSetException extends IllegalArgumentException {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public BeanNoSecuredPropertyHandlerSetException(String message) {
        super(message);
        logger.error(message);
    }
}
