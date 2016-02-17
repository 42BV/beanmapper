package io.beanmapper.config;

public abstract class AbstractConfiguration implements Configuration {

    @Override
    public void unsetCollectionClass() {
        setCollectionClass(null);
    }

    @Override
    public void unsetTargetClass() {
        setTargetClass(null);
    }

    @Override
    public void unsetTarget() {
        setTarget(null);
    }

}
