package io.beanmapper.core;

public record MatchedBeanPropertyPair(BeanProperty sourceBeanProperty, BeanProperty targetBeanProperty) {

    public MatchedBeanPropertyPair {
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
