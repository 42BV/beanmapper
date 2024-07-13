package io.beanmapper.core.inspector.models.bean_property_selector;

import java.lang.annotation.Annotation;

import io.beanmapper.annotations.BeanProperty;

public class BeanPropertyImpl implements Annotation, BeanProperty {

    private final String name;
    private final String value;
    private final Class<?>[] targets;

    public BeanPropertyImpl(String name, String value, Class<?>[] targets) {
        this.name = name;
        this.value = value;
        this.targets = targets;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<?>[] targets() {
        return targets;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return BeanProperty.class;
    }
}
