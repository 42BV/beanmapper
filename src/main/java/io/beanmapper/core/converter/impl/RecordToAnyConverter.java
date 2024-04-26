package io.beanmapper.core.converter.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.exceptions.RecordMappingToIntermediaryException;
import io.beanmapper.utils.DefaultValues;
import io.beanmapper.utils.Records;

public class RecordToAnyConverter implements BeanConverter {

    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        try {
            Class<?> intermediaryClass = beanMapper.getConfiguration().getClassStore().getOrCreateGeneratedClass(source.getClass(),
                    List.of(Records.getRecordFieldNames(
                            (Class<? extends Record>) source.getClass())), null);
            Object intermediaryObject = intermediaryClass.cast(
                    this.copyRecordComponentsToIntermediary((Record) source, intermediaryClass.getConstructors()[0].newInstance()));
            return beanMapper.map(intermediaryObject, targetClass);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            throw new RecordMappingToIntermediaryException(source.getClass(), e);
        }
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.isRecord();
    }

    /**
     * Compiles a map of fields, using the field-names as the key, and calls {@link RecordToAnyConverter#copyFieldsToIntermediary(Object, Map)} in order to map
     * the copy the values of the fields in the recordClass to the intermediary.
     *
     * @param recordClass Record from which the RecordComponents will be copied to the intermediary object.
     * @param intermediary Intermediary object to which the RecordComponents will be mapped.
     * @return The intermediary object to which the RecordComponents have been copied.
     * @param <R> The type of the record class.
     * @param <T> The type of the intermediary class.
     */
    private <R extends Record, T> T copyRecordComponentsToIntermediary(R recordClass, T intermediary)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        Map<String, Object> fieldMap = new HashMap<>();
        for (var field : intermediary.getClass().getDeclaredFields()) {
            MethodHandle methodHandle = MethodHandles.lookup().unreflect(recordClass.getClass().getDeclaredMethod(field.getName()));
            try {
                fieldMap.put(field.getName(), methodHandle.invoke(recordClass));
            } catch (Throwable t) {
                throw new RecordMappingToIntermediaryException(intermediary.getClass(), t);
            }
        }
        return copyFieldsToIntermediary(intermediary, fieldMap);
    }

    /**
     * Copies the values in the fieldMap to the given intermediary object.
     *
     * @param intermediary The intermediary object to which the fields in the fieldMap will be copied.
     * @param fieldMap Contains the fields that will be copied to the intermediary object.
     * @return The intermediary object containing the fields from the fieldMap.
     * @param <T> The type of the intermediary object.
     */
    private <T> T copyFieldsToIntermediary(T intermediary, Map<String, Object> fieldMap) throws NoSuchFieldException, IllegalAccessException {
        BeanMapper beanMapper = new BeanMapperBuilder().setConverterChoosable(true).setUseNullValue().build();
        for (Map.Entry<String, Object> fieldEntry : fieldMap.entrySet()) {
            final Class<?> targetType = intermediary.getClass().getDeclaredField(fieldEntry.getKey()).getType();
            var value = fieldEntry.getValue();
            if (value == null) {
                if (targetType.isPrimitive()) {
                    value = DefaultValues.defaultValueFor(targetType);
                }
            } else if (!value.getClass().equals(targetType)) {
                value = beanMapper.map(value, targetType);
            }
            intermediary.getClass().getDeclaredField(fieldEntry.getKey()).set(intermediary, value);
        }
        return intermediary;
    }
}
