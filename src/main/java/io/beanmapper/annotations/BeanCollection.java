package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.beanmapper.config.FlushAfterClearInstruction;

/**
 * Determines the type of the other side in a collection.
 *
 * <p>When this annotation is set, beanmapper-logic is activated on the field or method it annotates.</p>
 *
 * @apiNote This annotation may also be applied to {@link java.util.Map}, and its implementations. Any mention of collections -
 *          within the context of the usage of BeanCollection - should be assumed to include Map its implementations.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanCollection {

    /**
     * Determines the class-type of the elements within the target collection.
     *
     * <p>The class-type will be used by {@link io.beanmapper.BeanMapper} to pass the target class type.</p>
     *
     * @return The class of the type-parameter of the target collection, or {@code void.class} if none is set.
     *
     * @see io.beanmapper.BeanMapper
     */
    Class<?> elementType() default void.class;

    /**
     * Determines how BeanMapper must deal with the annotated collection.
     *
     * <p>The default option is to apply {@link BeanCollectionUsage#CLEAR}, which means it will reuse the collection
     * instance by calling {@link java.util.Collection#clear()} - or {@link java.util.Map#clear()} - on it. If it does
     * not exist, it will be created.</p>
     * <p>Others options are {@link BeanCollectionUsage#REUSE} (if the collection exists, it will not be changed
     * upfront) or {@link BeanCollectionUsage#CONSTRUCT} (the collection is newly constructed).</p>
     *
     * @return The instruction for handling the annotated Collection, as an enum. Returns
     *         {@code BeanCollectionUsage.CLEAR} by default.
     *
     * @apiNote <p>{@code BeanCollectionUsage.CONSTRUCT} will not work with frameworks like Hibernate, since the managed
     *             Collection will be removed in favor of a new, unmanaged one.</p><p>If this annotation is applied to a
     *             field or accessor that is not associated with a collection, the presence of the annotation is simply
     *             ignored.</p>
     *
     * @see BeanCollectionUsage
     * @see io.beanmapper.core.BeanMatchStore
     */
    BeanCollectionUsage beanCollectionUsage() default BeanCollectionUsage.CLEAR;

    /**
     * Sets what type of collection the user would prefer the elements of the source are mapped into.
     *
     * <p>BeanMapper will verify whether it conforms to the possibilities. If it does, this class will be instantiated
     * instead of the default one provided by the collection handler.</p>
     *
     * @return The class to instantiate rather than the one provided by the
     *         {@link io.beanmapper.core.collections.CollectionHandler}.
     *
     * @see io.beanmapper.core.collections.CollectionHandler
     */
    Class<?> preferredCollectionClass() default void.class;

    /**
     * Determines whether the target-collection will be flushed after clearing it.
     *
     * <p>When usage is {@link BeanCollectionUsage#CLEAR} and the target collection is being managed by, eg, Hibernate's
     * OneToMany, in combination with {@code orphanRemoval = true}, clearing the collection will trigger
     * DELETE-statements. The DELETE-statements are executed after the INSERT-statements, which may lead to a
     * constraint-violation in records with the UNIQUE-constraint.</p>
     * <p>Flushing the collection after clearing it, will force Hibernate to perform any DELETE-statements before the
     * INSERT-statements, preventing constraint-violations.</p>
     *
     * @return Whether to flush the collection after clearing or not. Returns
     *         {@link FlushAfterClearInstruction#FLUSH_ENABLED} by default.
     *
     * @apiNote <p>Flush persists any merged entity.</p>
     *          <p>It is advisable to use this annotation in concert with io.beanmapper.spring.Lazy, in order to
     *             guarantee that the BeanMapper operates within the transaction-scope.</p>
     */
    FlushAfterClearInstruction flushAfterClear() default FlushAfterClearInstruction.FLUSH_ENABLED;

}
