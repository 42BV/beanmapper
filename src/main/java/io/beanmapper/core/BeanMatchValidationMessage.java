package io.beanmapper.core;

import java.util.List;

import io.beanmapper.config.BeanPair;

public class BeanMatchValidationMessage {

    private boolean logged = false;

    private final List<BeanField> fields;

    private final BeanPair beanPair;

    public BeanMatchValidationMessage(BeanPair beanPair, List<BeanField> fields) {
        this.beanPair = beanPair;
        this.fields = fields;
    }

    public List<BeanField> getFields() {
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
        return beanPair.isSourceStrict() ?
                getSourceClass() :
                beanPair.isTargetStrict() ? getTargetClass() : null;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged() {
        logged = true;
    }

}
