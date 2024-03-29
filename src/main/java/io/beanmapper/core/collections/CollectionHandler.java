package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.config.CollectionFlusher;

/**
 * Deals with the basic collection manipulations required by BeanMapper
 */
public interface CollectionHandler<C> {

    /**
     * Takes all the content from source and offers it to target. Every item is handled
     * by BeanMapper before it is stored in target.
     * @param beanMapper the beanmapper to use for mapping the individual items
     * @param collectionElementClass the class of an item within the target collection
     * @param source the source collection of items
     * @param target the target collection where the mapped source items will end up
     * @return the target collection
     */
    C copy(
            BeanMapper beanMapper,
            Class collectionElementClass,
            C source,
            C target);

    /**
     * Retrieves either the target collection or creates a new collection instance.
     * If CONSTRUCT is set, it will always create a new instance. For the other two
     * options a new instance will only be created if the target collection is null.
     * If CLEAR is used, the target collection will have its clear() method called.
     * @param collectionUsage the type of collection usage to apply
     * @param preferredCollectionClass the class type of the target collection
     * @param collectionElementClass the class type of an element within the target collection
     * @param targetCollection the actual target collection
     * @param collectionFlusher the flusher to call after clear has been called on a filled
     *                          collection
     * @param flushAfterClear whether flush must be called after clear on a collection has been
     *                        called
     * @return the target collection to copy the source collection to
     */
    C getTargetCollection(
            BeanCollectionUsage collectionUsage,
            Class<C> preferredCollectionClass,
            Class<?> collectionElementClass,
            C targetCollection,
            CollectionFlusher collectionFlusher,
            boolean flushAfterClear);

    /**
     * The type of the collection class. This will be used to determine if the source
     * collection contains the type somewhere in its super classes or interfaces
     * @return the type of the collection class
     */
    Class<C> getType();

    /**
     * Check if the sourceClass contains the matching class somewhere in its
     * hierarchy
     * @param clazz the class to check for the type
     * @return true if the class can be upcast to the type
     */
    boolean isMatch(Class<?> clazz);

    int size(C targetCollection);

    int getGenericParameterIndex();
}
