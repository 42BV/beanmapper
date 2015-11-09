/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.rule;

import io.beanmapper.core.BeanField;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps each property when the source field is included.
 *
 * @author Jeroen van Schagen
 * @since Nov 6, 2015
 */
public class SourceFieldMapperRule implements BeanMapperRule {
    
    private final Set<String> fieldNames;

    public SourceFieldMapperRule(Collection<String> fieldNames) {
        this.fieldNames = new HashSet<String>(fieldNames);
    }
    
    public SourceFieldMapperRule(String... fieldNames) {
        this(Arrays.asList(fieldNames));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowed(BeanField sourceField, BeanField targetField) {
        boolean allowed = false;
        if (sourceField != null) {
            String fieldName = sourceField.getName();
            allowed = fieldNames.contains(fieldName);
        }
        return allowed;
    }
    
}
