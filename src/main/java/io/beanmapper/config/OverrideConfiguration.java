package io.beanmapper.config;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.dynclass.ClassStore;

public class OverrideConfiguration implements Configuration {

    private final Configuration parentConfiguration;

    private BeanInitializer beanInitializer;

    private SkippingBeanUnproxy beanUnproxy;

    private List<String> packagePrefixes;

    private List<BeanConverter> beanConverters = new ArrayList<BeanConverter>();
    
    private List<BeanPair> beanPairs = new ArrayList<BeanPair>();

    private List<String> downsizeSourceFields;

    private List<String> downsizeTargetFields;

    private List<CollectionHandler> collectionsHandlers = new ArrayList<>();

    private Class targetClass;

    private Object target;

    private Object parent;

    private Class collectionClass;

    private Boolean converterChoosable = null;

    private StrictMappingProperties strictMappingProperties = StrictMappingProperties.emptyConfig();

    private BeanCollectionUsage collectionUsage = null;

    private Class<?> preferredCollectionClass = null;

    private Boolean flushAfterClear = null;

    private Boolean flushEnabled = null;

    public OverrideConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new ParentConfigurationPossiblyNullException("Developer error: the parent configuration may not be null");
        }
        this.parentConfiguration = configuration;
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
        return parent == null ? parentConfiguration.getParent() : parent;
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return beanInitializer == null ? parentConfiguration.getBeanInitializer() : beanInitializer;
    }

    @Override
    public SkippingBeanUnproxy getBeanUnproxy() {
        return beanUnproxy == null ? parentConfiguration.getBeanUnproxy() : beanUnproxy;
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
        return packagePrefixes == null ? parentConfiguration.getPackagePrefixes() : packagePrefixes;
    }

    @Override
    public List<BeanConverter> getBeanConverters() {
        List<BeanConverter> converters = new ArrayList<BeanConverter>();
        converters.addAll(parentConfiguration.getBeanConverters());
        converters.addAll(beanConverters);
        return converters;
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
        return converterChoosable == null ? parentConfiguration.isConverterChoosable() : converterChoosable;
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
    public BeanCollectionUsage getCollectionUsage() {
        return this.collectionUsage == null ?
                parentConfiguration.getCollectionUsage() :
                this.collectionUsage;
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
    public boolean isFlushAfterClear() {
        return this.flushAfterClear == null ?
                parentConfiguration.isFlushAfterClear() :
                this.flushAfterClear;
    }

    @Override
    public boolean isFlushEnabled() {
        return this.flushEnabled == null ?
                parentConfiguration.isFlushEnabled() :
                this.flushEnabled;
    }

    @Override
    public boolean mustFlush() {
        return isFlushEnabled() && isFlushAfterClear();
    }

    @Override
    public void addConverter(BeanConverter converter) {
        beanConverters.add(converter);
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
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer = beanInitializer;
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        this.beanUnproxy = new SkippingBeanUnproxy(beanUnproxy);
    }

    @Override
    public boolean isAddDefaultConverters() {
        return false;
    }

    @Override
    public void downsizeSource(List<String> includeFields) {
        this.downsizeSourceFields = includeFields;
    }

    @Override
    public void downsizeTarget(List<String> includeFields) {
        this.downsizeTargetFields = includeFields;
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
    public void setParent(Object parent) { this.parent = parent; }

    @Override
    public void setConverterChoosable(boolean converterChoosable) {
        this.converterChoosable = converterChoosable;
    }

    @Override
    public boolean canReuse() {
        return true;
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
    public void setFlushAfterClear(boolean flushAfterClear) {
        this.flushAfterClear = flushAfterClear;
    }

    @Override
    public void setFlushEnabled(boolean flushEnabled) {
        this.flushEnabled = flushEnabled;
    }

}
