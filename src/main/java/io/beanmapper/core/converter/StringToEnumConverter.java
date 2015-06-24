/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

/**
 * Converts a string into an enumeration.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToEnumConverter extends AbstractBeanConverter<String, Enum<?>> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doConvert(String source, Class<? extends Enum<?>> targetClass) {
        return toEnum(targetClass, source);
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> T toEnum(Class<? extends Enum<?>> targetClass, String name) {
        return isNotEmpty(name) ? Enum.valueOf((Class<T>) targetClass, name) : null;
    }

    private static boolean isNotEmpty(String name) {
        return name.trim().length() > 0;
    }

}
