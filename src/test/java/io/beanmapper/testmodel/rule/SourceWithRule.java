package io.beanmapper.testmodel.rule;

public class SourceWithRule {

    private Integer id;
    private String name;
    private NestedWithRule nested;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NestedWithRule getNested() {
        return nested;
    }

    public void setNested(NestedWithRule nested) {
        this.nested = nested;
    }
}
