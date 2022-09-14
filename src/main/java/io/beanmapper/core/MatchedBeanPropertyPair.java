package io.beanmapper.core;

public record MatchedBeanPropertyPair(BeanProperty sourceBeanProperty, BeanProperty targetBeanProperty) {

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
}
