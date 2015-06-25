package io.beanmapper.core;

import io.beanmapper.core.constructor.NoArgConstructorBeanInitializer;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.NoSuchPropertyException;

import java.util.Stack;

public class BeanField {

    private String name;

    private PropertyAccessor accessor;

    private BeanField next;

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
        object = getCurrentField().getValue(object);
        if (hasNext()) {
            if (object != null) {
                return getNext().getObject(object);
            }
        }
        return object;
    }

    public Object getOrCreate(Object parent) throws BeanMappingException {
        Object target = getCurrentField().getValue(parent);
        if (target == null) {
            Class<?> type = getCurrentField().getType();
            target = new NoArgConstructorBeanInitializer().instantiate(type);
            getCurrentField().setValue(parent, target);
        }
        return target;
    }

    public Object writeObject(Object source, Object parent) throws BeanMappingException {
        if (hasNext()) {
            getNext().writeObject(source, getOrCreate(parent));
        } else {
            getCurrentField().setValue(parent, source);
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
        while (prefixingBeanField != null) {
            beanFields.push(new BeanField(prefixingBeanField.getName(), prefixingBeanField.getCurrentField()));
            prefixingBeanField = prefixingBeanField.getNext();
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
        for (String node : route.getRoute()) {
            final PropertyAccessor property = PropertyAccessors.findProperty(baseClass, node);
            if (property == null) {
                throw new NoSuchPropertyException("Property '" + node + "' does not exist in: " + baseClass.getSimpleName());
            }
            beanFields.push(new BeanField(node, property));
            baseClass = property.getType();
        }
    }

}
