package io.beanmapper.testmodel.optional_getter;

import java.util.Optional;

public class MyEntityResultWithNestedOptionalField {

    public String value;

    public Optional<Optional<MyEntityResultWithNestedOptionalField>> child;

}
