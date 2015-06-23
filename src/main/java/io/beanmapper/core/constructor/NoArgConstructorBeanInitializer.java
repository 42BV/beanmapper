/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import io.beanmapper.exceptions.BeanInstantiationException;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public class NoArgConstructorBeanInitializer implements BeanInitializer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T instantiate(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException(beanClass, e);
        }
    }
    
}
