package io.beanmapper.core.converter.impl;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.converter.SimpleBeanConverter;
import io.beanmapper.testmodel.converterBetweenNestedClasses.NestedSourceClass;
import io.beanmapper.testmodel.converterBetweenNestedClasses.NestedTargetAbstractClass;
import io.beanmapper.testmodel.converterBetweenNestedClasses.NestedTargetClass;

public class NestedSourceClassToNestedTargetClassConverter extends SimpleBeanConverter<NestedSourceClass, NestedTargetAbstractClass> {

    private final BeanMapper beanMapper;

    public NestedSourceClassToNestedTargetClassConverter(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    @Override
    protected NestedTargetAbstractClass doConvert(NestedSourceClass source) {
        return beanMapper.map(source, NestedTargetClass.class);
    }
}
