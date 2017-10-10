package io.beanmapper.testmodel.enums;

import java.util.Arrays;

import io.beanmapper.annotations.BeanCollection;

public class EnumSourceArraysAsList {

    @BeanCollection(elementType = String.class)
    public Object items = Arrays.asList(RGB.values());

}
