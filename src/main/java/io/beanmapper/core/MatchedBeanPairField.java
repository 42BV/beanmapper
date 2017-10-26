package io.beanmapper.core;

public class MatchedBeanPairField {

    private final BeanField sourceBeanField;

    private final BeanField targetBeanField;

    public MatchedBeanPairField(BeanField sourceBeanField, BeanField targetBeanField) {
        this.sourceBeanField = sourceBeanField;
        this.targetBeanField = targetBeanField;
        setMatchedBeanField(sourceBeanField);
        setMatchedBeanField(targetBeanField);
    }

    private void setMatchedBeanField(BeanField beanField) {
        if (beanField == null) {
            return;
        }
        beanField.setMatched();
    }

    public BeanField getSourceBeanField() {
        return sourceBeanField;
    }

    public BeanField getTargetBeanField() {
        return targetBeanField;
    }
}
