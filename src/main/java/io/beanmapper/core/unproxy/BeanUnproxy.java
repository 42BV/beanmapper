/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.unproxy;

/**
 * Strip proxies from bean classes.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public interface BeanUnproxy {

    /**
     * Removes any potential proxy classes.
     * @param beanClass the bean proxy (could be proxied)
     * @return the unproxied class
     */
    Class<?> unproxy(Class<?> beanClass);

    default BeanUnproxy getDelegate() {
        return null;
    }
}
