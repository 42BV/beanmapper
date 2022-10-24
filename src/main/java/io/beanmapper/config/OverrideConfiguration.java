package io.beanmapper.config;

import java.util.ArrayList;
import java.util.Collections;
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
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;

public class OverrideConfiguration implements Configuration {

    private final Configuration parentConfiguration;

    private OverrideField<BeanInitializer> beanInitializer;

    private List<BeanConverter> beanConverters = new ArrayList<>();

    private List<BeanPair> beanPairs = new ArrayList<>();

    private OverrideField<List<String>> downsizeSourceFields;

    private OverrideField<List<String>> downsizeTargetFields;

    private Class targetClass;

    private Object target;

    private OverrideField<Object> parent;

    private Class collectionClass;

    private OverrideField<Boolean> converterChoosable;

    private OverrideField<StrictMappingProperties> strictMappingProperties;

    private BeanCollectionUsage collectionUsage = null;

    private Class<?> preferredCollectionClass = null;

    private OverrideField<Boolean> enforcedSecuredProperties;

    private OverrideField<Boolean> useNullValue;

    private OverrideField<FlushAfterClearInstruction> flushAfterClear;

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
        this.useNullValue = new OverrideField<>(configuration::getUseNullValue);
        this.strictMappingProperties = new OverrideField<>(configuration::getStrictMappingProperties);
        this.enforcedSecuredProperties = new OverrideField<>(configuration::getEnforceSecuredProperties);
    }

    @Override
    public List<String> getDownsizeSource() {
        var list = this.downsizeSourceFields.get();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<String> getDownsizeTarget() {
        var list = this.downsizeTargetFields.get();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public Class getCollectionClass() {
        return collectionClass;
    }

    @Override
    public void setCollectionClass(Class collectionClass) {
        this.collectionClass = collectionClass;
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
    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object getParent() {
        return parent.get();
    }

    @Override
    public void setParent(Object parent) {
        this.parent.set(parent);
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return beanInitializer.get();
    }

    @Override
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer.set(beanInitializer);
    }

    @Override
    public BeanUnproxy getBeanUnproxy() {
        return parentConfiguration.getBeanUnproxy();
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add BeanUnproxy on the override configuration, works only for core configurations");
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
        var list = parentConfiguration.getPackagePrefixes();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<BeanConverter> getBeanConverters() {
        List<BeanConverter> converters = new ArrayList<>();
        converters.addAll(parentConfiguration.getBeanConverters());
        converters.addAll(beanConverters);
        return converters;
    }

    @Override
    public Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> getLogicSecuredChecks() {
        var map = this.parentConfiguration.getLogicSecuredChecks();
        return map != null ? map : Collections.emptyMap();
    }

    @Override
    public List<CollectionHandler> getCollectionHandlers() {
        var list = this.parentConfiguration.getCollectionHandlers();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<BeanPair> getBeanPairs() {
        List<BeanPair> beanPairs = new ArrayList<>();
        beanPairs.addAll(parentConfiguration.getBeanPairs());
        beanPairs.addAll(this.beanPairs);
        return beanPairs;
    }

    @Override
    public boolean isConverterChoosable() {
        return converterChoosable.get();
    }

    @Override
    public void setConverterChoosable(boolean converterChoosable) {
        this.converterChoosable.set(converterChoosable);
    }

    @Override
    public void withoutDefaultConverters() {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to modify default converters on the override configuration, works only for core configurations");
    }

    @Override
    public String getStrictSourceSuffix() {
        return this.strictMappingProperties.get().getStrictSourceSuffix();
    }

    @Override
    public void setStrictSourceSuffix(String strictSourceSuffix) {
        this.strictMappingProperties.get().setStrictSourceSuffix(strictSourceSuffix);
    }

    @Override
    public String getStrictTargetSuffix() {
        return strictMappingProperties.get().getStrictTargetSuffix();
    }

    @Override
    public void setStrictTargetSuffix(String strictTargetSuffix) {
        this.strictMappingProperties.get().setStrictTargetSuffix(strictTargetSuffix);
    }

    @Override
    public boolean isApplyStrictMappingConvention() {
        return strictMappingProperties.get().isApplyStrictMappingConvention();
    }

    @Override
    public void setApplyStrictMappingConvention(boolean applyStrictMappingConvention) {
        this.strictMappingProperties.get().setApplyStrictMappingConvention(applyStrictMappingConvention);
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
    public void setPreferredCollectionClass(Class<?> preferredCollectionClass) {
        this.preferredCollectionClass = preferredCollectionClass;
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
    public void setCollectionUsage(BeanCollectionUsage collectionUsage) {
        this.collectionUsage = collectionUsage;
    }

    @Override
    public FlushAfterClearInstruction isFlushAfterClear() {
        return flushAfterClear.get();
    }

    @Override
    public boolean isFlushEnabled() {
        return this.flushEnabled.get();
    }

    @Override
    public void setFlushEnabled(boolean flushEnabled) {
        this.flushEnabled.set(flushEnabled);
    }

    @Override
    public boolean mustFlush() {
        return isFlushEnabled() && isFlushAfterClear() == FlushAfterClearInstruction.FLUSH_ENABLED;
    }

    @Override
    public boolean getUseNullValue() {
        return useNullValue.get();
    }

    @Override
    public void setUseNullValue(boolean useNullValue) {
        this.useNullValue.set(useNullValue);
    }

    @Override
    public RoleSecuredCheck getRoleSecuredCheck() {
        return parentConfiguration.getRoleSecuredCheck();
    }

    @Override
    public void setRoleSecuredCheck(RoleSecuredCheck roleSecuredCheck) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a RoleSecuredCheck on the override configuration, works only for core configurations");
    }

    @Override
    public boolean getEnforceSecuredProperties() {
        return this.enforcedSecuredProperties.get();
    }

    @Override
    public void setEnforceSecuredProperties(boolean enforceSecuredProperties) {
        this.enforcedSecuredProperties.set(enforceSecuredProperties);
    }

    @Override
    public void addConverter(BeanConverter converter) {
        beanConverters.add(converter);
    }

    @Override
    public void addLogicSecuredCheck(LogicSecuredCheck logicSecuredCheck) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a LogicSecuredCheck on the override configuration, works only for core configurations");
    }

    @Override
    public void addCollectionHandler(CollectionHandler collectionHandler) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a CollectionHandler on the override configuration, works only for core configurations");
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
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a ProxySkip-class on the override configuration, works only for core configurations");
    }

    @Override
    public void addPackagePrefix(Class<?> clazz) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a package prefix on the override configuration, works only for core configurations");
    }

    @Override
    public void addPackagePrefix(String packagePrefix) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a package prefix on the override configuration, works only for core configurations");
    }

    @Override
    public void addAfterClearFlusher(AfterClearFlusher afterClearFlusher) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a AfterClearFlusher on the override configuration, works only for core configurations");
    }

    @Override
    public boolean isAddDefaultConverters() {
        return false;
    }

    @Override
    public void downsizeSource(List<String> includeFields) {
        this.downsizeSourceFields.set(includeFields != null ? includeFields : Collections.emptyList());
    }

    @Override
    public void downsizeTarget(List<String> includeFields) {
        this.downsizeTargetFields.set(includeFields != null ? includeFields : Collections.emptyList());
    }

    @Override
    public Class determineTargetClass() {
        return getTargetClass() == null ? getTarget().getClass() : getTargetClass();
    }

    @Override
    public void setFlushAfterClear(FlushAfterClearInstruction flushAfterClear) {
        this.flushAfterClear.set(flushAfterClear);
    }

}
