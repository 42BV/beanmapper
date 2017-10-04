package io.beanmapper.config;

public class StrictMappingProperties {

    /**
     * The classname suffix that determines a source class is to be treated as strict
     * with regards to mapping.
     */
    private String strictSourceSuffix = "Form";

    /**
     * The classname suffix that determines a target class is to be treated as strict
     * with regards to mapping.
     */
    private String strictTargetSuffix = "Result";

    /**
     * Determines if strict mapping convention will be applied. This means that if a source
     * class has the strict source suffix, or a target class has the strict target suffix,
     * the classes will be treated as if they are strict. This implies that all of their
     * properties will require matching properties on the other side.
     */
    private Boolean applyStrictMappingConvention = true;

    public StrictMappingProperties(
            String strictSourceSuffix,
            String strictTargetSuffix,
            Boolean applyStrictMappingConvention) {
        this.strictSourceSuffix = strictSourceSuffix;
        this.strictTargetSuffix = strictTargetSuffix;
        this.applyStrictMappingConvention = applyStrictMappingConvention;
    }

    public String getStrictSourceSuffix() {
        return strictSourceSuffix;
    }

    public String getStrictTargetSuffix() {
        return strictTargetSuffix;
    }

    public Boolean isApplyStrictMappingConvention() {
        return applyStrictMappingConvention;
    }

    public void setStrictSourceSuffix(String strictSourceSuffix) {
        this.strictSourceSuffix = strictSourceSuffix;
    }

    public void setStrictTargetSuffix(String strictTargetSuffix) {
        this.strictTargetSuffix = strictTargetSuffix;
    }

    public void setApplyStrictMappingConvention(Boolean applyStrictMappingConvention) {
        this.applyStrictMappingConvention = applyStrictMappingConvention;
    }

    public BeanPair createBeanPair(Class<?> sourceClass, Class<?> targetClass) {
        BeanPair beanPair = new BeanPair(sourceClass, targetClass);
        if (!isApplyStrictMappingConvention()) {
            return beanPair;
        }
        if (    strictSourceSuffix != null &&
                sourceClass.getCanonicalName().endsWith(strictSourceSuffix)) {
            beanPair = beanPair.withStrictSource();
        }
        if (    strictTargetSuffix != null &&
                targetClass.getCanonicalName().endsWith(strictTargetSuffix)) {
            beanPair = beanPair.withStrictTarget();
        }
        return beanPair;
    }

    public static StrictMappingProperties defaultConfig() {
        return new StrictMappingProperties(
                "Form",
                "Result",
                true);
    }

    public static StrictMappingProperties emptyConfig() {
        return new StrictMappingProperties(
                null,
                null,
                null);
    }

}
