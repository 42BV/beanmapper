package io.beanmapper.core;

import java.util.Collections;
import java.util.List;

import io.beanmapper.config.BeanPair;

public class BeanMatchValidationMessage {

    private boolean logged = false;

    private final List<BeanProperty> fields;

    private final BeanPair beanPair;

    public BeanMatchValidationMessage(BeanPair beanPair, List<BeanProperty> fields) {
        this.beanPair = beanPair;
        this.fields = fields;
    }

    public List<BeanProperty> getFields() {
        return this.fields != null ? this.fields : Collections.emptyList();
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
