package io.beanmapper.config;

public abstract class AbstractConfiguration implements Configuration {

    public final static boolean FALLBACK_TO_PARENT = true;
    public final static boolean LITERAL_VALUE = false;

    @Override
    public void setCollectionClass(Class collectionClass) {
        setCollectionClass(collectionClass, LITERAL_VALUE);
    }

    @Override
    public void setTargetClass(Class targetClass) {
        setTargetClass(targetClass, LITERAL_VALUE);
    }

    @Override
    public void setTarget(Object target) {
        setTarget(target, LITERAL_VALUE);
    }

    @Override
    public void unsetCollectionClass() {
        setCollectionClass(null, FALLBACK_TO_PARENT);
    }

    @Override
    public void unsetTargetClass() {
        setTargetClass(null, FALLBACK_TO_PARENT);
    }

    @Override
    public void unsetTarget() {
        setTarget(null, FALLBACK_TO_PARENT);
    }

    public abstract void setCollectionClass(Class collectionClass, boolean fallbackToParent);

    public abstract void setTargetClass(Class targetClass, boolean fallbackToParent);

    public abstract void setTarget(Object target, boolean fallbackToParent);

}
