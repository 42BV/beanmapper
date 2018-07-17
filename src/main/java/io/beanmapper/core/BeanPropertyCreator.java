package io.beanmapper.core;

import java.util.Stack;

import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;

public class BeanPropertyCreator {

    private final BeanPropertyMatchupDirection matchupDirection;

    private final Class<?> baseClass;

    private final Route route;

    public BeanPropertyCreator(BeanPropertyMatchupDirection matchupDirection, Class<?> baseClass, String path) {
        this.matchupDirection = matchupDirection;
        this.baseClass = baseClass;
        this.route = new Route(path);
    }

    public BeanProperty determineNodesForPath() {
        return determineNodes(new Stack<>());
    }

    public BeanProperty determineNodesForPath(BeanProperty precedingBeanProperty) {
        return determineNodes(copyNodes(precedingBeanProperty));
    }

    private Stack<BeanProperty> copyNodes(BeanProperty precedingBeanProperty) {
        Stack<BeanProperty> beanProperties = new Stack<>();
        BeanProperty currentPrecedingBeanProperty = precedingBeanProperty;
        Class currentBaseClass = baseClass;
        while (currentPrecedingBeanProperty != null) {
            beanProperties.push(new BeanProperty(
                    currentPrecedingBeanProperty.getName(),
                    matchupDirection,
                    currentPrecedingBeanProperty.getCurrentAccessor(),
                    currentBaseClass
            ));
            currentBaseClass = currentPrecedingBeanProperty.getCurrentAccessor().getType();
            currentPrecedingBeanProperty = currentPrecedingBeanProperty.getNext();
        }
        return beanProperties;
    }

    private BeanProperty determineNodes(Stack<BeanProperty> beanProperties) {
        traversePath(beanProperties);
        return getFirstBeanProperty(beanProperties);
    }

    private BeanProperty getFirstBeanProperty(Stack<BeanProperty> beanProperties) {
        BeanProperty previousBeanProperty = null;
        BeanProperty currentBeanProperty = null;
        while (!beanProperties.empty()) {
            currentBeanProperty = beanProperties.pop();
            if (previousBeanProperty != null) {
                currentBeanProperty.setNext(previousBeanProperty);
            }
            previousBeanProperty = currentBeanProperty;
        }
        return currentBeanProperty;
    }

    private void traversePath(Stack<BeanProperty> beanProperties) {
        Class<?> currentBaseClass = baseClass;
        for (String node : route.getRoute()) {
            final PropertyAccessor property = PropertyAccessors.findProperty(currentBaseClass, node);
            if (property == null) {
                throw new BeanNoSuchPropertyException("Property '" + node + "' does not exist in: " + currentBaseClass.getSimpleName());
            }
            beanProperties.push(new BeanProperty(
                    node,
                    matchupDirection,
                    property,
                    currentBaseClass));
            currentBaseClass = property.getType();
        }
    }

}
