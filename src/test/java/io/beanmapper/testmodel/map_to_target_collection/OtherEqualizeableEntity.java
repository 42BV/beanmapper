package io.beanmapper.testmodel.map_to_target_collection;

import io.beanmapper.core.collections.Equalizer;

public class OtherEqualizeableEntity implements Equalizer {

    private Long id;

    private String name;

    private Long age;

    public OtherEqualizeableEntity(Long id) {
        this.id = id;
    }

    public OtherEqualizeableEntity() {}

    @Override
    public <T extends Equalizer> boolean isEqual(T target) {
        return Equalizer.super.isEqual(target);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}
