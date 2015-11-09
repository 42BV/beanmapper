/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import io.beanmapper.utils.ConstructorArguments;

/**
 * Abstraction that initializes beans.
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public interface BeanInitializer {
    
    /**
     * Initialize a new bean.
     * @param beanClass the bean class
     * @param arguments object containing types and values necessary for constructing using bean construct
     * @return the initialized bean
     */
    <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments);

}
