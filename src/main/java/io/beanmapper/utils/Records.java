package io.beanmapper.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collection;

import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;
import io.beanmapper.exceptions.RecordConstructorNotFoundException;

/**
 * <p>Utility-class for methods commonly used when mapping to and from records.</p>
 */
public final class Records {

    private Records() {
    }

    /**
     * <p>Gets the names of all the fields that are passed to the given record through the constructor.</p>
     * <p>By using the names of {@link java.lang.reflect.RecordComponent}-objects, this method won't map any of the
     * static fields a record may contain.</p>
     *
     * @param clazz The record of which the names of the RecordComponent-objects will be returned.
     * @return An array containing the names of the RecordComponent-objects.
     */
    public static String[] getRecordFieldNames(Class<? extends Record> clazz) {
        String[] values = new String[clazz.getRecordComponents().length];
        var recordComponents = clazz.getRecordComponents();
        for (var i = 0; i < recordComponents.length; ++i) {
            values[i] = recordComponents[i].getName();
        }
        return values;
    }

    /**
     * Gets the canonical constructor of the given record.
     *
     * <p>The canonical constructor is the constructor in a record, that is automatically generated based off of the
     * signature of the record. A canonical constructor requires every RecordComponent as its parameter, in the order
     * they were declared in.</p>
     * <p>Based off of the requirements for a canonical constructor, we can retrieve the canonical constructor by
     * creating an array of the types of the RecordComponents, in the correct order, and calling
     * {@link Class#getDeclaredConstructor(Class[])}</p>
     *
     * @param recordClass The type of the record.
     * @return The canonical constructor of the record.
     * @param <R> The type of the record.
     */
    public static <R extends Record> Constructor<R> getCanonicalConstructorOfRecord(Class<R> recordClass) {
        Class<?>[] componentTypes = Arrays.stream(recordClass.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);
        try {
            return recordClass.getDeclaredConstructor(componentTypes);
        } catch (NoSuchMethodException e) {
            StringBuilder builder = new StringBuilder();
            for (var type : componentTypes) {
                builder.append(type.getName()).append(", ");
            }
            throw new RecordConstructorNotFoundException(recordClass,
                    "Attempted to get constructor with signature: %s(%s)".formatted(
                            recordClass.getName(), builder.substring(0, builder.length() - 1)), e);
        }
    }

    /**
     * Gets all constructors of the target record, annotated with {@link BeanRecordConstruct @RecordConstruct}, without the option
     * constructMode = {@link BeanRecordConstructMode#EXCLUDE}.
     * @param recordClass The record-class of which the constructors will be retrieved.
     * @return The collection of constructors annotated with the @RecordConstruct-annotation.
     * @param <R> The type of the record-class.
     */
    public static <R> Collection<Constructor<R>> getConstructorsAnnotatedWithRecordConstruct(Class<R> recordClass) {
        return Arrays.stream(recordClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(BeanRecordConstruct.class)
                        && constructor.getAnnotation(BeanRecordConstruct.class).constructMode() != BeanRecordConstructMode.EXCLUDE)
                .map(constructor -> (Constructor<R>) constructor)
                .toList();
    }
}
