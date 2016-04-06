package io.beanmapper.testmodel.innerclass;

public class TargetWithInnerClass {

    public Long id;
    public String name;
    public TargetInnerClass innerClass;

    public class TargetInnerClass {
        public String description;
    }
}
