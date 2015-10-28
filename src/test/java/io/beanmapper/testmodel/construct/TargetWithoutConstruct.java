package io.beanmapper.testmodel.construct;

public class TargetWithoutConstruct {

    public Long id;
    private String fullName;
    public NestedTargetWithConstruct nestedClass;

    public TargetWithoutConstruct(String firstName, String infix, String lastName){
        this.fullName = firstName + infix + lastName;
    }

    public String getFullName() {
        return fullName;
    }
}
