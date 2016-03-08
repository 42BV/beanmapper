package io.beanmapper.testmodel.rule;

public class TargetWithRule {

    public Integer id;
    public String name;
    public NestedWithRule nested;

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
