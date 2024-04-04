package io.beanmapper.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.annotations.LogicSecuredCheck;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.converter.BeanConverterStore;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.dynclass.ClassStore;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.execution_plan.ExecutionPlan;
import io.beanmapper.utils.DefaultValues;
import io.beanmapper.utils.Trinary;

public class OverrideConfiguration implements Configuration {

    private final Configuration parentConfiguration;

    private BeanInitializer beanInitializer;

    private final List<BeanConverter> beanConverters;

    private final List<BeanPair> beanPairs;

    private final List<String> downsizeSourceFields;

    private final List<String> downsizeTargetFields;

    private final Map<Class<?>, Object> customDefaultValuesMap;

    private final CollectionHandlerStore collectionHandlerStore;

    private final BeanUnproxy beanUnproxy;

    private final BeanMatchStore beanMatchStore;

    private final BeanConverterStore beanConverterStore;

    private final List<String> packagePrefixes;

    private final Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> logicSecuredChecks;

    private final List<CollectionHandler> collectionHandlers;

    private final CollectionFlusher collectionFlusher;

    private final RoleSecuredCheck roleSecuredCheck;

    private final StrictMappingProperties strictMappingProperties;

    private Class<?> targetClass;

    private Object target;

    private Object parent;

    private Class collectionClass;

    private boolean converterChoosable;

    private BeanCollectionUsage collectionUsage;

    private Class<?> preferredCollectionClass;

    private boolean enforcedSecuredProperties;

    private boolean useNullValue;

    private Trinary flushAfterClear;

    private boolean flushEnabled;

    public OverrideConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new ParentConfigurationPossiblyNullException("Developer error: the parent configuration may not be null");
        }

        boolean parentIsOverride = configuration instanceof OverrideConfiguration;

        this.parentConfiguration = configuration;
        this.downsizeSourceFields = parentIsOverride ? new ArrayList<>(parentConfiguration.getDownsizeSource()) : new ArrayList<>();
        this.downsizeTargetFields = parentIsOverride ? new ArrayList<>(configuration.getDownsizeTarget()) : new ArrayList<>();
        this.beanInitializer = configuration.getBeanInitializer();
        this.parent = configuration.getParent();
        this.converterChoosable = configuration.isConverterChoosable();
        this.flushAfterClear = configuration.isFlushAfterClear();
        this.flushEnabled = configuration.isFlushEnabled();
        this.useNullValue = configuration.getUseNullValue();
        this.strictMappingProperties = configuration.getStrictMappingProperties();
        this.enforcedSecuredProperties = configuration.getEnforceSecuredProperties();
        this.customDefaultValuesMap = new HashMap<>(parentConfiguration.getCustomDefaultValuesMap());
        this.collectionHandlerStore = parentConfiguration.getCollectionHandlerStore();
        this.beanUnproxy = parentConfiguration.getBeanUnproxy();
        this.beanMatchStore = parentConfiguration.getBeanMatchStore();
        this.beanConverterStore = parentIsOverride
                ? parentConfiguration.getBeanConverterStore()
                : new BeanConverterStore(parentConfiguration.getBeanConverterStore());
        this.packagePrefixes = parentConfiguration.getPackagePrefixes();
        this.beanConverters = new ArrayList<>(parentConfiguration.getBeanConverters());
        this.beanPairs = parentConfiguration.getBeanPairs();
        this.collectionUsage = parentConfiguration.getCollectionUsage();
        this.logicSecuredChecks = parentConfiguration.getLogicSecuredChecks();
        this.collectionHandlers = parentConfiguration.getCollectionHandlers();
        this.collectionFlusher = parentConfiguration.getCollectionFlusher();
        this.roleSecuredCheck = parentConfiguration.getRoleSecuredCheck();
        this.preferredCollectionClass = parentConfiguration.getPreferredCollectionClass();
    }

    @Override
    public List<String> getDownsizeSource() {
        return downsizeSourceFields;
    }

    @Override
    public List<String> getDownsizeTarget() {
        return downsizeTargetFields;
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
        return collectionHandlerStore.getCollectionHandlerFor(collectionClass, beanUnproxy);
    }

    @Override
    public CollectionHandler getCollectionHandlerFor(Class<?> clazz) {
        return getCollectionHandlerFor(clazz);
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
        return beanUnproxy;
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add BeanUnproxy on the override configuration, works only for core configurations");
    }

    @Override
    public BeanMatchStore getBeanMatchStore() {
        return beanMatchStore;
    }

    @Override
    public ClassStore getClassStore() {
        return parentConfiguration.getClassStore();
    }

    @Override
    public List<String> getPackagePrefixes() {
        return packagePrefixes;
    }

    @Override
    public List<BeanConverter> getBeanConverters() {
        return beanConverters;
    }

    @Override
    public Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> getLogicSecuredChecks() {
        return logicSecuredChecks;
    }

    @Override
    public List<CollectionHandler> getCollectionHandlers() {
        return collectionHandlers;
    }

    @Override
    public List<BeanPair> getBeanPairs() {
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
        return strictMappingProperties.getStrictSourceSuffix();
    }

    @Override
    public void setStrictSourceSuffix(String strictSourceSuffix) {
        strictMappingProperties.setStrictSourceSuffix(strictSourceSuffix);
    }

    @Override
    public String getStrictTargetSuffix() {
        return strictMappingProperties.getStrictTargetSuffix();
    }

    @Override
    public void setStrictTargetSuffix(String strictTargetSuffix) {
        strictMappingProperties.setStrictTargetSuffix(strictTargetSuffix);
    }

    @Override
    public boolean isApplyStrictMappingConvention() {
        return strictMappingProperties.isApplyStrictMappingConvention();
    }

    @Override
    public void setApplyStrictMappingConvention(boolean applyStrictMappingConvention) {
        strictMappingProperties.setApplyStrictMappingConvention(applyStrictMappingConvention);
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
        return collectionFlusher;
    }

    @Override
    public BeanCollectionUsage getCollectionUsage() {
        return collectionUsage;
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
        return flushEnabled;
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
        return roleSecuredCheck;
    }

    @Override
    public void setRoleSecuredCheck(RoleSecuredCheck roleSecuredCheck) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to add a RoleSecuredCheck on the override configuration, works only for core configurations");
    }

    @Override
    public boolean getEnforceSecuredProperties() {
        return enforcedSecuredProperties;
    }

    @Override
    public void setEnforceSecuredProperties(boolean enforceSecuredProperties) {
        enforcedSecuredProperties = enforceSecuredProperties;
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
        beanPairs.add(new BeanPair(source, target).withStrictSource());
    }

    @Override
    public void addBeanPairWithStrictTarget(Class source, Class target) {
        beanPairs.add(new BeanPair(source, target).withStrictTarget());
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
        downsizeSourceFields.clear();
        downsizeSourceFields.addAll(includeFields);
    }

    @Override
    public void downsizeTarget(List<String> includeFields) {
        downsizeTargetFields.clear();
        downsizeTargetFields.addAll(includeFields);
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
        customDefaultValuesMap.put(target, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, V> V getDefaultValueForClass(Class<T> targetClass) {
        V value = (V) customDefaultValuesMap.get(targetClass);
        return value != null ? value : DefaultValues.defaultValueFor(targetClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionHandlerStore getCollectionHandlerStore() {
        return collectionHandlerStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Class<?>, Object> getCustomDefaultValuesMap() {
        return customDefaultValuesMap;
    }

    @Override
    public BeanConverterStore getBeanConverterStore() {
        return beanConverterStore;
    }

    @Override
    public <S, T> BeanConverter getBeanConverter(Class<S> source, Class<T> target) {
        return beanConverterStore.get(source, target);
    }

    @Override
    public <S, T> ExecutionPlan<S, T> getExecutionPlan() {
        return null;
    }

    @Override
    public <S, T> ExecutionPlan<S, T> setExecutionPlan(ExecutionPlan<S, T> executionPlan) {
        return null;
    }
}
