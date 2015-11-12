package io.beanmapper.core;

import io.beanmapper.core.rule.MappableFields;

import java.util.Map;

public class BeanMatch {

    private final Class<?> sourceClass;

    private final Class<?> targetClass;

    private final Map<String, BeanField> sourceNode;

    private final Map<String, BeanField> targetNode;

    private final Map<String, BeanField> aliases;

    private final MappableFields mappableFields;

    public BeanMatch(Class<?> sourceClass, Class<?> targetClass, Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode, Map<String, BeanField> aliases) {
        this(sourceClass, targetClass, sourceNode, targetNode, aliases, null);
    }

    public BeanMatch(Class<?> sourceClass, Class<?> targetClass, Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode, Map<String, BeanField> aliases, MappableFields mappableFields) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.aliases = aliases;
        this.mappableFields = mappableFields;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Map<String, BeanField> getSourceNode() {
        return sourceNode;
    }

    public Map<String, BeanField> getTargetNode() {
        return targetNode;
    }

    public Map<String, BeanField> getAliases() {
        return aliases;
    }

    public MappableFields getMappableFields() {
        return mappableFields;
    }
}
