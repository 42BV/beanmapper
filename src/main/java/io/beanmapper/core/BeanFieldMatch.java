package io.beanmapper.core;

import java.lang.annotation.Annotation;
import java.util.Map;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.LogicSecuredCheck;
import io.beanmapper.config.RoleSecuredCheck;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.BeanNoLogicSecuredCheckSetException;
import io.beanmapper.exceptions.BeanNoRoleSecuredCheckSetException;

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
            RoleSecuredCheck roleSecuredCheck,
            Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> logicSecuredChecks,
            Boolean enforcedSecuredProperties) {

        boolean accessAllowed = checkForLogicSecured(
                logicSecuredChecks, sourceBeanField, source, target, enforcedSecuredProperties);
        accessAllowed = accessAllowed && checkForLogicSecured(
                logicSecuredChecks, targetBeanField, source, target, enforcedSecuredProperties);

        return accessAllowed && checkForRoleSecured(roleSecuredCheck, enforcedSecuredProperties);
    }

    private boolean checkForRoleSecured(RoleSecuredCheck roleSecuredCheck, Boolean enforcedSecuredProperties) {
        if (roleSecuredCheck == null) {
            checkIfSecuredFieldHandlerNotSet(sourceBeanField, enforcedSecuredProperties);
            checkIfSecuredFieldHandlerNotSet(targetBeanField, enforcedSecuredProperties);
            return true;
        }
        return
            roleSecuredCheck.hasRole(sourceBeanField.getRequiredRoles()) &&
            roleSecuredCheck.hasRole(targetBeanField.getRequiredRoles());
    }

    private boolean checkForLogicSecured(
            Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> logicSecuredChecks,
            BeanField beanField, Object source, Object target, Boolean enforcedSecuredProperties) {

        Class<? extends LogicSecuredCheck> logicSecuredCheckClass = beanField.getLogicSecuredCheck();
        if (logicSecuredCheckClass == null) {
            return true;
        }
        LogicSecuredCheck logicSecuredCheck = logicSecuredChecks.get(beanField.getLogicSecuredCheck());
        if (logicSecuredCheck == null) {
            String message =
                    "Property '" +
                    beanField.getName() +
                    "' has @BeanLogicSecured annotation, but bean for check is missing: " +
                    logicSecuredCheckClass.getName();
            if (enforcedSecuredProperties) {
                throw new BeanNoLogicSecuredCheckSetException(message);
            }
            logger.warn(message);
            return true;
        }
        return logicSecuredCheck.isAllowed(source, target);
    }

    private void checkIfSecuredFieldHandlerNotSet(BeanField beanField, Boolean enforcedSecuredProperties) {
        if (beanField.getRequiredRoles().length > 0) {
            String message =
                    "Property '" +
                    beanField.getName() +
                    "' has @BeanRoleSecured annotation, but RoleSecuredCheck has not been set";
            if (enforcedSecuredProperties) {
                throw new BeanNoRoleSecuredCheckSetException(message);
            }
            logger.warn(message);
        }
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
