package io.beanmapper.strategy.record.model.computed_getter;

/**
 * Source class with a computed getter (getter without backing field).
 * This simulates a JPA entity with a computed URL property.
 */
public class SourceWithComputedUrl {

    public Long id;
    public String name;

    public SourceWithComputedUrl(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Computed getter - no backing field exists for 'url'.
     * This is a common pattern in JPA entities for computed properties.
     */
    public String getUrl() {
        return id != null ? "/api/items/" + id : null;
    }
}
