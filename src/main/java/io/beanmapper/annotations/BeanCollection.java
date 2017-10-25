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

    /**
     * It is possible to set your own preferred instantiated class. BeanMapper will verify
     * whether it conforms to the possibilities. If it does, this class will be instantiated
     * instead of the default one provided by the collection handler
     * @return the class to instantiate instead of the one provided by the collection handler
     */
    Class preferredCollectionClass() default void.class;

    /**
     * When usage is CLEAR and the target collection is being managed by, eg, Hibernate's
     * OneToMany in combination with orphanRemoval=true, clearing the collection will trigger
     * delete statements. The problem is that these statements are executed AFTER the insert
     * statements. If a record is constrained by unicity, this might lead to a constraint
     * violation. One way to solve this is by asking BeanMapper to flush after the call to
     * clear. This will force Hibernate to perform the delete before the insert statements.
     * Note that the flush also persists any merged entity. You are advised to use this
     * functionality in combination with Lazy to make sure BeanMapper operates within the
     * transaction scope. If you do, the flush action can be rolled back, resulting in the
     * original state being preserved.
     */
    boolean flushAfterClear() default false;

}
