package io.beanmapper.core;

public class MatchedBeanPairField {

    private final BeanField sourceBeanField;

    private final BeanField targetBeanField;

    public MatchedBeanPairField(BeanField sourceBeanField, BeanField targetBeanField) {
        this.sourceBeanField = sourceBeanField;
        this.targetBeanField = targetBeanField;
    }

    public BeanField getSourceBeanField() {
        return sourceBeanField;
    }

    public BeanField getTargetBeanField() {
        return targetBeanField;
    }
}
