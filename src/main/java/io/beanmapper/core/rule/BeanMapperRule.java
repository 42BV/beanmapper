/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.rule;

import io.beanmapper.core.BeanField;

/**
 * Determines if a field should be mapped or not.
 *
 * @author Jeroen van Schagen
 * @since Nov 6, 2015
 */
public interface BeanMapperRule {
    
    /**
     * Determines if a certain field match should be performed.
     * 
     * @param sourceField the source field
     * @param targetField the target field
     * @return {@code true} if the map should be performed, else {@code false}
     */
    boolean isAllowed(BeanField sourceField, BeanField targetField);
    
}
