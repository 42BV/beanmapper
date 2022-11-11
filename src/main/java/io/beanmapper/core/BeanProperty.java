package io.beanmapper.core;

import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.LogicSecuredCheck;
import io.beanmapper.core.constructor.ConstructorArguments;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.core.generics.DirectedBeanProperty;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.utils.DefaultValues;
import io.beanmapper.utils.Records;

public class BeanProperty {

    private final String name;

    private final PropertyAccessor accessor;
    private final DirectedBeanProperty directedBeanProperty;
    private BeanProperty next;
    private BeanCollectionInstructions collectionInstructions;
    private String[] requiredRoles = new String[0];
    private Class<? extends LogicSecuredCheck> logicSecuredCheck;
    private boolean mustMatch = false;
    private boolean matched = false;

    public BeanProperty(String name, BeanPropertyMatchupDirection matchupDirection,
            PropertyAccessor accessor, Class containingClass) {
        this.name = name;
        this.accessor = accessor;
        this.directedBeanProperty =
                new DirectedBeanProperty(matchupDirection, accessor, containingClass);
    }

    public void setMustMatch(boolean mustMatch) {
        this.mustMatch = mustMatch;
    }

    public String getName() {
        return getName("");
    }

    private String getName(String prefix) {
        if (hasNext()) {
            return getNext().getName(prefix + name + ".");
        }
        return prefix + name;
    }

    public BeanCollectionInstructions getCollectionInstructions() {
        return collectionInstructions;
    }

    public void setCollectionInstructions(BeanCollectionInstructions collectionInstructions) {
        this.collectionInstructions = collectionInstructions;
    }

    public boolean hasNext() {
        return next != null;
    }

    public BeanProperty getNext() {
        return next;
    }

    public void setNext(BeanProperty next) {
        this.next = next;
    }

    protected PropertyAccessor getCurrentAccessor() {
        return accessor;
    }

    public PropertyAccessor getAccessor() {
        return getLastFieldInChain().getCurrentAccessor();
    }

    private BeanProperty getLastFieldInChain() {
        return hasNext() ? next.getLastFieldInChain() : this;
    }

    public Object getObject(Object object) throws BeanMappingException {
        Object valueObject = getCurrentAccessor().getValue(object);
        if (hasNext() && valueObject != null) {
            return getNext().getObject(valueObject);
        }
        return valueObject;
    }

    public Object getOrCreate(Object parent, Object source, BeanMatch beanMatch) throws BeanMappingException {
        Object target = getCurrentAccessor().getValue(parent);
        if (target == null) {
            Class<?> type = getCurrentAccessor().getType();

            ConstructorArguments arguments = null;
            if (!type.isAnnotationPresent(BeanConstruct.class)) {
                if (type.isRecord()) {
                    arguments = new ConstructorArguments(source, beanMatch, Records.getRecordFieldNames((Class<? extends Record>) type));
                }
            } else {
                arguments = new ConstructorArguments(source, beanMatch, type.getAnnotation(BeanConstruct.class).value());
            }
            target = new DefaultBeanInitializer().instantiate(type, arguments);
            getCurrentAccessor().setValue(parent, target);
        }
        return target;
    }

    public Object writeObject(Object value, Object parent, Object source, BeanMatch beanMatch) throws BeanMappingException {
        if (hasNext()) {
            if (value != null) {
                getNext().writeObject(value, getOrCreate(parent, source, beanMatch), source, beanMatch);
            } else if (getCurrentAccessor().getValue(parent) != null) {
                // If source is null and target object is filled. The nested target object should be overridden with null.
                getNext().writeObject(null, getCurrentAccessor().getValue(parent), source, beanMatch);
            }
        } else {
            if (value == null && getCurrentAccessor().getType().isPrimitive()) {
                // Primitives types can't be null.
                value = DefaultValues.defaultValueFor(getCurrentAccessor().getType());
            }
            getCurrentAccessor().setValue(parent, value);
        }
        return parent;
    }

    public void setMatched() {
        matched = true;
    }

    public boolean isUnmatched() {
        return mustMatch && !matched;
    }

    public String[] getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(String[] requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    public Class<? extends LogicSecuredCheck> getLogicSecuredCheck() {
        return logicSecuredCheck;
    }

    public void setLogicSecuredCheck(Class<? extends LogicSecuredCheck> logicSecuredCheck) {
        this.logicSecuredCheck = logicSecuredCheck;
    }

    public Class<?> getBeanClass() {
        return getLastFieldInChain().directedBeanProperty.getBeanPropertyClass().getBasicType();
    }

    public Class<?> getGenericClassOfField(int index) {
        return getLastFieldInChain().directedBeanProperty.getGenericClassOfProperty(index);
    }

    public boolean isBeanFieldAvailable() {
        return getLastFieldInChain().directedBeanProperty.isBeanFieldAvailable();
    }
}
