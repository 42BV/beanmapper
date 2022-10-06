package io.beanmapper.testmodel.same_source_diff_results;

public class Entity {

    private Long id;
    private String name;
    private String description;

    public Entity(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Entity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
