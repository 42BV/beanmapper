package io.beanmapper.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import io.beanmapper.utils.Trinary;

public class OverrideConfiguration implements Configuration {

    private final Configuration parentConfiguration;

    private BeanInitializer beanInitializer;

    private final List<BeanConverter> beanConverters = new ArrayList<>();

    private final List<BeanPair> beanPairs = new ArrayList<>();

    private final List<String> downsizeSourceFields;

    private final List<String> downsizeTargetFields;

    private Class<?> targetClass;

    private Object target;

    private Object parent;

    private Class collectionClass;

    private boolean converterChoosable;

    private final StrictMappingProperties strictMappingProperties;

    private BeanCollectionUsage collectionUsage = null;

    private Class<?> preferredCollectionClass = null;

    private boolean enforcedSecuredProperties;

    private boolean useNullValue;

    private Trinary flushAfterClear;

    private boolean flushEnabled;

    private final Map<Class<?>, Object> customDefaultValues;

    public OverrideConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new ParentConfigurationPossiblyNullException("Developer error: the parent configuration may not be null");
        }
        this.parentConfiguration = configuration;
        this.downsizeSourceFields = new ArrayList<>(configuration.getDownsizeSource());
        this.downsizeTargetFields = new ArrayList<>(configuration.getDownsizeTarget());
        this.beanInitializer = configuration.getBeanInitializer();
        this.parent = configuration.getParent();
        this.converterChoosable = configuration.isConverterChoosable();
        this.flushAfterClear = configuration.isFlushAfterClear();
        this.flushEnabled = configuration.isFlushEnabled();
        this.useNullValue = configuration.getUseNullValue();
        this.strictMappingProperties = configuration.getStrictMappingProperties();
        this.enforcedSecuredProperties = configuration.getEnforceSecuredProperties();
        this.customDefaultValues = new HashMap<>();
    }

    @Override
    public List<String> getDownsizeSource() {
        return this.downsizeSourceFields;
    }

    @Override
    public List<String> getDownsizeTarget() {
        return this.downsizeTargetFields;
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
        return this.targetClass;
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
        return parent;
    }

    @Override
    public void setParent(Object parent) {
        this.parent = parent;
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return beanInitializer;
    }

    @Override
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer = beanInitializer;
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
        return converterChoosable;
    }

    @Override
    public void setConverterChoosable(boolean converterChoosable) {
        this.converterChoosable = converterChoosable;
    }

    @Override
    public void withoutDefaultConverters() {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to modify default converters on the override configuration, works only for core configurations");
    }

    @Override
    public String getStrictSourceSuffix() {
        return this.strictMappingProperties.getStrictSourceSuffix();
    }

    @Override
    public void setStrictSourceSuffix(String strictSourceSuffix) {
        this.strictMappingProperties.setStrictSourceSuffix(strictSourceSuffix);
    }

    @Override
    public String getStrictTargetSuffix() {
        return strictMappingProperties.getStrictTargetSuffix();
    }

    @Override
    public void setStrictTargetSuffix(String strictTargetSuffix) {
        this.strictMappingProperties.setStrictTargetSuffix(strictTargetSuffix);
    }

    @Override
    public boolean isApplyStrictMappingConvention() {
        return strictMappingProperties.isApplyStrictMappingConvention();
    }

    @Override
    public void setApplyStrictMappingConvention(boolean applyStrictMappingConvention) {
        this.strictMappingProperties.setApplyStrictMappingConvention(applyStrictMappingConvention);
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
    public Trinary isFlushAfterClear() {
        return flushAfterClear;
    }

    @Override
    public boolean isFlushEnabled() {
        return this.flushEnabled;
    }

    @Override
    public void setFlushEnabled(boolean flushEnabled) {
        this.flushEnabled = flushEnabled;
    }

    @Override
    public boolean mustFlush() {
        return isFlushEnabled() && isFlushAfterClear() == Trinary.ENABLED;
    }

    @Override
    public boolean getUseNullValue() {
        return useNullValue;
    }

    @Override
    public void setUseNullValue(boolean useNullValue) {
        this.useNullValue = useNullValue;
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
        return this.enforcedSecuredProperties;
    }

    @Override
    public void setEnforceSecuredProperties(boolean enforceSecuredProperties) {
        this.enforcedSecuredProperties = enforceSecuredProperties;
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
        this.downsizeSourceFields.clear();
        this.downsizeSourceFields.addAll(includeFields);
    }

    @Override
    public void downsizeTarget(List<String> includeFields) {
        this.downsizeTargetFields.clear();
        this.downsizeTargetFields.addAll(includeFields);
    }

    @Override
    public Class determineTargetClass() {
        return getTargetClass() == null ? getTarget().getClass() : getTargetClass();
    }

    @Override
    public void setFlushAfterClear(Trinary flushAfterClear) {
        this.flushAfterClear = flushAfterClear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, V> void addCustomDefaultValueForClass(Class<T> target, V value) {
        this.customDefaultValues.put(target, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, V> V getDefaultValueForClass(Class<T> targetClass) {
        return this.customDefaultValues.containsKey(targetClass)
                ? (V) this.customDefaultValues.get(targetClass)
                : this.parentConfiguration.getDefaultValueForClass(targetClass);
    }

}
