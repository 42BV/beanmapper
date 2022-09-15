package io.beanmapper.testmodel.optional_getter;

import java.util.Optional;

public class MyEntity {

    public String value = "42";
    public MyEntity child;

    public String getValue() {
        return value;
    }

    public Optional<MyEntity> getChild() {
        return Optional.ofNullable(child);
    }

}
