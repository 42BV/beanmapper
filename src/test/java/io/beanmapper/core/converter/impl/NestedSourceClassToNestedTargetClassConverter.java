package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;
import io.beanmapper.testmodel.converterbetweennestedclasses.NestedSourceClass;
import io.beanmapper.testmodel.converterbetweennestedclasses.NestedTargetAbstractClass;
import io.beanmapper.testmodel.converterbetweennestedclasses.NestedTargetClass;

public class NestedSourceClassToNestedTargetClassConverter extends SimpleBeanConverter<NestedSourceClass, NestedTargetAbstractClass> {

    @Override
    protected NestedTargetAbstractClass doConvert(NestedSourceClass source) {
        return beanMapper.map(source, NestedTargetClass.class);
    }
}
