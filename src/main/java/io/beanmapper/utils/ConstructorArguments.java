package io.beanmapper.utils;

public class ConstructorArguments {

    public Class[] types;
    public Object[] values;

    public ConstructorArguments(int count){
        types = new Class[count];
        values = new Object[count];
    }
}
