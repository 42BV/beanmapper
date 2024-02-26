package io.beanmapper.config;

import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.utils.BeanMapperPerformanceLogger;

public class StrictMappingProperties {

    private BeanUnproxy beanUnproxy;

    /**
     * The classname suffix that determines a source class is to be treated as strict
     * with regards to mapping.
     */
    private String strictSourceSuffix;

    /**
     * The classname suffix that determines a target class is to be treated as strict
     * with regards to mapping.
     */
    private String strictTargetSuffix;

    /**
     * Determines if strict mapping convention will be applied. This means that if a source
     * class has the strict source suffix, or a target class has the strict target suffix,
     * the classes will be treated as if they are strict. This implies that all of their
     * properties will require matching properties on the other side.
     */
    private boolean applyStrictMappingConvention;

    public StrictMappingProperties(
            BeanUnproxy beanUnproxy,
            String strictSourceSuffix,
            String strictTargetSuffix,
            boolean applyStrictMappingConvention) {
        this.beanUnproxy = beanUnproxy;
        this.strictSourceSuffix = strictSourceSuffix;
        this.strictTargetSuffix = strictTargetSuffix;
        this.applyStrictMappingConvention = applyStrictMappingConvention;
    }

    public static StrictMappingProperties defaultConfig() {
        return new StrictMappingProperties(
                null,
                "Form",
                "Result",
                true);
    }

    public static StrictMappingProperties emptyConfig() {
        return new StrictMappingProperties(
                null,
                null,
                null,
                false);
    }

    public String getStrictSourceSuffix() {
        return strictSourceSuffix;
    }

    public void setStrictSourceSuffix(String strictSourceSuffix) {
        this.strictSourceSuffix = strictSourceSuffix;
    }

    public String getStrictTargetSuffix() {
        return strictTargetSuffix;
    }

    public void setStrictTargetSuffix(String strictTargetSuffix) {
        this.strictTargetSuffix = strictTargetSuffix;
    }

    public boolean isApplyStrictMappingConvention() {
        return applyStrictMappingConvention;
    }

    public void setApplyStrictMappingConvention(boolean applyStrictMappingConvention) {
        this.applyStrictMappingConvention = applyStrictMappingConvention;
    }

    public BeanPair createBeanPair(Class<?> sourceClass, Class<?> targetClass) {
        BeanPair beanPair = BeanMapperPerformanceLogger.runTimed("%s#%s".formatted(this.getClass().getSimpleName(), "createBeanPair(Class, Class) -> BeanUnproxy#unproxy(Class)"), () -> {
            Class<?> unproxiedSource = beanUnproxy.unproxy(sourceClass);
            Class<?> unproxiedTarget = beanUnproxy.unproxy(targetClass);
            return new BeanPair(unproxiedSource, unproxiedTarget);
        });
        if (!isApplyStrictMappingConvention()) {
            return beanPair;
        }
        if (strictSourceSuffix != null &&
            beanPair.getSourceClass().getCanonicalName().endsWith(strictSourceSuffix)) {
            beanPair = beanPair.withStrictSource();
        }
        if (strictTargetSuffix != null &&
            beanPair.getTargetClass().getCanonicalName().endsWith(strictTargetSuffix)) {
            beanPair = beanPair.withStrictTarget();
        }
        return beanPair;
    }

    public void setBeanUnproxy(SkippingBeanUnproxy beanUnproxy) {
        this.beanUnproxy = beanUnproxy;
    }
}
