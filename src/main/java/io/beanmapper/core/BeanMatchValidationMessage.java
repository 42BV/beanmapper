package io.beanmapper.core;

import java.util.Collection;

import io.beanmapper.config.BeanPair;

public class BeanMatchValidationMessage {

    private boolean logged = false;

    private final Iterable<BeanProperty> fields;

    private final BeanPair beanPair;

    public BeanMatchValidationMessage(BeanPair beanPair, Iterable<BeanProperty> fields) {
        this.beanPair = beanPair;
        this.fields = fields;
    }

    public Iterable<BeanProperty> getFields() {
        return fields;
    }

    public Class<?> getSourceClass() {
        return beanPair.getSourceClass();
    }

    public Class<?> getTargetClass() {
        return beanPair.getTargetClass();
    }

    public boolean isSourceStrict() {
        return beanPair.isSourceStrict();
    }

    public boolean isTargetStrict() {
        return beanPair.isTargetStrict();
    }

    public Class<?> getStrictClass() {
        if (beanPair.isSourceStrict())
            return getSourceClass();
        else if (beanPair.isTargetStrict())
            return getTargetClass();
        return null;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged() {
        logged = true;
    }

}
