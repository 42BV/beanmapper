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
public class AnyToEnumConverter<T extends Enum<T>> extends AbstractBeanConverter<Object, Enum<T>> {

    public AnyToEnumConverter() {
        super(Object.class, Enum.class);
    }

    private static boolean isNotEmpty(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Retrieve the enumeration from a specific type and name.
     * <br>
     * <b>This is placed in a separate method to due to the {@code Enum<T>} generic restriction.</b>
     * @param enumClass the enumeration type
     * @param name the name of the enumeration constant
     * @return the enumeration, if any
     */
    private static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        return Enum.valueOf(enumClass, name);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Enum<T> doConvert(Object source, Class<? extends Enum<T>> targetClass) {
        if (source == null) {
            return null;
        }
        String sourceText = source instanceof Enum<?> enumerable ? enumerable.name() : source.toString();
        if (isNotEmpty(sourceText)) {
            return valueOf((Class<T>) targetClass, sourceText);
        }
        return null;
    }

}
