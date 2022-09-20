package io.beanmapper.testmodel.map_to_target_collection;

import io.beanmapper.core.collections.Equalizer;

public class DissimilarEqualizeableEntity implements Equalizer {

    private Long id;

    private String name;

    public DissimilarEqualizeableEntity(Long id) {
        this.id = id;
    }

    public DissimilarEqualizeableEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T extends Equalizer> boolean isEqual(T target) {
        return Equalizer.super.isEqual(target);
    }
}
