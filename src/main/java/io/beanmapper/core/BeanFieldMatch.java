package io.beanmapper.core;

import io.beanmapper.annotations.BeanDefault;

import java.lang.annotation.Annotation;

public class BeanFieldMatch<S,T> {
    private S source;
    private T target;
    private BeanField sourceBeanField;
    private BeanField targetBeanField;
    private String targetFieldName;

    public BeanFieldMatch(S source, T target, BeanField sourceBeanField, BeanField targetBeanField, String targetFieldName) {
        this.source = source;
        this.target = target;
        this.sourceBeanField = sourceBeanField;
        this.targetBeanField = targetBeanField;
        this.targetFieldName = targetFieldName;
    }
    public T getTarget() { return target; }
    public String getTargetFieldName() { return targetFieldName; }
    public boolean hasMatchingSource() { return sourceBeanField != null; }
    public Class getTargetClass() { return targetBeanField.getField().getType(); }
    public Object getSourceValue() throws Exception { return sourceBeanField.getObject(source); }
    public boolean targetHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(targetBeanField, annotationClass);
    }
    public boolean sourceHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(sourceBeanField, annotationClass);
    }
    protected boolean hasAnnotation(BeanField beanField, Class<? extends Annotation> annotationClass) {
        return beanField.getField().isAnnotationPresent(annotationClass);
    }
    public Object getSourceDefaultValue() {
        return getDefaultValue(sourceBeanField);
    }
    public Object getTargetDefaultValue() {
        return getDefaultValue(targetBeanField);
    }
    protected Object getDefaultValue(BeanField beanField) {
        return beanField.getField().getDeclaredAnnotation(BeanDefault.class).value();
    }
    public void setTarget(Object value) throws IllegalAccessException {
        targetBeanField.getField().set(target, value);
    }
    public void writeObject(Object value) throws Exception {
        targetBeanField.writeObject(value, target);
    }
    public Object getSourceObject() throws Exception {
        return sourceBeanField.getObject(source);
    }
    public Object getOrCreateTargetObject() throws Exception {
        return targetBeanField.getOrCreate(target);
    }
}
