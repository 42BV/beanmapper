package io.beanmapper.config;

import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.rule.MappableFields;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;

import java.util.List;

public interface Configuration {

    Class getTargetClass();

    Object getTarget();

    MappableFields getMappableFields();

    BeanInitializer getBeanInitializer();

    SkippingBeanUnproxy getBeanUnproxy();

    BeanMatchStore getBeanMatchStore();

    List<String> getPackagePrefixes();

    List<BeanConverter> getBeanConverters();

    boolean isConverterChoosable();

    void withoutDefaultConverters();

    Class getCollectionClass();

    /**
     * Add a converter class (must inherit from abstract BeanConverter class) to the beanMapper.
     * On mapping, the beanMapper will check for a suitable converter and use its from and
     * to methods to convert the value of the fields to the correct new data type.
     * @param converter an instance of the class that contains the conversion method implementations and inherits
     *                  from the abstract BeanConverter class.
     */
    void addConverter(BeanConverter converter);

    /**
     * Add classes to skip while unproxying to prevent failing of the BeanMapper while mapping
     * proxy classes or classes containing synthetic fields (Like ENUM types).
     * @param clazz the class that is added to the list of skipped classes
     */
    void addProxySkipClass(Class<?> clazz);

    /**
     * Adds a package on the basis of a class. All classes in that package and sub-packages are
     * eligible for mapping. The root source and target do not need to be set as such, because
     * the verification is only run against nested classes which should be mapped implicity as
     * well
     * @param clazz the class which sets the package prefix for all mappable classes
     */
    void addPackagePrefix(Class<?> clazz);

    /**
     * Adds a package on the basis of a class. All classes in that package and sub-packages are
     * eligible for mapping. The root source and target do not need to be set as such, because
     * the verification is only run against nested classes which should be mapped implicity as
     * well
     * @param packagePrefix the String which sets the package prefix for all mappable classes
     */
    void addPackagePrefix(String packagePrefix);

    void setBeanInitializer(BeanInitializer beanInitializer);

    void setBeanUnproxy(BeanUnproxy beanUnproxy);

    boolean isAddDefaultConverters();

    void setTargetClass(Class targetClass);

    void setTarget(Object target);

    void setMappableFields(MappableFields mappableFields);

    void setConverterChoosable(boolean converterChoosable);

    void setCollectionClass(Class collectionClass);

    /**
     * Unsetting a collection class, means that the current collection class will be removed,
     * so that the collection class of the parent configuration is used. If you need to
     * explicitly set the collection class to null, use setCollectionClass instead.
     */
    void unsetCollectionClass();

    /**
     * Unsetting a target class, means that the current target class will be removed,
     * so that the target class of the parent configuration is used. If you need to
     * explicitly set the target class to null, use setTargetClass instead.
     */
    void unsetTargetClass();

    /**
     * Unsetting a target, means that the current target will be removed, so that the
     * target of the parent configuration is used. If you need to explicitly set the
     * target to null, use setTarget instead.
     */
    void unsetTarget();

    /**
     * Used to determine whether the configuration can be reused and modified (Override config)
     * or whether it must be wrapped in a new configuration (Core config).
     * @return true (Override config) if the configuration can be reused, false (Core config) if not.
     */
    boolean canReuse();
}
