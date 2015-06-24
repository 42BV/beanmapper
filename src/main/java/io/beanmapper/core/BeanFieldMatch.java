package io.beanmapper.core;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.exceptions.BeanMappingException;

import java.lang.annotation.Annotation;

public class BeanFieldMatch {
    
    private Object source;
    private Object target;
    private BeanField sourceBeanField;
    private BeanField targetBeanField;
    private String targetFieldName;

    public BeanFieldMatch(Object source, Object target, BeanField sourceBeanField, BeanField targetBeanField, String targetFieldName) {
        this.source = source;
        this.target = target;
        this.sourceBeanField = sourceBeanField;
        this.targetBeanField = targetBeanField;
        this.targetFieldName = targetFieldName;
    }
    public boolean hasSimilarClasses() {
        return sourceBeanField.getPropertyAccessor().getType().equals(targetBeanField.getPropertyAccessor().getType());
    }
    public Object getTarget() { return target; }
    public String getTargetFieldName() { return targetFieldName; }
    public boolean hasMatchingSource() { return sourceBeanField != null; }
    public Class<?> getSourceClass() {
        return sourceBeanField.getPropertyAccessor().getType();
    }
    public Class<?> getTargetClass() { return targetBeanField.getPropertyAccessor().getType(); }
    public boolean targetHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(targetBeanField, annotationClass);
    }
    public boolean sourceHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(sourceBeanField, annotationClass);
    }
    protected boolean hasAnnotation(BeanField beanField, Class<? extends Annotation> annotationClass) {
        return beanField.getPropertyAccessor().findAnnotation(annotationClass) != null;
    }
    public Object getSourceDefaultValue() {
        return getDefaultValue(sourceBeanField);
    }
    public Object getTargetDefaultValue() {
        return getDefaultValue(targetBeanField);
    }
    protected Object getDefaultValue(BeanField beanField) {
        return beanField.getPropertyAccessor().findAnnotation(BeanDefault.class).value();
    }
    public void setTarget(Object value) throws BeanMappingException {
        targetBeanField.getPropertyAccessor().setValue(target, value);
    }
    public void writeObject(Object value) throws BeanMappingException {
        targetBeanField.writeObject(value, target);
    }
    public Object getSourceObject() throws BeanMappingException {
        return sourceBeanField.getObject(source);
    }
    public Object getOrCreateTargetObject() throws BeanMappingException {
        return targetBeanField.getOrCreate(target);
    }
}
