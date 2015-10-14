package io.beanmapper.core;

import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.NoSuchPropertyException;
import io.beanmapper.utils.ConstructorArguments;
import io.beanmapper.utils.DefaultValues;

import java.util.Stack;

public class BeanField {

    private String name;

    private PropertyAccessor accessor;

    private BeanField next;

    private BeanCollectionInstructions collectionInstructions;

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
        object = getCurrentField().getValue(object);
        if (hasNext()) {
            if (object != null) {
                return getNext().getObject(object);
            }
        }
        return object;
    }

    public Object getOrCreate(Object parent, Object source, BeanMatch beanMatch) throws BeanMappingException {
        Object target = getCurrentField().getValue(parent);
        if (target == null) {
            Class<?> type = getCurrentField().getType();
            BeanConstruct beanConstruct = type.getAnnotation(BeanConstruct.class);

            if(beanConstruct == null) {
                beanConstruct = source.getClass().getAnnotation(BeanConstruct.class);
            }

            if(beanConstruct == null){
                target = new DefaultBeanInitializer().instantiate(type, null);
            }else{
                String[] constructArgs = beanConstruct.value();
                ConstructorArguments arguments = new ConstructorArguments(constructArgs.length);

                for(int i=0; i<constructArgs.length; i++){
                    BeanField constructField = beanMatch.getSourceNode().get(constructArgs[i]);
                    arguments.types[i] = constructField.getProperty().getType();
                    arguments.values[i] = constructField.getObject(source);
                }

                target = new DefaultBeanInitializer().instantiate(type, arguments);
            }

            getCurrentField().setValue(parent, target);
        }
        return target;
    }

    public Object writeObject(Object source, Object parent, BeanMatch beanMatch) throws BeanMappingException {
        if (hasNext()) {
            if(source != null) {
                getNext().writeObject(source, getOrCreate(parent, source, beanMatch), beanMatch);
            } else if (getCurrentField().getValue(parent) != null) {
                // If source is null and target object is filled. The nested target object should be overridden with null.
                getNext().writeObject(null, getCurrentField().getValue(parent), beanMatch);
            }
        } else {
            if(source == null && getCurrentField().getType().isPrimitive()) {
                // Primitives types can't be null.
                source = DefaultValues.defaultValueFor(getCurrentField().getType());
            }
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
