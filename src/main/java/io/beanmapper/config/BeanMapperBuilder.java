package io.beanmapper.config;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.converter.BeanMapperAware;
import io.beanmapper.core.converter.collections.CollectionListConverter;
import io.beanmapper.core.converter.collections.CollectionMapConverter;
import io.beanmapper.core.converter.collections.CollectionSetConverter;
import io.beanmapper.core.converter.impl.*;
import io.beanmapper.core.rule.MappableFields;
import io.beanmapper.core.unproxy.BeanUnproxy;

import java.util.ArrayList;
import java.util.List;

public class BeanMapperBuilder {

    private static final boolean REUSE_CONFIGURATION = true;
    private static final boolean WRAP_IN_NEW_CONFIGURATION = false;

    private final Configuration configuration;

    List<BeanConverter> customBeanConverters = new ArrayList<BeanConverter>();

    public BeanMapperBuilder() {
        this.configuration = new CoreConfiguration();
    }

    protected BeanMapperBuilder(Configuration configuration, boolean reuseConfiguration) {
        this.configuration = reuseConfiguration && configuration.canReuse() ?
                configuration :
                new OverrideConfiguration(configuration);
    }

    public static BeanMapperBuilder config(Configuration configuration) {
        return new BeanMapperBuilder(configuration, REUSE_CONFIGURATION);
    }

    public static BeanMapperBuilder wrapConfig(Configuration configuration) {
        return new BeanMapperBuilder(configuration, WRAP_IN_NEW_CONFIGURATION);
    }

    public BeanMapperBuilder withoutDefaultConverters() {
        this.configuration.withoutDefaultConverters();
        return this;
    }

    public BeanMapperBuilder addConverter(BeanConverter converter) {
        this.customBeanConverters.add(converter);
        return this;
    }

    public BeanMapperBuilder addProxySkipClass(Class<?> clazz) {
        this.configuration.addProxySkipClass(clazz);
        return this;
    }

    public BeanMapperBuilder addPackagePrefix(Class<?> clazz) {
        this.configuration.addPackagePrefix(clazz);
        return this;
    }

    public BeanMapperBuilder addPackagePrefix(String packagePrefix) {
        this.configuration.addPackagePrefix(packagePrefix);
        return this;
    }

    public BeanMapperBuilder setBeanInitializer(BeanInitializer beanInitializer) {
        this.configuration.setBeanInitializer(beanInitializer);
        return this;
    }

    public BeanMapperBuilder setBeanUnproxy(BeanUnproxy beanUnproxy) {
        this.configuration.setBeanUnproxy(beanUnproxy);
        return this;
    }

    public BeanMapperBuilder setTargetClass(Class targetClass) {
        this.configuration.setTargetClass(targetClass);
        return this;
    }

    public BeanMapperBuilder setTarget(Object target) {
        this.configuration.setTarget(target);
        return this;
    }

    public BeanMapperBuilder setMappableFields(MappableFields mappableFields) {
        this.configuration.setMappableFields(mappableFields);
        return this;
    }

    public BeanMapperBuilder setConverterChoosable(boolean converterChoosable) {
        this.configuration.setConverterChoosable(converterChoosable);
        return this;
    }

    public BeanMapperBuilder setCollectionClass(Class collectionClass) {
        this.configuration.setCollectionClass(collectionClass);
        return this;
    }

    public BeanMapperBuilder unsetCollectionClass() {
        this.configuration.unsetCollectionClass();
        return this;
    }

    public BeanMapperBuilder unsetTargetClass() {
        this.configuration.unsetTargetClass();
        return this;
    }

    public BeanMapperBuilder unsetTarget() {
        this.configuration.unsetTarget();
        return this;
    }

    public BeanMapper build() {
        BeanMapper beanMapper = new BeanMapper(configuration);
        // Custom bean converters must be registered before default ones
        addCustomConverters(beanMapper);
        if (configuration.isAddDefaultConverters()) {
            addDefaultConverters(beanMapper);
        }
        return beanMapper;
    }

    private void addCustomConverters(BeanMapper beanMapper) {
        for (BeanConverter customBeanConverter : customBeanConverters) {
            addConverter(beanMapper, customBeanConverter);
        }
    }

    private void addDefaultConverters(BeanMapper beanMapper) {
        addConverter(beanMapper, new PrimitiveConverter());
        addConverter(beanMapper, new StringToBooleanConverter());
        addConverter(beanMapper, new StringToIntegerConverter());
        addConverter(beanMapper, new StringToLongConverter());
        addConverter(beanMapper, new StringToBigDecimalConverter());
        addConverter(beanMapper, new StringToEnumConverter());
        addConverter(beanMapper, new NumberToNumberConverter());
        addConverter(beanMapper, new ObjectToStringConverter());

        addConverter(beanMapper, new CollectionListConverter());
        addConverter(beanMapper, new CollectionSetConverter());
        addConverter(beanMapper, new CollectionMapConverter());
    }

    private void addConverter(BeanMapper beanMapper, BeanConverter customBeanConverter) {
        if (customBeanConverter instanceof BeanMapperAware) {
            ((BeanMapperAware) customBeanConverter).setBeanMapper(beanMapper);
        }
        configuration.addConverter(customBeanConverter);
    }

}
