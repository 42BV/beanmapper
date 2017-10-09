package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determines the type of the other side in a collection. When this annotation is set, beanmapper
 * logic is activated on it.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanCollection {

    /**
     * Determines the class type of an element within the target collection. The class
     * type will be used by BeanMapper to pass the target class type.
     * @return the class type of a target element collection
     */
    Class elementType();

    /**
     * If the target collection is null, BeanMapper will attempt to instantiate a new
     * one. The result of this method will determine the class to instantiate.
     * @return
     */
    Class targetCollectionType() default void.class;

    /**
     * Determines how BeanMapper must deal with the target collection. The default option
     * is to apply CLEAR, which means it will reuse the collection instance by calling clear()
     * on it. If it does not exist, it will be created. Others options are REUSE (if the
     * collection exists, it will not be changed upfront) or CONSTRUCT (the collection is
     * newly constructed). Note that especially CONSTRUCT will not work with frameworks like
     * Hibernate, since the managed collection will be removed in favor of a new, unmanaged
     * one.
     * @return the way the target collection must be dealt with
     */
    BeanCollectionUsage beanCollectionUsage() default BeanCollectionUsage.CLEAR;
}
