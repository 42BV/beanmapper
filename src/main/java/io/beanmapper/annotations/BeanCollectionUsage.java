package io.beanmapper.annotations;

/**
 * Determines how to deal with the target collection. If the value is set to CONSTRUCT, it
 * will also be recreated, even if it already exists. If REUSE is set, it will attempt to
 * reuse the collection if it exists, or else CONSTRUCT it. If CLEAR is set, it will
 * call clear() on the collection, unless it does not exist (call CONSTRUCT).
 */
public enum BeanCollectionUsage {
    /**
     * Always reconstruct the target collection
     */
    CONSTRUCT(true, false),
    /**
     * Reuse the target collection if it exists; construct if not.
     */
    REUSE(false, false),
    /**
     * Call clear on the target collection if it exists; construct if not. DEFAULT option
     */
    CLEAR(false, true);

    private final boolean construct;
    private final boolean clear;

    BeanCollectionUsage(boolean construct, boolean clear) {
        this.construct = construct;
        this.clear = clear;
    }

    public boolean mustClear() {
        return this.clear;
    }

    public boolean mustConstruct(Object targetCollection) {
        return this.construct || targetCollection == null;
    }

}
