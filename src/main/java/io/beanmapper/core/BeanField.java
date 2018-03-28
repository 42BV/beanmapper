package io.beanmapper.core;

import java.util.Stack;

import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.LogicSecuredCheck;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;
import io.beanmapper.strategy.ConstructorArguments;
import io.beanmapper.utils.DefaultValues;

public class BeanField {

    private String name;

    private PropertyAccessor accessor;

    private BeanField next;

    private BeanCollectionInstructions collectionInstructions;

    private String[] requiredRoles = new String[0];

    private Class<? extends LogicSecuredCheck> logicSecuredCheck;

    private boolean mustMatch = false;

    private boolean matched = false;

    public boolean isMustMatch() {
        return mustMatch;
    }

    public void setMustMatch(boolean mustMatch) {
        this.mustMatch = mustMatch;
    }

    public BeanField(String name, PropertyAccessor accessor) {
        this.name = name;
        this.accessor = accessor;
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

    public BeanField getNext() {
        return next;
    }

    public void setNext(BeanField next) {
        this.next = next;
    }

    protected PropertyAccessor getCurrentField() {
        return accessor;
    }

    public PropertyAccessor getProperty() {
        return hasNext() ? getNext().getProperty() : getCurrentField();
    }

    public Object getObject(Object object) throws BeanMappingException {
        Object valueObject = getCurrentField().getValue(object);
        if (hasNext() && valueObject != null) {
            return getNext().getObject(valueObject);
        }
        return valueObject;
    }

    public Object getOrCreate(Object parent, Object source, BeanMatch beanMatch) throws BeanMappingException {
        Object target = getCurrentField().getValue(parent);
        if (target == null) {
            Class<?> type = getCurrentField().getType();
            BeanConstruct beanConstruct = type.getAnnotation(BeanConstruct.class);

            if(beanConstruct == null) {
                target = new DefaultBeanInitializer().instantiate(type, null);
            } else {
                ConstructorArguments arguments = new ConstructorArguments(source, beanMatch, beanConstruct.value());
                target = new DefaultBeanInitializer().instantiate(type, arguments);
            }
            getCurrentField().setValue(parent, target);
        }
        return target;
    }

    public Object writeObject(Object value, Object parent, Object source, BeanMatch beanMatch) throws BeanMappingException {
        if (hasNext()) {
            if(value != null) {
                getNext().writeObject(value, getOrCreate(parent, source, beanMatch), source, beanMatch);
            } else if (getCurrentField().getValue(parent) != null) {
                // If source is null and target object is filled. The nested target object should be overridden with null.
                getNext().writeObject(null, getCurrentField().getValue(parent), source, beanMatch);
            }
        } else {
            if(value == null && getCurrentField().getType().isPrimitive()) {
                // Primitives types can't be null.
                value = DefaultValues.defaultValueFor(getCurrentField().getType());
            }
            getCurrentField().setValue(parent, value);
        }
        return parent;
    }

    public static BeanField determineNodesForPath(Class<?> baseClass, String path) {
        return determineNodes(baseClass, new Route(path), new Stack<BeanField>());
    }

    public static BeanField determineNodesForPath(Class<?> baseClass, String path, BeanField prefixingBeanField) {
        return determineNodes(baseClass, new Route(path), copyNodes(prefixingBeanField));
    }

    private static Stack<BeanField> copyNodes(BeanField prefixingBeanField) {
        Stack<BeanField> beanFields = new Stack<BeanField>();
        BeanField currentPrefixingBeanField = prefixingBeanField;
        while (currentPrefixingBeanField != null) {
            beanFields.push(new BeanField(currentPrefixingBeanField.getName(), currentPrefixingBeanField.getCurrentField()));
            currentPrefixingBeanField = currentPrefixingBeanField.getNext();
        }
        return beanFields;
    }

    private static BeanField determineNodes(Class<?> baseClass, Route route, Stack<BeanField> beanFields) {
        traversePath(baseClass, route, beanFields);
        return getFirstBeanField(beanFields);
    }

    private static BeanField getFirstBeanField(Stack<BeanField> beanFields) {
        BeanField previousBeanField = null;
        BeanField currentBeanField = null;
        while (!beanFields.empty()) {
            currentBeanField = beanFields.pop();
            if (previousBeanField != null) {
                currentBeanField.setNext(previousBeanField);
            }
            previousBeanField = currentBeanField;
        }
        return currentBeanField;
    }

    private static void traversePath(Class<?> baseClass, Route route, Stack<BeanField> beanFields) {
        Class<?> currentBaseClass = baseClass;
        for (String node : route.getRoute()) {
            final PropertyAccessor property = PropertyAccessors.findProperty(currentBaseClass, node);
            if (property == null) {
                throw new BeanNoSuchPropertyException("Property '" + node + "' does not exist in: " + currentBaseClass.getSimpleName());
            }
            beanFields.push(new BeanField(node, property));
            currentBaseClass = property.getType();
        }
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
}
