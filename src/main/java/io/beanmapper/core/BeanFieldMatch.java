package io.beanmapper.core;

import java.lang.annotation.Annotation;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.config.SecuredPropertyHandler;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.BeanNoSecuredPropertyHandlerSetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFieldMatch {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private BeanMatch beanMatch;
    private Object source;
    private Object target;
    private BeanField sourceBeanField;
    private BeanField targetBeanField;
    private String targetFieldName;
    private BeanCollectionInstructions beanCollectionInstructions;

    public BeanFieldMatch(Object source, Object target,
            MatchedBeanPairField matchedBeanPairField, String targetFieldName, BeanMatch beanMatch) {
        this.source = source;
        this.target = target;
        this.sourceBeanField = matchedBeanPairField.getSourceBeanField();
        this.targetBeanField = matchedBeanPairField.getTargetBeanField();
        this.targetFieldName = targetFieldName;
        this.beanMatch = beanMatch;
        this.beanCollectionInstructions = BeanCollectionInstructions.merge(
                matchedBeanPairField.getSourceBeanField(),
                matchedBeanPairField.getTargetBeanField());
    }
    public boolean hasAccess(
            SecuredPropertyHandler securedPropertyHandler,
            Boolean enforcedSecuredProperties) {

        if (securedPropertyHandler == null) {
            checkIfSecuredFieldHandlerNotSet(sourceBeanField, enforcedSecuredProperties);
            checkIfSecuredFieldHandlerNotSet(targetBeanField, enforcedSecuredProperties);
            return true;
        }
        return
            securedPropertyHandler.hasRole(sourceBeanField.getRequiredRoles()) &&
            securedPropertyHandler.hasRole(targetBeanField.getRequiredRoles());
    }

    private void checkIfSecuredFieldHandlerNotSet(BeanField beanField, Boolean enforcedSecuredProperties) {
        if (beanField.getRequiredRoles().length > 0) {
            String message = getBeanFieldSecuredPropertyMessage(beanField);
            if (enforcedSecuredProperties) {
                throw new BeanNoSecuredPropertyHandlerSetException(message);
            }
            logger.warn(message);
        }
    }

    private String getBeanFieldSecuredPropertyMessage(BeanField beanField) {
        return
                "Property '" +
                beanField.getName() +
                "' has @BeanSecuredProperty annotation, but SecuredPropertyHandler is not set";
    }

    public boolean hasSimilarClasses() {
        return sourceBeanField.getProperty().getType().equals(targetBeanField.getProperty().getType());
    }
    public Object getTarget() { return target; }
    public String getTargetFieldName() { return targetFieldName; }
    public boolean hasMatchingSource() { return sourceBeanField != null; }
    public boolean isMappable() {
        return sourceBeanField.getProperty().isReadable() && targetBeanField.getProperty().isWritable();
    }
    public Class<?> getSourceClass() {
        return sourceBeanField.getProperty().getType();
    }
    public Class<?> getTargetClass() { return targetBeanField.getProperty().getType(); }
    public boolean targetHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(targetBeanField, annotationClass);
    }
    public boolean sourceHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(sourceBeanField, annotationClass);
    }
    protected boolean hasAnnotation(BeanField beanField, Class<? extends Annotation> annotationClass) {
        return beanField.getProperty().findAnnotation(annotationClass) != null;
    }
    public Object getSourceDefaultValue() {
        return getDefaultValue(sourceBeanField);
    }
    public Object getTargetDefaultValue() {
        return getDefaultValue(targetBeanField);
    }
    protected Object getDefaultValue(BeanField beanField) {
        return beanField.getProperty().findAnnotation(BeanDefault.class).value();
    }
    public void setTarget(Object value) throws BeanMappingException {
        targetBeanField.getProperty().setValue(target, value);
    }
    public void writeObject(Object value) throws BeanMappingException {
        targetBeanField.writeObject(value, target, source, beanMatch);
    }
    public Object getSourceObject() throws BeanMappingException {
        return sourceBeanField.getObject(source);
    }
    public Object getTargetObject() throws BeanMappingException {
        return targetBeanField.getObject(target);
    }
    public BeanCollectionInstructions getCollectionInstructions() {
        return beanCollectionInstructions;
    }

    public String getSourceFieldName() {
        return sourceBeanField.getName();
    }

    public BeanMatch getBeanMatch() {
        return beanMatch;
    }

    public String sourceToString() {
        return source.getClass().getSimpleName() + (sourceBeanField == null ? "" : "." + sourceBeanField.getName());
    }

    public String targetToString() {
        return target.getClass().getSimpleName() + (targetBeanField == null ? "" : "." + targetBeanField.getName());
    }

}
