/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.rule;

import io.beanmapper.core.BeanField;
import io.beanmapper.core.BeanMatch;

import java.util.*;

/**
 * Maps each property when the source field is included.
 *
 * @author Jeroen van Schagen
 * @since Nov 6, 2015
 */
public class MappableFields {

    private final Set<String> fieldNames;

    public MappableFields(Collection<String> fieldNames) {
        this.fieldNames = new HashSet<String>(fieldNames);
    }

    public MappableFields(String... fieldNames) {
        this(Arrays.asList(fieldNames));
    }

    public BeanMatch compressBeanMatch(BeanMatch beanMatch) {
        Map<String, BeanField> sourceNode = new HashMap<String, BeanField>();
        for(String includeField : fieldNames) {
            sourceNode.put(includeField, beanMatch.getSourceNode().get(includeField));
        }
        return new BeanMatch(beanMatch.getSourceClass(), beanMatch.getTargetClass(), sourceNode, beanMatch.getTargetNode(), beanMatch.getAliases(), this);
    }

    public MappableFields splitForField(String fieldName) {
        Set<String> mappableNestedFields = new HashSet<String>();
        for(String includeField : fieldNames) {
            String[] splitFields = includeField.split("\\.");
            if(splitFields.length > 1 && splitFields[0].equals(fieldName)) {
                mappableNestedFields.add(includeField.substring(splitFields[0].length()+1));
            }
        }
        return new MappableFields(mappableNestedFields);
    }
}
