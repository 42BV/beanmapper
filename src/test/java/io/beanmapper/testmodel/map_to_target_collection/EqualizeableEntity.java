package io.beanmapper.testmodel.map_to_target_collection;

import io.beanmapper.core.collections.Equalizer;

public class EqualizeableEntity implements Equalizer {
    private Long id;
    private String name;

    private Long age;

    public EqualizeableEntity(Long id, String name, Long age) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Long getAge() {
        return age;
    }

    @Override
    public <T extends Equalizer> boolean isEqual(T target) {
        if (target instanceof EqualizeableEntity entity) {
            return this.id.equals(entity.id);
        } else if (target instanceof OtherEqualizeableEntity otherEntity) {
            return this.id.equals(otherEntity.getId());
        } else if (target instanceof DissimilarEqualizeableEntity dissimilarEntity) {
            return this.id.equals(dissimilarEntity.getId());
        }
        return false;
    }
}
