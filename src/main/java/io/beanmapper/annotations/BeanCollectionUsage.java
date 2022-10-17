package io.beanmapper.annotations;

/**
 * Enum used to determine how to handle the mapping of elements of a source collection, to a target collection.
 *
 * <p>This enum provides three options, with associated expected behaviour.</p>
 * <ol start="0">
 *     <li>{@link #CONSTRUCT}: Indicates that a new collection should be constructed, even if a target-object
 *                             exists.</li>
 *     <li>{@link #REUSE}:     Indicates that the target-collection should be reused. If no target-collection exists,
 *                             the implementation should default to handling the mapping as if the relevant collection
 *                             is annotated with {@code CONSTRUCT}, instead.</li>
 *     <li>{@link #CLEAR}:     Indicates that the target-collection should be cleared before new elements are inserted.
 *                             If no target-collection exists, the implementation should default to handling the mapping
 *                             as if the relevant collection is annotated with {@code CONSTRUCT}, instead.</li>
 * </ol>
 */
public enum BeanCollectionUsage {

    /**
     * Indicates that mapping should always construct a new collection, even if a target-collection exists.
     */
    CONSTRUCT,

    /**
     * Indicates that the target-collection should be reused.
     *
     * <p>If no target-collection exists, the implementation should default to handling the mapping as if the relevant
     * collection is annotated with {@code CONSTRUCT}, instead.</p>
     */
    REUSE,

    /** Indicates that the target-collection should be cleared before new elements are inserted.
     *  <p>If no target-collection exists, the implementation should default to handling the mapping as if the relevant
     *  collection is annotated with {@code CONSTRUCT}, instead.</p></li>
     */
    CLEAR;

    /**
     * @deprecated
     *
     * @return Whether the collection annotated with this annotation should be cleared.
     */
    @Deprecated(since = "v4.1.0", forRemoval = true)
    public boolean mustClear() {
        return this == CLEAR;
    }

    /**
     * Determines whether the target-collection is immutable.
     *
     * <p>To determine whether the target-collection is immutable, the name of the class of the target-collection is
     * retrieved. That name is tested to check whether it starts with {@code "java.util.Collections."} or
     * {@code "java.util.ImmutableCollections."}</p>
     * <p>If the test returns true, a new target-collection object should be created.</p>
     *
     * @param targetCollection The target-collection that will be checked for immutability.
     *
     * @return True, if the target-collection is immutable. False, otherwise.
     *
     * @see java.util.Collections
     * @see java.util.ImmutableCollections.AbstractImmutableCollection
     */
    private boolean isTargetCollectionImmutable(Object targetCollection) {
        var canonicalClassName = targetCollection.getClass().getCanonicalName();
        return canonicalClassName.startsWith("java.util.Collections.")
                || canonicalClassName.startsWith("java.util.ImmutableCollections.");
    }

    /**
     * Determines whether the target-collection is an invalid target for copying elements to.
     *
     * <p>To determine whether the target-collection is an invalid target, the target-collection is tested for nullity,
     * as well as immutability.</p>
     *
     * @param targetCollection The target-collection that will be checked for validity.
     *
     * @return True is the target-collection does not support adding new elements. False, otherwise.
     *
     * @see java.util.Collections
     * @see java.util.ImmutableCollections.AbstractImmutableCollection
     */
    private boolean isTargetCollectionInvalidTarget(Object targetCollection) {
        return targetCollection == null || this.isTargetCollectionImmutable(targetCollection);
    }

    /**
     * Returns whether the target-collection should be constructed, rather than reusing an existing collection.
     *
     * <p>To determine whether the target-collection should be constructed, first the type of this instance is checked.
     * If this instance equals {@link BeanCollectionUsage#CONSTRUCT}, true is returned.</p>
     * <p>Additionally, the target-collection passed to this method is checked for validity by
     * {@link BeanCollectionUsage#isTargetCollectionInvalidTarget(Object)}. If the target-collection is invalid, this
     * method will return true as well.</p>
     * <p>If neither of the checks return true, this method will return false.</p>
     *
     * @param targetCollection
     *
     * @return True, if this instance equals {@code BeanCollectionUsage.CONSTRUCT}, or if the target-collection is
     *         invalid, as determined by {@code BeanCollectionUsage.isTargetCollectionInvalidTarget(Object)}. False,
     *         otherwise.
     */
    public boolean mustConstruct(Object targetCollection) {
        return this == CONSTRUCT || this.isTargetCollectionInvalidTarget(targetCollection);
    }

}
