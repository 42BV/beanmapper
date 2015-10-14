/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

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
     * @param beanMatch the beanmatch containing information for bean construction
     * @return the initialized bean
     */
    <T> T instantiate(Class<T> beanClass, Object... beanMatch);

}
