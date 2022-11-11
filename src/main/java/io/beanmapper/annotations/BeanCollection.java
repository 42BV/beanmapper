package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;

import io.beanmapper.core.collections.AbstractCollectionHandler;
import io.beanmapper.utils.Trinary;

/**
 * BeanCollection provides runtime-information to BeanMapper, relevant to the mapping of
 * {@link Collection}-types, as well as {@link Map}-types.
 *
 * <p>The BeanCollection-annotation can be used to annotate fields of a {@link Collection}-type, as well as
 * fields of the {@link Map}-type. Additionally, this annotation can be applied to method returning on of the
 * aforementioned types, or their subtypes.</p>
 *
 * <p>This annotation provides the following fields, with associated behaviour:
 * <ul>
 *     <li>{@link #elementType() Class elementType}: Used by BeanMapper to determine the type of the elements in the
 *     target-collection. This field is essential to the successful mapping of a source-collection to a
 *     target-collection with a different element-type requirement.</li>
 *     <li>{@link #beanCollectionUsage() BeanCollectionUsage beanCollectionUsage}: Used by BeanMapper to determine how
 *     the target-collection will be created and populated. BeanCollectionUsage provides three distinct values with an
 *     associated behaviour:
 *     <ol start="0">
 *         <li>{@link BeanCollectionUsage#CLEAR}: Indicates that the target-collection should be cleared, using the
 *         appropriate clear-method ({@link Collection#clear()}, or {@link Map#clear()}. If the target-collection has
 *         not been instantiated, a new collection will be created.</li>
 *         <li>{@link BeanCollectionUsage#REUSE}: Indicates that the target-collection should be reused, without taking
 *         any preparatory action.</li>
 *         <li>{@link BeanCollectionUsage#CONSTRUCT}: Indicates that a new target-collection must be constructed,
 *         regardless of whether an object already exists.</li>
 *     </ol></li>
 *     <li>{@link #preferredCollectionClass() Class preferredCollectionClass}: Used to indicate what type of collection
 *     the user would prefer to be created for the target-collection. This will be disregarded if the
 *     preferredCollectionClass does not provide a no-args constructor.</li>
 *     <li>{@link #flushAfterClear() Trinary flushAfterClear}: Only relevant when beanCollectionUsage is set to
 *     BeanCollectionUsage.CLEAR. <p>When a collection is managed by an ORM, such as Hibernate, clearing a collection may
 *     trigger DELETE-operations. As these DELETE-operations would take place after the INSERT-operations, this may
 *     lead to constraint violations for records with a UNIQUE-constraint. By performing a flush after clearing the
 *     collection, any DELETE-operations will be performed before the INSERT-operations.</p><p>As such, merged entities will
 *     also be persisted. It is recommended to use this option in concert with the BeanMapper-Spring's Lazy, to ensure
 *     that any operations are performed from within a transactional context.</p></li>
 * </ul></p>
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanCollection {

    /**
     * Determines the class type of the elements within the target collection.
     *
     * <p>The type will be used by BeanMapper to map the elements of the source-collection, to the correct type, before
     * copying the elements to the target-collection. The source-collection will remain unmodified, as will the
     * individual elements in the source-collection.</p>
     * <p>If this option is not set explicitly, it will be set to void.class by default. During mapping, a value of
     * void.class will be ignored.</p>
     *
     * @return The class type of the elements in the target-collection.
     */
    Class<?> elementType() default void.class;

    /**
     * Determines how the target-collection is created, and populated with the mapped elements from the
     * source-collection.
     *
     * <p>The following values are provided, with associated behaviour:
     * <ol start="0">
     *     <li>{@link BeanCollectionUsage#CLEAR}: Indicates that the target-collection should be cleared, using the
     *         appropriate clear-method ({@link Collection#clear()}, or {@link Map#clear()}. If the target-collection
     *         has not been instantiated, a new collection will be created.</li>
     *     <li>{@link BeanCollectionUsage#REUSE}: Indicates that the target-collection should be reused, without taking
     *         any preparatory action.</li>
     *     <li>{@link BeanCollectionUsage#CONSTRUCT}: Indicates that a new target-collection must be constructed,
     *         regardless of whether an object already exists.</li>
     *     </ol>
     *  </p>
     *
     * @return The way the target collection is created and populated.
     *
     * @apiNote CONSTRUCT will not work with frameworks like Hibernate, since the managed collection will be
     * removed in favor of a new, unmanaged one.
     */
    BeanCollectionUsage beanCollectionUsage() default BeanCollectionUsage.CLEAR;

    /**
     * Allows users to set what collection-implementation should be instantiated by BeanMapper, to serve as the
     * target-collection.
     *
     * <p>When setting this option, the user should consider whether the specified class is a viable type. To determine
     * that, the user should consider the following:
     * <ul>
     *     <li>Is the specified type a non-abstract implementation of the target-collection's type?</li>
     *     <li>Does the specified type provide a public no-args constructor?</li>
     *     <li>Does the specified type support add-operations?</li>
     * </ul>
     * If all of the above can be answered with "yes", the user should be able to use the specified class.</p>
     *
     * <p>Specifying a type here, will override the instantiation of the default implementation of the target-collection
     * , as specified in the relevant implementation of the {@link AbstractCollectionHandler#create()}-method</p>
     *
     * @return The class to instantiate instead of the one provided by the CollectionHandler.
     */
    Class preferredCollectionClass() default void.class;

    /**
     * Allows the user to set whether the ORM should flush after the clear-operations is finished, possibly preventing
     * constraint violations due to UNIQUE-constraints in the records.
     *
     * <p>Only relevant when beanCollectionUsage is set to BeanCollectionUsage.CLEAR.</p>
     *
     * <p>When a collection is managed by an ORM, such as Hibernate, clearing a collection may trigger
     * DELETE-operations. As these DELETE-operations would take place after the INSERT-operations, this may lead to
     * constraint violations for records with a UNIQUE-constraint. By performing a flush after clearing the collection,
     * any DELETE-operations will be performed before the INSERT-operations.</p>
     *
     * <p>As such, merged entities will also be persisted. It is recommended to use this option in concert with the
     * BeanMapper-Spring's Lazy, to ensure that any operations are performed from within a transactional context.</p>
     *
     * @return Whether flushing is enabled, as a {@link Trinary}. Enabled by default.
     */
    Trinary flushAfterClear() default Trinary.ENABLED;

}
