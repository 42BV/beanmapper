package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;
import io.beanmapper.testmodel.converter_between_nested_classes.NestedSourceClass;
import io.beanmapper.testmodel.converter_between_nested_classes.NestedTargetAbstractClass;
import io.beanmapper.testmodel.converter_between_nested_classes.NestedTargetClass;

public class NestedSourceClassToNestedTargetClassConverter extends SimpleBeanConverter<NestedSourceClass, NestedTargetAbstractClass> {

    @Override
    protected NestedTargetAbstractClass doConvert(NestedSourceClass source) {
        NestedTargetClass target = beanMapper
                .wrap()
                .setConverterChoosable(false)
                .build()
                .map(source, NestedTargetClass.class);
        target.laptopNumber = "[" + target.laptopNumber + "]";
        return target;
    }

}
