package io.beanmapper.testmodel.enums;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class DayEnumSourceArraysAsList {

    @BeanCollection(elementType = String.class)
    public Object items = List.of(Day.values());

}
