package io.beanmapper.testmodel.rule;

public class TargetWithRule extends SuperWithRule {

    private String name;
    private NestedWithRule nested;

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
