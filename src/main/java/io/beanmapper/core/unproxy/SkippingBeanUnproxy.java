/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.unproxy;

import java.util.HashSet;
import java.util.Set;

import io.beanmapper.utils.BeanMapperTraceLogger;

/**
 * Unproxy that allows you to configure classes to skip.
 *
 * @author Jeroen van Schagen
 * @since Jun 29, 2015
 */
public class SkippingBeanUnproxy implements BeanUnproxy {

    /**
     * The classes to skip when unproxying.
     */
    private final Set<Class<?>> proxyClassesToSkip = new HashSet<>();

    private BeanUnproxy delegate;

    public SkippingBeanUnproxy(BeanUnproxy delegate) {
        this.skip(Enum.class); // Skip enum classes by default
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> unproxy(Class<?> beanClass) {
        BeanMapperTraceLogger.log("Unproxying {}.", beanClass != null ? beanClass : "null");
        if (isSkippedProxyClass(beanClass)) {
            return beanClass;
        }
        if (beanClass.isAnonymousClass()) {
            beanClass = beanClass.getSuperclass();
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
                boolean skipSuperClass = isSkippedProxyClass(clazz.getSuperclass());
                if (skipSuperClass) {
                    return true;
                }
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

    /**
     * Change the underlying delegate bean unproxy.
     * @param delegate the delegate to set
     */
    public final void setDelegate(BeanUnproxy delegate) {
        this.delegate = delegate;
    }

    @Override
    public final BeanUnproxy getDelegate() {
        return delegate;
    }
}
