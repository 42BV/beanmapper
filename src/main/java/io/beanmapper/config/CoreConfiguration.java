package io.beanmapper.config;

import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.dynclass.ClassStore;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;

import java.util.ArrayList;
import java.util.List;

public class CoreConfiguration implements Configuration {

    /**
     * Initializes the beans.
     */
    private BeanInitializer beanInitializer = new DefaultBeanInitializer();

    /**
     * Removes any potential proxies of beans.
     */
    private SkippingBeanUnproxy beanUnproxy = new SkippingBeanUnproxy(new DefaultBeanUnproxy());

    /**
     * Contains a store of matches for source and target class pairs. A pair is created only
     * once and reused every time thereafter.
     */
    private BeanMatchStore beanMatchStore = new BeanMatchStore();

    /**
     * Contains a store of classes that are generated by using the MapToDynamicClassStrategy.
     * Every generated class is a downsized source class or a downsized target class.
     */
    private ClassStore classStore = new ClassStore();

    /**
     * The list of packages (and subpackages) containing classes which are eligible for mapping.
     */
    private List<String> packagePrefixes = new ArrayList<String>();

    /**
     * The list of converters that should be checked for conversions.
     */
    private List<BeanConverter> beanConverters = new ArrayList<BeanConverter>();

    private boolean addDefaultConverters = true;

    @Override
    public List<String> getDownsizeTarget() { return null; }

    @Override
    public List<String> getDownsizeSource() { return null; }

    @Override
    public Class getTargetClass() {
        return null;
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public Class getCollectionClass() {
        return null;
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return this.beanInitializer;
    }

    @Override
    public SkippingBeanUnproxy getBeanUnproxy() {
        return this.beanUnproxy;
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
    public boolean isConverterChoosable() {
        return false;
    }

    @Override
    public void withoutDefaultConverters() {
        this.addDefaultConverters = false;
    }

    @Override
    public void addConverter(BeanConverter converter) {
        this.beanConverters.add(converter);
    }

    @Override
    public void addProxySkipClass(Class<?> clazz) {
        this.beanUnproxy.skip(clazz);
    }

    @Override
    public void addPackagePrefix(Class<?> clazz) {
        if (clazz.getPackage() != null) {
            addPackagePrefix(clazz.getPackage().getName());
        }
    }

    @Override
    public void addPackagePrefix(String packagePrefix) {
        this.packagePrefixes.add(packagePrefix);
    }

    @Override
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer = beanInitializer;
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        this.beanUnproxy.setDelegate(beanUnproxy);
    }

    public boolean isAddDefaultConverters() {
        return this.addDefaultConverters;
    }

    @Override
    public void setConverterChoosable(boolean converterChoosable) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set whether the converter is choosable on the Core configuration, works only for override configurations.");
    }

    @Override
    public void downsizeSource(List<String> includeFields) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a include fields on the Core configuration, works only for override configurations");
    }

    @Override
    public void downsizeTarget(List<String> includeFields) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a include fields on the Core configuration, works only for override configurations");
    }

    @Override
    public void setCollectionClass(Class collectionClass) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a target instance on the Core configuration, works only for override configurations");
    }

    @Override
    public void setTargetClass(Class targetClass) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set target class on the Core configuration, works only for override configurations");
    }

    @Override
    public void setTarget(Object target) {
        throw new BeanConfigurationOperationNotAllowedException(
                "Illegal to set a target instance on the Core configuration, works only for override configurations");
    }

    @Override
    public boolean canReuse() {
        return false;
    }

    @Override
    public Class determineTargetClass() {
        return getTargetClass() == null ? getTarget().getClass() : getTargetClass();
    }
}