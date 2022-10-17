package io.beanmapper.core;

import java.util.ArrayDeque;
import java.util.Deque;

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
        return determineNodes(new ArrayDeque<>());
    }

    public BeanProperty determineNodesForPath(BeanProperty precedingBeanProperty) {
        return determineNodes(copyNodes(precedingBeanProperty));
    }

    private Deque<BeanProperty> copyNodes(BeanProperty precedingBeanProperty) {
        Deque<BeanProperty> beanProperties = new ArrayDeque<>();
        BeanProperty currentPrecedingBeanProperty = precedingBeanProperty;
        Class<?> currentBaseClass = baseClass;
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

    private BeanProperty determineNodes(Deque<BeanProperty> beanProperties) {
        traversePath(beanProperties);
        return getFirstBeanProperty(beanProperties);
    }

    private BeanProperty getFirstBeanProperty(Deque<BeanProperty> beanProperties) {
        BeanProperty previousBeanProperty = null;
        BeanProperty currentBeanProperty = null;
        while (!beanProperties.isEmpty()) {
            currentBeanProperty = beanProperties.pop();
            if (previousBeanProperty != null) {
                currentBeanProperty.setNext(previousBeanProperty);
            }
            previousBeanProperty = currentBeanProperty;
        }
        return currentBeanProperty;
    }

    private void traversePath(Deque<BeanProperty> beanProperties) {
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
