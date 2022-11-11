package io.beanmapper.core.constructor;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanProperty;
import io.beanmapper.exceptions.BeanInstantiationException;

public class ConstructorArguments {

    private final List<Class<?>> types = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();

    public ConstructorArguments(Object source, BeanMatch beanMatch, String[] constructorArgs) {
        if (constructorArgs == null)
            throw new IllegalArgumentException("Cannot construct ConstructorArguments-object: Constructor-parameter String[] constructorArgs is null.");

        for (String constructorArg : constructorArgs) {
            if (constructorArgumentFoundInSource(beanMatch, constructorArg)) {
                BeanProperty constructField = beanMatch.getSourceNodes().get(constructorArg);
                if (constructField == null) {
                    constructField = beanMatch.getAliases().get(constructorArg);
                }
                types.add(constructField.getAccessor().getType());
                values.add(constructField.getObject(source));
            } else {
                throw new BeanInstantiationException(beanMatch.getTargetClass(), null);
            }
        }
    }

    private boolean constructorArgumentFoundInSource(BeanMatch beanMatch, String constructorArg) {
        return beanMatch.getSourceNodes().containsKey(constructorArg) || beanMatch.getAliases().containsKey(constructorArg);
    }

    public Class<?>[] getTypes() {
        return types.toArray(new Class<?>[0]);
    }

    public Object[] getValues() {
        return values.toArray(new Object[0]);
    }
}
