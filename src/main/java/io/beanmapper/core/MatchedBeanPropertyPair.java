package io.beanmapper.core;

public class MatchedBeanPropertyPair {

    private final BeanProperty sourceBeanProperty;

    private final BeanProperty targetBeanProperty;

    public MatchedBeanPropertyPair(BeanProperty sourceBeanProperty, BeanProperty targetBeanProperty) {
        this.sourceBeanProperty = sourceBeanProperty;
        this.targetBeanProperty = targetBeanProperty;
        setMatchedBeanProperty(sourceBeanProperty);
        setMatchedBeanProperty(targetBeanProperty);
    }

    private void setMatchedBeanProperty(BeanProperty beanProperty) {
        if (beanProperty == null) {
            return;
        }
        beanProperty.setMatched();
    }

    public BeanProperty getSourceBeanProperty() {
        return sourceBeanProperty;
    }

    public BeanProperty getTargetBeanProperty() {
        return targetBeanProperty;
    }
}
