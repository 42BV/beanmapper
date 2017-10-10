package io.beanmapper.config;

import java.util.List;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.dynclass.ClassStore;

public interface Configuration {

    /**
     * When include fields are passed, BeanMapper will assume that the generation (or reuse)
     * of a dynamic class is required. For this, DynamicBeanMapper is used. Note that
     * include fields is a marker field which impact the selection of the mapping strategy.
     * Include fields never refer to the parent configuration.
     * @return the fields to include in the target
     */
    List<String> getDownsizeTarget();

    /**
     * When include fields are passed, BeanMapper will assume that the generation (or reuse)
     * of a dynamic class is required. For this, DynamicBeanMapper is used. Note that
     * include fields is a marker field which impact the selection of the mapping strategy.
     * Include fields never refer to the parent configuration.
     * @return the fields to include in the source
     */
    List<String> getDownsizeSource();

    /**
     * The class that represents the collection itself. Used to instantiate a collection.
     * Note that collection class is a marker field which impact the selection of the mapping
     * strategy. Collection class never refers to the parent configuration.
     * @return class of the collection
     */
    Class getCollectionClass();

    /**
     * The class that represents the target class. Used to instantiate a target for the mapping.
     * Note that target class is a marker field which impact the selection of the mapping
     * strategy. Target class never refers to the parent configuration.
     * @return class of the target
     */
    Class getTargetClass();

    /**
     * The target to map to. Note that collection class is a marker field which impact the
     * selection of the mapping strategy. Target never refers to the parent configuration.
     * @return class of the collection
     */
    Object getTarget();

    /**
     * The active parent for the field that is currently being mapped. This is always a parent
     * on the target side of BeanMapper. @BeanParent makes us of this variable to assign to a
     * field
     * @return the parent of the active field
     */
    Object getParent();

    BeanInitializer getBeanInitializer();

    SkippingBeanUnproxy getBeanUnproxy();

    /**
     * Always use the CoreConfiguration beanmatch store
     * @return the one beanmatch store
     */
    BeanMatchStore getBeanMatchStore();

    /**
     * Always use the CoreConfiguration class store
     * @return the one class store
     */
    ClassStore getClassStore();

    List<String> getPackagePrefixes();

    List<BeanConverter> getBeanConverters();

    /**
     * Returns the list of registered collection handlers. The handlers are used to deal
     * with the complexities of mapping between collections. Methods for copying, clearing
     * and construction are supplied.
     * @return the list of registered collection handlers
     */
    List<CollectionHandler> getCollectionHandlers();

    /**
     * Finds the correction handler for the class. Null, if not found.
     * @param clazz class to find the correct collection handler for
     * @return collection handler for the class, or null if not found
     */
    CollectionHandler getCollectionHandlerFor(Class<?> clazz);

    /**
     * Finds the collection handler for the collection class, null if not exists
     * @return the collection handler for the collection class, null if not exists
     */
    CollectionHandler getCollectionHandlerForCollectionClass();

    /**
     * Returns the entire list of strict bean pairs. The properties on the strict side must
     * have matching properties on the other, non-strict side.
     * @return the entire list of strict bean pairs.
     */
    List<BeanPair> getBeanPairs();

    Boolean isConverterChoosable();

    void withoutDefaultConverters();

    /**
     * Returns the classname suffix that determines a source class is to be treated as strict
     * with regards to mapping. Default is "Form"
     * @return the source classname suffix for a class to be treated as strict
     */
    String getStrictSourceSuffix();

    /**
     * Returns the classname suffix that determines a target class is to be treated as strict
     * with regards to mapping. Default is "Result"
     * @return the target classname suffix for a class to be treated as strict
     */
    String getStrictTargetSuffix();

    /**
     * Determines if strict mapping convention will be applied. This means that if a source
     * class has the strict source suffix, or a target class has the strict target suffix,
     * the classes will be treated as if they are strict. This implies that all of their
     * properties will require matching properties on the other side. Default is true.
     * @return if true, the strict mapping convention will be applied
     */
    Boolean isApplyStrictMappingConvention();

    /**
     * Returns the collection of strictSourceSuffix, strictTargetSuffix and
     * applyStrictMappingConvention properties.
     * @return all properties required for dealing with the strict mapping convention
     */
    StrictMappingProperties getStrictMappingProperties();

    /**
     * Returns the type of collection usage for the current collection mapping
     * @return collection usage to apply
     */
    BeanCollectionUsage getCollectionUsage();

    /**
     * Gets the preferred collection class to be instantiated. If it has this choice,
     * it will use this class over the one provided by the collection handler.
     * @return the collection class to prefer for instantiation
     */
    Class<?> getPreferredCollectionClass();

    /**
     * Add a converter class (must inherit from abstract BeanConverter class) to the beanMapper.
     * On mapping, the beanMapper will check for a suitable converter and use its from and
     * to methods to convert the value of the fields to the correct new data type.
     * @param converter an instance of the class that contains the conversion method implementations
     *                  and inherits from the abstract BeanConverter class.
     */
    void addConverter(BeanConverter converter);

    /**
     * Registers a collection handler to the configuration. The Collection handlers supply the
     * underlying mechanism for dealing with mapping from and to collections. They supply methods
     * for copying, clearing and construction.
     * @param collectionHandler the collection handler to register
     */
    void addCollectionHandler(CollectionHandler collectionHandler);

    /**
     * Adds a new pair of classes of which the source is strict. The strict side must match for all
     * public fields and getter properties.
     * @param source the source class that must match, also the strict side of the pair
     * @param target the target class that must match
     */
    void addBeanPairWithStrictSource(Class source, Class target);

    /**
     * Adds a new pair of classes of which the target is strict. The strict side must match for all
     * public fields and setter properties.
     * @param source the source class that must match
     * @param target the target class that must match, also the strict side of the pair
     */
    void addBeanPairWithStrictTarget(Class source, Class target);

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

    void setConverterChoosable(boolean converterChoosable);

    /**
     * Sets the field to downsize the source class.
     * THe limited source class is mapped over the given target class.
     * @param includeFields The fields to be mapped to the target class.
     */
    void downsizeSource(List<String> includeFields);

    /**
     * Sets the only fields that are allowed in the target class. If the include fields are set,
     * it impacts the usage of the mapping strategy. Note that getting this field never refers to
     * the parent configuration.
     * @param includeFields The field that are allowed in the target class.
     */
    void downsizeTarget(List<String> includeFields);

    /**
     * Sets the collection class of the collection. Used to instantiate the collection. If the
     * collection class is set, it impacts the usage of the mapping strategy. Note that getting
     * this field never refers to the parent configuration.
     * @param collectionClass the class type of the collection
     */
    void setCollectionClass(Class collectionClass);

    /**
     * Sets the target class. Used to instantiate the target. If this class is set, it impact
     * the usage of the mapping strategy. Note that getting this field never refers to the
     * parent configuration.
     * @param targetClass the class type of the target
     */
    void setTargetClass(Class targetClass);

    /**
     * Sets the target. If the target is set, it impact the usage of the mapping strategy.
     * Note that getting this field never refers to the parent configuration.
     * @param target the target instance to map to
     */
    void setTarget(Object target);

    /**
     * The active parent for the field that is currently being mapped. This is always a parent
     * on the target side of BeanMapper. @BeanParent makes us of this variable to assign to a
     * field
     * @param parent the parent of the active field
     */
    void setParent(Object parent);

    /**
     * Used to determine whether the configuration can be reused and modified (Override config)
     * or whether it must be wrapped in a new configuration (Core config).
     * @return true (Override config) if the configuration can be reused, false (Core config) if not.
     */
    boolean canReuse();

    Class determineTargetClass();

    /**
     * Sets the classname suffix that determines a source class is to be treated as strict
     * with regards to mapping. Default is "Form"
     * @param strictSourceSuffix the source classname suffix for a class to be treated as strict
     */
    void setStrictSourceSuffix(String strictSourceSuffix);

    /**
     * Sets the classname suffix that determines a target class is to be treated as strict
     * with regards to mapping. Default is "Result"
     * @param strictTargetSuffix the target classname suffix for a class to be treated as strict
     */
    void setStrictTargetSuffix(String strictTargetSuffix);

    /**
     * Determines if strict mapping convention will be applied. This means that if a source
     * class has the strict source suffix, or a target class has the strict target suffix,
     * the classes will be treated as if they are strict. This implies that all of their
     * properties will require matching properties on the other side. Default is true.
     * @param applyStrictMappingConvention whether the strict mapping convention must be applied
     */
    void setApplyStrictMappingConvention(Boolean applyStrictMappingConvention);

    /**
     * Sets the collection usage for the current collection mapping
     * @param collectionUsage the collection usage to apply
     */
    void setCollectionUsage(BeanCollectionUsage collectionUsage);

    /**
     * Sets the preferred collection class to be instantiated. If it has this choice,
     * it will use this class over the one provided by the collection handler.
     * @param preferredCollectionClass the collection class to prefer for instantiation
     */
    void setPreferredCollectionClass(Class<?> preferredCollectionClass);

}
