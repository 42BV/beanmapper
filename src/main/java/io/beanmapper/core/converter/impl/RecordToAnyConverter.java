package io.beanmapper.core.converter.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.exceptions.RecordMappingToIntermediaryException;
import io.beanmapper.utils.Records;

public class RecordToAnyConverter implements BeanConverter {

    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        Map<String, RecordComponent> mappedComponents = new HashMap<>();
        Arrays.stream(source.getClass().getRecordComponents()).forEach(component -> {
            if (component.isAnnotationPresent(BeanAlias.class)) {
                mappedComponents.put(component.getAnnotation(BeanAlias.class).value(), component);
            } else {
                mappedComponents.put(component.getName(), component);
            }
        });

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
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Map<String, Object> fieldMap = new HashMap<>();
        for (var field : intermediary.getClass().getDeclaredFields()) {
            Method getter = recordClass.getClass().getDeclaredMethod(field.getName());
            fieldMap.put(field.getName(), getter.invoke(recordClass));
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
            if (!fieldEntry.getValue().getClass().equals(intermediary.getClass().getDeclaredField(fieldEntry.getKey()).getType())) {
                fieldEntry.setValue(beanMapper.map(fieldEntry.getValue(), intermediary.getClass().getDeclaredField(fieldEntry.getKey()).getType()));
            }
            intermediary.getClass().getDeclaredField(fieldEntry.getKey()).set(intermediary, fieldEntry.getValue());
        }
        return intermediary;
    }
}
