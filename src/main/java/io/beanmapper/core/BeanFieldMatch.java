package io.beanmapper.core;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.exceptions.BeanGetFieldException;
import io.beanmapper.exceptions.BeanMappingException;

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
    public boolean hasSimilarClasses() {
        return sourceBeanField.getField().getType().equals(targetBeanField.getField().getType());
    }
    public T getTarget() { return target; }
    public String getTargetFieldName() { return targetFieldName; }
    public boolean hasMatchingSource() { return sourceBeanField != null; }
    public Class getTargetClass() { return targetBeanField.getField().getType(); }
    public Object getSourceValue() throws BeanMappingException {
        return sourceBeanField.getObject(source);
    }
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
    public void setTarget(Object value) throws BeanMappingException {
        try {
            targetBeanField.getField().set(target, value);
        } catch (IllegalAccessException e) {
            throw new BeanGetFieldException(target.getClass(), targetBeanField.getField(), e);
        }
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
