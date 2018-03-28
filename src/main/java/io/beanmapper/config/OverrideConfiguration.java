package io.beanmapper.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.annotations.LogicSecuredCheck;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.dynclass.ClassStore;

public class OverrideConfiguration implements Configuration {

    private final Configuration parentConfiguration;

    private OverrideField<BeanInitializer> beanInitializer;

    private List<BeanConverter> beanConverters = new ArrayList<BeanConverter>();
    
    private List<BeanPair> beanPairs = new ArrayList<BeanPair>();

    private OverrideField<List<String>> downsizeSourceFields;

    private OverrideField<List<String>> downsizeTargetFields;

    private Class targetClass;

    private Object target;

    private OverrideField<Object> parent;

    private Class collectionClass;

    private OverrideField<Boolean> converterChoosable;

    private StrictMappingProperties strictMappingProperties = StrictMappingProperties.emptyConfig();

    private BeanCollectionUsage collectionUsage = null;

    private Class<?> preferredCollectionClass = null;

    private Boolean enforcedSecuredProperties = null;

    private OverrideField<Boolean> flushAfterClear;

    private OverrideField<Boolean> flushEnabled;

    public OverrideConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new ParentConfigurationPossiblyNullException("Developer error: the parent configuration may not be null");
        }
        this.parentConfiguration = configuration;
        this.downsizeSourceFields = new OverrideField<>(configuration::getDownsizeSource);
        this.downsizeTargetFields = new OverrideField<>(configuration::getDownsizeTarget);
        this.beanInitializer = new OverrideField<>(configuration::getBeanInitializer);
        this.parent = new OverrideField<>(configuration::getParent);
        this.converterChoosable = new OverrideField<>(configuration::isConverterChoosable);
        this.flushAfterClear = new OverrideField<>(configuration::isFlushAfterClear);
        this.flushEnabled = new OverrideField<>(configuration::isFlushEnabled);
    }

    @Override
    public List<String> getDownsizeSource() {
        return downsizeSourceFields.get();
    }

    @Override
    public List<String> getDownsizeTarget() {
        return downsizeTargetFields.get();
    }

    @Override
    public Class getCollectionClass() {
        return collectionClass;
    }

    @Override
    public CollectionHandler getCollectionHandlerForCollectionClass() {
        return getCollectionHandlerFor(getCollectionClass());
    }

    @Override
    public CollectionHandler getCollectionHandlerFor(Class<?> clazz) {
        return parentConfiguration.getCollectionHandlerFor(clazz);
    }

    @Override
    public Class getTargetClass() {
        return targetClass;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object getParent() {
        return parent.get();
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return beanInitializer.get();
    }

    @Override
    public BeanUnproxy getBeanUnproxy() {
        return parentConfiguration.getBeanUnproxy();
    }

    @Override
    public BeanMatchStore getBeanMatchStore() {
        return parentConfiguration.getBeanMatchStore();
    }

    @Override
    public ClassStore getClassStore() {
        return parentConfiguration.getClassStore();
    }

    @Override
    public List<String> getPackagePrefixes() {
        return parentConfiguration.getPackagePrefixes();
    }

    @Override
    public List<BeanConverter> getBeanConverters() {
        List<BeanConverter> converters = new ArrayList<BeanConverter>();
        converters.addAll(parentConfiguration.getBeanConverters());
        converters.addAll(beanConverters);
        return converters;
    }

    @Override
    public Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> getLogicSecuredChecks() {
        return parentConfiguration.getLogicSecuredChecks();
    }

    @Override
    public List<CollectionHandler> getCollectionHandlers() {
        return parentConfiguration.getCollectionHandlers();
    }

    @Override
    public List<BeanPair> getBeanPairs() {
        List<BeanPair> beanPairs = new ArrayList<BeanPair>();
        beanPairs.addAll(parentConfiguration.getBeanPairs());
        beanPairs.addAll(this.beanPairs);
        return beanPairs;
    }

    @Override
    public Boolean isConverterChoosable() {
        return converterChoosable.get();
    }

    @Override
    public void withoutDefaultConverters() {
        // not supported for override options
    }

    @Override
    public String getStrictSourceSuffix() {
        return strictMappingProperties.getStrictSourceSuffix() == null ?
                parentConfiguration.getStrictSourceSuffix() :
                strictMappingProperties.getStrictSourceSuffix();
    }

    @Override
    public String getStrictTargetSuffix() {
        return strictMappingProperties.getStrictTargetSuffix() == null ?
                parentConfiguration.getStrictTargetSuffix() :
                strictMappingProperties.getStrictTargetSuffix();
    }

    @Override
    public Boolean isApplyStrictMappingConvention() {
        return strictMappingProperties.isApplyStrictMappingConvention() == null ?
                parentConfiguration.isApplyStrictMappingConvention() :
                strictMappingProperties.isApplyStrictMappingConvention();
    }

    @Override
    public StrictMappingProperties getStrictMappingProperties() {
        return new StrictMappingProperties(
                getBeanUnproxy(),
                getStrictSourceSuffix(),
                getStrictTargetSuffix(),
                isApplyStrictMappingConvention()
        );
    }

    @Override
    public Class<?> getPreferredCollectionClass() {
        return preferredCollectionClass;
    }

    @Override
    public CollectionFlusher getCollectionFlusher() {
        return parentConfiguration.getCollectionFlusher();
    }

    @Override
    public BeanCollectionUsage getCollectionUsage() {
        return this.collectionUsage == null ?
                parentConfiguration.getCollectionUsage() :
                this.collectionUsage;
    }

    @Override
    public Boolean isFlushAfterClear() {
        return flushAfterClear.get();
    }

    @Override
    public Boolean isFlushEnabled() {
        return flushEnabled.get();
    }

    @Override
    public Boolean mustFlush() {
        return isFlushEnabled() && isFlushAfterClear();
    }

    @Override
    public RoleSecuredCheck getRoleSecuredCheck() {
        return parentConfiguration.getRoleSecuredCheck();
    }

    @Override
    public Boolean getEnforceSecuredProperties() {
        return this.enforcedSecuredProperties == null ?
                parentConfiguration.getEnforceSecuredProperties() :
                this.enforcedSecuredProperties;
    }

    @Override
    public void addConverter(BeanConverter converter) {
        beanConverters.add(converter);
    }

    @Override
    public void addLogicSecuredCheck(LogicSecuredCheck logicSecuredCheck) {
        // not supported for override options
    }

    @Override
    public void addCollectionHandler(CollectionHandler collectionHandler) {
        // not supported for override options
    }

    @Override
    public void addBeanPairWithStrictSource(Class source, Class target) {
        this.beanPairs.add(new BeanPair(source, target).withStrictSource());
    }

    @Override
    public void addBeanPairWithStrictTarget(Class source, Class target) {
        this.beanPairs.add(new BeanPair(source, target).withStrictTarget());
    }

    @Override
    public void addProxySkipClass(Class<?> clazz) {
        // not supported for override options
    }

    @Override
    public void addPackagePrefix(Class<?> clazz) {
        // not supported for override options
    }

    @Override
    public void addPackagePrefix(String packagePrefix) {
        // not supported for override options
    }

    @Override
    public void addAfterClearFlusher(AfterClearFlusher afterClearFlusher) {
        // not supported for override options
    }

    @Override
    public void setRoleSecuredCheck(RoleSecuredCheck roleSecuredCheck) {
        // not supported for override options
    }

    @Override
    public void setEnforceSecuredProperties(Boolean enforceSecuredProperties) {
        this.enforcedSecuredProperties = enforceSecuredProperties;
    }

    @Override
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer.set(beanInitializer);
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        // not supported for override options
    }

    @Override
    public boolean isAddDefaultConverters() {
        return false;
    }

    @Override
    public void downsizeSource(List<String> includeFields) {
        this.downsizeSourceFields.set(includeFields);
    }

    @Override
    public void downsizeTarget(List<String> includeFields) {
        this.downsizeTargetFields.set(includeFields);
    }

    @Override
    public void setCollectionClass(Class collectionClass) {
        this.collectionClass = collectionClass;
    }

    @Override
    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void setParent(Object parent) {
        this.parent.set(parent);
    }

    @Override
    public void setConverterChoosable(boolean converterChoosable) {
        this.converterChoosable.set(converterChoosable);
    }

    @Override
    public Class determineTargetClass() {
        return getTargetClass() == null ? getTarget().getClass() : getTargetClass();
    }

    @Override
    public void setStrictSourceSuffix(String strictSourceSuffix) {
        this.strictMappingProperties.setStrictSourceSuffix(strictSourceSuffix);
    }

    @Override
    public void setStrictTargetSuffix(String strictTargetSuffix) {
        this.strictMappingProperties.setStrictTargetSuffix(strictTargetSuffix);
    }

    @Override
    public void setApplyStrictMappingConvention(Boolean applyStrictMappingConvention) {
        this.strictMappingProperties.setApplyStrictMappingConvention(applyStrictMappingConvention);
    }

    @Override
    public void setCollectionUsage(BeanCollectionUsage collectionUsage) {
        this.collectionUsage = collectionUsage;
    }

    @Override
    public void setPreferredCollectionClass(Class<?> preferredCollectionClass) {
        this.preferredCollectionClass = preferredCollectionClass;
    }

    @Override
    public void setFlushAfterClear(Boolean flushAfterClear) {
        this.flushAfterClear.set(flushAfterClear);
    }

    @Override
    public void setFlushEnabled(Boolean flushEnabled) {
        this.flushEnabled.set(flushEnabled);
    }

}
