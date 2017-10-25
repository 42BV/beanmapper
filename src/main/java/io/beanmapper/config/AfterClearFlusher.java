package io.beanmapper.config;

/**
 * After BeanMapper calls the clear() method on a collection, it will check for the
 * presence of AfterClearFlusher instance. All instances found will be executed. The
 * use case triggering this functionality is the fact that Hibernate's @OneToMany first
 * executes insert SQL-statements, before the delete SQL statements. With constraints,
 * this is an issue. By forcing the flush after a clear, Hibernate is forced to first
 * execute its delete SQL-statements, before the inserts.
 */
public interface AfterClearFlusher {

    /**
     * Calls the flush method. Typically, this is used to force an ORM implementation
     * to flush its cache.
     */
    void flush();

}
