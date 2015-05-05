package io.beanmapper.core;

import java.util.Map;
import java.util.TreeMap;

public class BeanMatch {

    private Class source;

    private Class target;

    private Map<String, BeanField> sourceNode = new TreeMap<>();

    private Map<String, BeanField> targetNode = new TreeMap<>();

    public BeanMatch(Class source, Class target, Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode) {
        this.source = source;
        this.target = target;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    public Class getSource() {
        return source;
    }

    public Class getTarget() {
        return target;
    }

    public Map<String, BeanField> getSourceNode() {
        return sourceNode;
    }

    public Map<String, BeanField> getTargetNode() {
        return targetNode;
    }

}
