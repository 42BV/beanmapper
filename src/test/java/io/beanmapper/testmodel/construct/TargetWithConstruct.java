package io.beanmapper.testmodel.construct;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct({ "firstName", "infix", "lastName" })
public class TargetWithConstruct {

    public Long id;
    private String fullName;

    public TargetWithConstruct(String firstName, String infix, String lastName){
        this.fullName = firstName + " " + infix + " " + lastName;
    }

    public String getFullName() {
        return fullName;
    }
}
