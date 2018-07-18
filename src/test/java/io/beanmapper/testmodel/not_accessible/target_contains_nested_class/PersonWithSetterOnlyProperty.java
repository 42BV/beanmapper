package io.beanmapper.testmodel.not_accessible.target_contains_nested_class;

public class PersonWithSetterOnlyProperty {

    public String result;

    public void setFullName(String fullName) {
        this.result = fullName;
    }

}
