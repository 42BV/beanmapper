package io.beanmapper.config;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.converter.collections.CollectionListConverter;
import io.beanmapper.core.converter.collections.CollectionMapConverter;
import io.beanmapper.core.converter.collections.CollectionSetConverter;
import io.beanmapper.core.converter.impl.*;
import io.beanmapper.core.unproxy.BeanUnproxy;

import java.util.ArrayList;
import java.util.List;

public class BeanMapperBuilder {

    private static final boolean REUSE_CONFIGURATION = true;
    private static final boolean WRAP_IN_NEW_CONFIGURATION = false;

    private final Configuration configuration;

    private List<BeanConverter> customBeanConverters = new ArrayList<BeanConverter>();

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

    public BeanMapperBuilder downsizeSource(List<String> includeFields) {
        this.configuration.downsizeSource(includeFields);
        return this;
    }

    public BeanMapperBuilder downsizeTarget(List<String> includeFields) {
        this.configuration.downsizeTarget(includeFields);
        return this;
    }

    public BeanMapperBuilder setCollectionClass(Class collectionClass) {
        this.configuration.setCollectionClass(collectionClass);
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

    public BeanMapperBuilder setParent(Object parent) {
        this.configuration.setParent(parent);
        return this;
    }

    public BeanMapperBuilder setConverterChoosable(boolean converterChoosable) {
        this.configuration.setConverterChoosable(converterChoosable);
        return this;
    }

    public BeanMapper build() {
        BeanMapper beanMapper = new BeanMapper(configuration);
        // Custom bean converters must be registered before default ones
        addCustomConverters();
        if (configuration.isAddDefaultConverters()) {
            addDefaultConverters();
        }
        return beanMapper;
    }

    private void addCustomConverters() {
        for (BeanConverter customBeanConverter : customBeanConverters) {
            attachConverter(customBeanConverter);
        }
    }

    private void addDefaultConverters() {
        attachConverter(new PrimitiveConverter());
        attachConverter(new StringToBooleanConverter());
        attachConverter(new StringToIntegerConverter());
        attachConverter(new StringToLongConverter());
        attachConverter(new StringToBigDecimalConverter());
        attachConverter(new StringToEnumConverter());
        attachConverter(new NumberToNumberConverter());
        attachConverter(new ObjectToStringConverter());

        attachConverter(new CollectionListConverter());
        attachConverter(new CollectionSetConverter());
        attachConverter(new CollectionMapConverter());
    }

    private void attachConverter(BeanConverter customBeanConverter) {
        configuration.addConverter(customBeanConverter);
    }

}
