package io.beanmapper.testmodel.construct;

public class TargetWithoutConstruct {

    public Long id;
    public NestedTargetWithConstruct nestedClass;
    private String fullName;

    public TargetWithoutConstruct(String firstName, String infix, String lastName) {
        this.fullName = firstName + infix + lastName;
    }

    public String getFullName() {
        return fullName;
    }
}
