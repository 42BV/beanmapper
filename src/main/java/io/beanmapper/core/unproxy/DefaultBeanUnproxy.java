/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.unproxy;

/**
 * Default (simple) implementation of bean unproxy.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class DefaultBeanUnproxy implements BeanUnproxy {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> unproxy(Class<?> beanClass) {
        String name = beanClass.getName();
        if (name.contains("$")) {
            if (hasSuperClass(beanClass)) {
                return unproxy(beanClass.getSuperclass());
            }
            Class<?>[] interfaces = beanClass.getInterfaces();
            if (interfaces.length > 0) {
                return beanClass.getInterfaces()[0];
            }
        }
        return beanClass;
    }

    private boolean hasSuperClass(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        return superClass != null && superClass != Object.class;
    }

}