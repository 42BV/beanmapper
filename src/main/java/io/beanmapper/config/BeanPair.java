package io.beanmapper.config;

public class BeanPair {

    private final Class<?> sourceClass;
    private final Class<?> targetClass;
    private boolean sourceStrict = false;
    private boolean targetStrict = false;

    public BeanPair(Class sourceClass, Class targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    public BeanPair withStrictSource() {
        this.sourceStrict = true;
        return this;
    }

    public BeanPair withStrictTarget() {
        this.targetStrict = true;
        return this;
    }

    public boolean isSourceStrict() {
        return sourceStrict;
    }

    public boolean isTargetStrict() {
        return targetStrict;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public boolean matches(Class<?> currentSourceClass, Class<?> currentTargetClass) {
        return
                currentSourceClass.isAssignableFrom(sourceClass) &&
                        currentTargetClass.isAssignableFrom(targetClass);
    }

}
