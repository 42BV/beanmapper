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

public class BeanPropertyMatch {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final BeanMatch beanMatch;
    private final Object source;
    private final Object target;
    private final BeanProperty sourceBeanProperty;
    private final BeanProperty targetBeanProperty;
    private final String targetFieldName;
    private final BeanCollectionInstructions beanCollectionInstructions;

    public BeanPropertyMatch(Object source, Object target,
            MatchedBeanPropertyPair matchedBeanPropertyPair, String targetFieldName, BeanMatch beanMatch) {
        this.source = source;
        this.target = target;
        this.sourceBeanProperty = matchedBeanPropertyPair.sourceBeanProperty();
        this.targetBeanProperty = matchedBeanPropertyPair.targetBeanProperty();
        this.targetFieldName = targetFieldName;
        this.beanMatch = beanMatch;
        this.beanCollectionInstructions = BeanCollectionInstructions.merge(
                matchedBeanPropertyPair.sourceBeanProperty(),
                matchedBeanPropertyPair.targetBeanProperty());
    }

    public boolean hasAccess(
            RoleSecuredCheck roleSecuredCheck,
            Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> logicSecuredChecks,
            boolean enforcedSecuredProperties) {

        boolean accessAllowed = checkForLogicSecured(
                logicSecuredChecks, sourceBeanProperty, source, target, enforcedSecuredProperties);
        accessAllowed = accessAllowed && checkForLogicSecured(
                logicSecuredChecks, targetBeanProperty, source, target, enforcedSecuredProperties);

        return accessAllowed && checkForRoleSecured(roleSecuredCheck, enforcedSecuredProperties);
    }

    private boolean checkForRoleSecured(RoleSecuredCheck roleSecuredCheck, boolean enforcedSecuredProperties) {
        if (roleSecuredCheck == null) {
            checkIfSecuredFieldHandlerNotSet(sourceBeanProperty, enforcedSecuredProperties);
            checkIfSecuredFieldHandlerNotSet(targetBeanProperty, enforcedSecuredProperties);
            return true;
        }
        return
                roleSecuredCheck.hasRole(sourceBeanProperty.getRequiredRoles()) &&
                        roleSecuredCheck.hasRole(targetBeanProperty.getRequiredRoles());
    }

    private boolean checkForLogicSecured(
            Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> logicSecuredChecks,
            BeanProperty beanProperty, Object source, Object target, boolean enforcedSecuredProperties) {

        Class<? extends LogicSecuredCheck> logicSecuredCheckClass = beanProperty.getLogicSecuredCheck();
        if (logicSecuredCheckClass == null) {
            return true;
        }
        LogicSecuredCheck logicSecuredCheck = logicSecuredChecks.get(beanProperty.getLogicSecuredCheck());
        if (logicSecuredCheck == null) {
            String message =
                    "Property '" +
                            beanProperty.getName() +
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

    private void checkIfSecuredFieldHandlerNotSet(BeanProperty beanProperty, boolean enforcedSecuredProperties) {
        if (beanProperty.getRequiredRoles().length > 0) {
            String message =
                    "Property '" +
                            beanProperty.getName() +
                            "' has @BeanRoleSecured annotation, but RoleSecuredCheck has not been set";
            if (enforcedSecuredProperties) {
                throw new BeanNoRoleSecuredCheckSetException(message);
            }
            logger.warn(message);
        }
    }

    public boolean hasSimilarClasses() {
        return getSourceClass().equals(getTargetClass());
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object value) throws BeanMappingException {
        targetBeanProperty.getAccessor().setValue(target, value);
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public boolean hasMatchingSource() {
        return sourceBeanProperty != null;
    }

    public boolean isMappable() {
        return sourceBeanProperty.getAccessor().isReadable() && targetBeanProperty.getAccessor().isWritable();
    }

    public Class<?> getSourceClass() {
        return sourceBeanProperty.getBeanClass();
    }

    public Class<?> getTargetClass() {
        return targetBeanProperty.getBeanClass();
    }

    public boolean targetHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(targetBeanProperty, annotationClass);
    }

    public boolean sourceHasAnnotation(Class<? extends Annotation> annotationClass) {
        return hasAnnotation(sourceBeanProperty, annotationClass);
    }

    protected boolean hasAnnotation(BeanProperty beanProperty, Class<? extends Annotation> annotationClass) {
        return beanProperty.getAccessor().findAnnotation(annotationClass) != null;
    }

    public Object getSourceDefaultValue() {
        return getDefaultValue(sourceBeanProperty);
    }

    public Object getTargetDefaultValue() {
        return getDefaultValue(targetBeanProperty);
    }

    protected Object getDefaultValue(BeanProperty beanProperty) {
        return beanProperty.getAccessor().findAnnotation(BeanDefault.class).value();
    }

    public void writeObject(Object value) throws BeanMappingException {
        targetBeanProperty.writeObject(value, target, source, beanMatch);
    }

    public Object getSourceObject() throws BeanMappingException {
        return sourceBeanProperty.getObject(source);
    }

    public Object getTargetObject() throws BeanMappingException {
        return targetBeanProperty.isBeanFieldAvailable() ? targetBeanProperty.getObject(target) : null;
    }

    public BeanCollectionInstructions getCollectionInstructions() {
        return beanCollectionInstructions;
    }

    public String getSourceFieldName() {
        return sourceBeanProperty.getName();
    }

    public BeanMatch getBeanMatch() {
        return beanMatch;
    }

    public String sourceToString() {
        return source.getClass().getSimpleName() + (sourceBeanProperty == null ? "" : "." + sourceBeanProperty.getName());
    }

    public String targetToString() {
        return target.getClass().getSimpleName() + (targetBeanProperty == null ? "" : "." + targetBeanProperty.getName());
    }

}
