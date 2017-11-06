package io.beanmapper.strategy;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.core.BeanField;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.exceptions.BeanInstantiationException;

public class ConstructorArguments {

    private List<Class> types = new ArrayList<Class>();
    private List<Object> values = new ArrayList<Object>();

    public ConstructorArguments(Object source, BeanMatch beanMatch, String[] constructorArgs){

        for (String constructorArg : constructorArgs) {
            if (constructorArgumentFoundInSource(beanMatch, constructorArg)) {
                BeanField constructField = beanMatch.getSourceNode().get(constructorArg);
                if(constructField == null) {
                    constructField = beanMatch.getAliases().get(constructorArg);
                }
                types.add(constructField.getProperty().getType());
                values.add(constructField.getObject(source));
            } else {
                throw new BeanInstantiationException(beanMatch.getTargetClass(), null);
            }
        }
    }

    private boolean constructorArgumentFoundInSource(BeanMatch beanMatch, String constructorArg) {
        return beanMatch.getSourceNode().containsKey(constructorArg) || beanMatch.getAliases().containsKey(constructorArg);
    }

    public Class[] getTypes() {
        return types.toArray(new Class[types.size()]);
    }

    public Object[] getValues() {
        return values.toArray(new Object[values.size()]);
    }
}
