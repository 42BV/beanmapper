/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.unproxy;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 29, 2015
 */
public class SkippingBeanUnproxy implements BeanUnproxy {
    
    private final BeanUnproxy delegate;
    
    /**
     * The classes to skip when unproxying.
     */
    private final Set<Class<?>> proxyClassesToSkip = new HashSet<Class<?>>();

    public SkippingBeanUnproxy(BeanUnproxy delegate) {
        this.delegate = delegate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> unproxy(Class<?> beanClass) {
        if (isSkippedProxyClass(beanClass)) {
            return beanClass;
        }
        return delegate.unproxy(beanClass);
    }

    /**
     * This method checks whether the class or its superclasses are in the list
     * of skipped classes for unproxying.
     * 
     * @param clazz the class to check
     * @return boolean true if class (or superclass) is in the list, else returns false
     */
    private boolean isSkippedProxyClass(Class<?> clazz) {
        for (Class<?> skipClazz : proxyClassesToSkip) {
            if (skipClazz.equals(clazz)) {
                return true;
            } else if (clazz.getSuperclass() != null) {
                return isSkippedProxyClass(clazz.getSuperclass());
            }
        }
        return false;
    }

    /**
     * Add classes to skip while unproxying to prevent failing of the BeanMapper while mapping
     * proxy classes or classes containing synthetic fields (Like ENUM types).
     * @param clazz the class that is added to the list of skipped classes
     * @return this instance, for chaining
     */
    public final SkippingBeanUnproxy skip(Class<?> clazz) {
        proxyClassesToSkip.add(clazz);
        return this;
    }

}
