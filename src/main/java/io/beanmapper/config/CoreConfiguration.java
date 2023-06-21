package io.beanmapper.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.annotations.LogicSecuredCheck;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.dynclass.ClassStore;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.utils.DefaultValues;
import io.beanmapper.utils.Trinary;

public class CoreConfiguration implements Configuration {

    /**
     * Initializes the beans.
     */
    private BeanInitializer beanInitializer = new DefaultBeanInitializer();

    /**
     * Removes any potential proxies of beans.
     */
    private final SkippingBeanUnproxy beanUnproxy = new SkippingBeanUnproxy(new DefaultBeanUnproxy());

    /**
     * Contains all the handlers for collections. Also used within BeanMatchStore, so must be declared
     * here.
     */
    private final CollectionHandlerStore collectionHandlerStore = new CollectionHandlerStore();

    /**
     * Contains a store of matches for source and target class pairs. A pair is created only
     * once and reused every time thereafter.
     */
    private final BeanMatchStore beanMatchStore = new BeanMatchStore(collectionHandlerStore, beanUnproxy);

    /**
     * Contains a store of classes that are generated by using the MapToDynamicClassStrategy.
     * Every generated class is a downsized source class or a downsized target class.
     */
    private final ClassStore classStore = new ClassStore();

    /**
     * The list of packages (and subpackages) containing classes which are eligible for mapping.
     */
    private final List<String> packagePrefixes = new ArrayList<>();

    /**
     * The list of converters that should be checked for conversions.
     */
    private final List<BeanConverter> beanConverters = new ArrayList<>();

    /**
     * The list of LogicSecuredCheck instances that verify whether access to a property is allowed.
     */
    private final Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> logicSecuredChecks = new HashMap<>();

    /**
     * The list of converters that should be checked for conversions.
     */
    private final List<BeanPair> beanPairs = new ArrayList<>();

    /**
     * The value that decides whether a converter may be chosen, or direct mapping has to take place
     */
    private boolean converterChoosable;

    private boolean addDefaultConverters = true;

    private final StrictMappingProperties strictMappingProperties = StrictMappingProperties.defaultConfig();

    private final CollectionFlusher collectionFlusher = new CollectionFlusher();

    private boolean flushEnabled;

    /**
     * The RoleSecuredCheck is responsible for checking if a Principal may access
     * a field or method annotated with @BeanRoleSecured.
     */
    private RoleSecuredCheck roleSecuredCheck;

    /**
     * Property that determines if secured properties must be handled. If this is set to true
     * and the RoleSecuredCheck has not been set, an exception will be thrown.
     */
    private boolean enforceSecuredProperties = true;

    /**
     * Property that determines if null values for the source will be used. Normal behaviour
     * is to skip the mapping operation if a source value is null.
     */
    private boolean useNullValue;

    private final Map<Class<?>, Object> customDefaultValueMap = new HashMap<>();

    @Override
    public Collection<String> getDownsizeTarget() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getDownsizeSource() {
        return Collections.emptyList();
    }

    @Override
    public Class getTargetClass() {
        return null;
    }

    @Override
    public void setTargetClass(Class targetClass) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set target class on the Core configuration, works only for override configurations");
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public void setTarget(Object target) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a target instance on the Core configuration, works only for override configurations");
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public void setParent(Object parent) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a parent instance on the Core configuration, works only for override configurations");
    }

    @Override
    public Class getCollectionClass() {
        return null;
    }

    @Override
    public void setCollectionClass(Class collectionClass) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a target instance on the Core configuration, works only for override configurations");
    }

    @Override
    public CollectionHandler getCollectionHandlerForCollectionClass() {
        return null;
    }

    @Override
    public CollectionHandler getCollectionHandlerFor(Class<?> clazz) {
        return collectionHandlerStore.getCollectionHandlerFor(clazz, getBeanUnproxy());
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return this.beanInitializer;
    }

    @Override
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer = beanInitializer;
    }

    @Override
    public BeanUnproxy getBeanUnproxy() {
        return this.beanUnproxy;
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        this.beanUnproxy.setDelegate(beanUnproxy);
    }

    @Override
    public BeanMatchStore getBeanMatchStore() {
        return this.beanMatchStore;
    }

    @Override
    public ClassStore getClassStore() {
        return this.classStore;
    }

    @Override
    public List<String> getPackagePrefixes() {
        return this.packagePrefixes;
    }

    @Override
    public List<BeanConverter> getBeanConverters() {
        return this.beanConverters;
    }

    @Override
    public Map<Class<? extends LogicSecuredCheck>, LogicSecuredCheck> getLogicSecuredChecks() {
        return this.logicSecuredChecks;
    }

    @Override
    public List<CollectionHandler> getCollectionHandlers() {
        return this.collectionHandlerStore.getCollectionHandlers();
    }

    @Override
    public List<BeanPair> getBeanPairs() {
        return this.beanPairs;
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
        this.addDefaultConverters = false;
    }

    @Override
    public String getStrictSourceSuffix() {
        return strictMappingProperties.getStrictSourceSuffix();
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
        this.strictMappingProperties.setBeanUnproxy(beanUnproxy);
        return this.strictMappingProperties;
    }

    @Override
    public BeanCollectionUsage getCollectionUsage() {
        return BeanCollectionUsage.CLEAR;
    }

    @Override
    public void setCollectionUsage(BeanCollectionUsage collectionUsage) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set collection usage on the core configuration");
    }

    @Override
    public Class<?> getPreferredCollectionClass() {
        return null;
    }

    @Override
    public void setPreferredCollectionClass(Class<?> preferredCollectionClass) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set preferred collection class on the core configuration");
    }

    @Override
    public CollectionFlusher getCollectionFlusher() {
        return this.collectionFlusher;
    }

    @Override
    public Trinary isFlushAfterClear() {
        return Trinary.DISABLED;
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
        return false;
    }

    @Override
    public boolean getUseNullValue() {
        return this.useNullValue;
    }

    @Override
    public void setUseNullValue(boolean useNullValue) {
        this.useNullValue = useNullValue;
    }

    @Override
    public RoleSecuredCheck getRoleSecuredCheck() {
        return this.roleSecuredCheck;
    }

    @Override
    public void setRoleSecuredCheck(RoleSecuredCheck roleSecuredCheck) {
        this.roleSecuredCheck = roleSecuredCheck;
    }

    @Override
    public boolean getEnforceSecuredProperties() {
        return enforceSecuredProperties;
    }

    @Override
    public void setEnforceSecuredProperties(boolean enforceSecuredProperties) {
        this.enforceSecuredProperties = enforceSecuredProperties;
    }

    @Override
    public void addConverter(BeanConverter converter) {
        this.beanConverters.add(converter);
    }

    @Override
    public void addLogicSecuredCheck(LogicSecuredCheck logicSecuredCheck) {
        this.logicSecuredChecks.put(logicSecuredCheck.getClass(), logicSecuredCheck);
    }

    @Override
    public void addCollectionHandler(CollectionHandler collectionHandler) {
        this.collectionHandlerStore.add(collectionHandler);
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
        this.beanUnproxy.skip(clazz);
    }

    @Override
    public void addPackagePrefix(Class<?> clazz) {
        if (clazz != null) {
            addPackagePrefix(clazz.getPackage().getName());
        }
    }

    @Override
    public void addPackagePrefix(String packagePrefix) {
        this.packagePrefixes.add(packagePrefix);
    }

    @Override
    public void addAfterClearFlusher(AfterClearFlusher afterClearFlusher) {
        this.collectionFlusher.addAfterClearFlusher(afterClearFlusher);
    }

    public boolean isAddDefaultConverters() {
        return this.addDefaultConverters;
    }

    @Override
    public void downsizeSource(Collection<String> includeFields) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a include fields on the Core configuration, works only for override configurations");
    }

    @Override
    public void downsizeTarget(Collection<String> includeFields) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a include fields on the Core configuration, works only for override configurations");
    }

    @Override
    public Class determineTargetClass() {
        return null;
    }

    @Override
    public void setFlushAfterClear(Trinary flushAfterClear) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set flush after clear on the core configuration");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, V> V getDefaultValueForClass(Class<T> targetClass) {
        return this.customDefaultValueMap.containsKey(targetClass)
                ? (V) this.customDefaultValueMap.get(targetClass)
                : DefaultValues.defaultValueFor(targetClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, V> void addCustomDefaultValueForClass(Class<T> targetClass, V value) {
        this.customDefaultValueMap.put(targetClass, value);
    }
}
