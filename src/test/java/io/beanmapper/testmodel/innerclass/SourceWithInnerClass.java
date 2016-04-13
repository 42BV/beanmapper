package io.beanmapper.testmodel.innerclass;

public class SourceWithInnerClass {

    public Long id;
    public String name;
    public SourceInnerClass innerClass;

    public SourceWithInnerClass(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static class SourceInnerClass {
        public String description;

        public SourceInnerClass(String description) {
            this.description = description;
        }
    }
}
