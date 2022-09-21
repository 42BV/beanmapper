package io.beanmapper.testmodel.optional_getter;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class MyEntityResultWithList {

    public String value;

    public List<MyEntityResultWithList> children;

}
