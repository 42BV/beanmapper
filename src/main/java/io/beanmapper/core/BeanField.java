package io.beanmapper.core;

import io.beanmapper.core.constructor.NoArgConstructorBeanInitializer;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanMappingException;

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

    public void setName(String name) {
        this.name = name;
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

    public PropertyAccessor getPropertyAccessor() {
        return hasNext() ? getNext().getPropertyAccessor() : getCurrentField();
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
            if (source != null && getCurrentField().getType().equals(String.class) && !source.getClass().equals(String.class)) {
                source = source.toString();
            }
            getCurrentField().setValue(parent, source);
        }
        return parent;
    }

    public static BeanField determineNodesForPath(Class<?> baseClass, String path) throws NoSuchFieldException {
        return determineNodes(baseClass, new Route(path), new Stack<>());
    }

    public static BeanField determineNodesForPath(Class<?> baseClass, String path, BeanField prefixingBeanField) throws NoSuchFieldException {
        return determineNodes(baseClass, new Route(path), copyNodes(prefixingBeanField));
    }

    private static Stack<BeanField> copyNodes(BeanField prefixingBeanField) {
        Stack<BeanField> beanFields = new Stack<>();
        while (prefixingBeanField != null) {
            beanFields.push(new BeanField(prefixingBeanField.getName(), prefixingBeanField.getCurrentField()));
            prefixingBeanField = prefixingBeanField.getNext();
        }
        return beanFields;
    }

    private static BeanField determineNodes(Class<?> baseClass, Route route, Stack<BeanField> beanFields) throws NoSuchFieldException {
        if (!traversePath(baseClass, route, beanFields)) return null;
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

    private static boolean traversePath(Class<?> baseClass, Route route, Stack<BeanField> beanFields) throws NoSuchFieldException {
        for (String node : route.getRoute()) {
            final PropertyAccessor property = PropertyAccessors.getProperty(baseClass, node);
            if (property == null) {
                return false;
            }
            beanFields.push(new BeanField(node, property));
            baseClass = property.getType();
        }
        return true;
    }

}
