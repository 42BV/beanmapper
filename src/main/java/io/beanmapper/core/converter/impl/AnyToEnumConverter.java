/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

/**
 * Converts a string into an enumeration.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class AnyToEnumConverter extends AbstractBeanConverter<Object, Enum<?>> {

    public AnyToEnumConverter() {
        super(Object.class, Enum.class);
    }

    @Override
    protected Enum<?> doConvert(Object source, Class<? extends Enum<?>> targetClass) {
        if (source == null) {
            return null;
        }
        if (targetClass.isInstance(source)) {
            return (Enum<?>)source;
        }
        String sourceText = source.toString();
        if (isNotEmpty(sourceText)) {
            return valueOf(targetClass, sourceText);
        }
        return null;
    }

    private static boolean isNotEmpty(String name) {
        return name != null && name.trim().length() > 0;
    }
    
    /**
     * Retrieve the enumeration from a specific type and name.
     * <br>
     * <b>This is placed in a seperate method to due to the {@code Enum<T>} generic restriction.</b>
     * @param enumClass the enumeration type
     * @param name the name of the enumeration constant
     * @return the enumeration, if any
     */
    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> T valueOf(Class<? extends Enum<?>> enumClass, String name) {
        return Enum.valueOf((Class<T>) enumClass, name);
    }

}
