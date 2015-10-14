package io.beanmapper.testmodel.construct;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct({ "firstName", "infix", "lastName" })
public class SourceWithConstruct {

    public Long id;
    public String firstName;
    public String infix;
    public String lastName;
    public NestedSourceWithoutConstruct nestedClass;

}
