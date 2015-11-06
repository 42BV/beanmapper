/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.rule;

import io.beanmapper.core.BeanField;

/**
 * Allows each property to be mapped.
 *
 * @author Jeroen van Schagen
 * @since Nov 6, 2015
 */
public class ConstantMapperRule implements BeanMapperRule {
    
    private final boolean allowed;
    
    public ConstantMapperRule(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowed(BeanField sourceField, BeanField targetField) {
        return allowed;
    }
    
}
