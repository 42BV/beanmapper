package io.beanmapper.core;

import java.util.Map;

public class BeanMatch {

    private final Class<?> sourceClass;

    private final Class<?> targetClass;

    private final Map<String, BeanField> sourceNode;

    private final Map<String, BeanField> targetNode;

    public BeanMatch(Class<?> sourceClass, Class<?> targetClass, Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
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

}
