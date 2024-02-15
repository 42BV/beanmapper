package io.beanmapper.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.exceptions.BeanInstantiationException;
import io.beanmapper.exceptions.RecordConstructorConflictException;
import io.beanmapper.exceptions.RecordNoAvailableConstructorsExceptions;
import io.beanmapper.exceptions.SourceFieldAccessException;
import io.beanmapper.utils.BeanMapperTraceLogger;
import io.beanmapper.utils.Records;

/**
 * MapToRecordStrategy offers a comprehensive implementation of the MapToClassStrategy, targeted towards mapping a class
 * to a record.
 *
 * <p>A class (or record) can be mapped to a target record, simply by calling the map-method associated with this class.
 * </p><p>Internally, the map-method will create dynamic classes as needed, to create suitable intermediaries. At least
 * one, and at most two, intermediary classed can be generated. For class-generation, the
 * {@link io.beanmapper.dynclass.ClassGenerator}-class is used.</p>
 */
public final class MapToRecordStrategy extends MapToClassStrategy {

    MapToRecordStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public <S, T> T map(final S source) {

        Class<T> targetClass = this.getConfiguration().getTargetClass();

        if (source.getClass().equals(targetClass))
            return targetClass.cast(source);

        // We use the RecordToAny-converter in case the source is also a Record. Furthermore, allowing the use of custom
        // converters increases flexibility of the library.
        if (getConfiguration().isConverterChoosable()) {
            BeanConverter converter = getConverterOptional(source.getClass(), this.getConfiguration().getTargetClass());
            if (converter != null) {
                BeanMapperTraceLogger.log("Converter called for source of class {}, while mapping to class {}\t{}->", source.getClass(), targetClass,
                        converter.getClass().getSimpleName());
                return converter.convert(getBeanMapper(), source, targetClass, null);
            }
        }

        Map<String, Field> sourceFields = getSourceFields(source);
        Constructor<T> constructor = (Constructor<T>) getSuitableConstructor(sourceFields, targetClass);
        String[] fieldNamesForConstructor = getNamesOfConstructorParameters(targetClass, constructor);
        List<Object> values = getValuesOfFields(source, sourceFields, sourceFields, Arrays.stream(fieldNamesForConstructor));

        return targetClass.cast(constructTargetObject(constructor, values));
    }

    /**
     * Gets a Map&lt;String, Field&gt;, containing the fields of the class, mapped by the name of the field, or the value of the BeanAlias-annotation if it is
     * present.
     *
     * @param sourceClass The class of the source-object.
     * @param <S>         The type of the sourceClass.
     * @return A Map containing the fields of the source-class, mapped by the name of the field, or the value of an available BeanAlias.
     */
    private <S> Map<String, Field> getFieldsOfClass(final Class<S> sourceClass) {
        return Arrays.stream(sourceClass.getDeclaredFields())
                .map(field -> {
                    if (field.isAnnotationPresent(BeanAlias.class)) {
                        return Map.entry(field.getAnnotation(BeanAlias.class).value(), field);
                    }
                    return Map.entry(field.getName(), field);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the fields in the source-object, mapped to their name, or, if present, the value of the
     * BeanAlias-annotation.
     *
     * @param source The source-object, of which the fields will be mapped.
     * @param <S>
     * @return The fields of the source-object, mapped to the
     */
    private <S> Map<String, Field> getSourceFields(final S source) {
        Map<String, Field> sourceFields = new HashMap<>();
        Class<? super S> sourceClass = (Class<? super S>) source.getClass();
        while (!sourceClass.equals(Object.class)) {
            sourceFields.putAll(getFieldsOfClass(sourceClass));
            sourceClass = sourceClass.getSuperclass();
        }
        return sourceFields;
    }

    /**
     * Gets the names of the RecordComponents as a String-array.
     *
     * <p>The names of the RecordComponents are retrieved either from the {@link RecordComponent#getName()}-method, or
     * from an available {@link io.beanmapper.annotations.BeanProperty BeanProperty}-annotation.</p>
     *
     * @param targetClass The class of the target record.
     * @param <T>         The type of the target record.
     * @return The names of the RecordComponents as a String-array.
     */
    private <T> String[] getNamesOfRecordComponents(final Class<T> targetClass) {
        return Arrays.stream(targetClass.getRecordComponents())
                .map(recordComponent -> {
                    if (recordComponent.isAnnotationPresent(io.beanmapper.annotations.BeanProperty.class)) {
                        return recordComponent.getAnnotation(io.beanmapper.annotations.BeanProperty.class).value();
                    }
                    return recordComponent.getName();
                })
                .toArray(String[]::new);
    }

    /**
     * Gets the names of constructor parameters.
     *
     * <p>Prefers to use the RecordConstruct-annotation, if it is present. Otherwise, it will use the RecordComponents of the record, to determine the names and
     * order of the parameters.</p>
     *
     * @param targetClass The target record.
     * @param constructor The target constructor.
     * @param <T>         The type of the target class.
     * @return The String-array containing the names of the constructor-parameters.
     */
    private <T> String[] getNamesOfConstructorParameters(final Class<T> targetClass, final Constructor<T> constructor) {
        if (constructor.isAnnotationPresent(BeanRecordConstruct.class)) {
            return constructor.getAnnotation(BeanRecordConstruct.class).value();
        }

        // We can only use this in cases where the compiler added the parameter-name to the classfile. If the name is
        // present, we use the parameter-names, otherwise we use the names as set in the record-components.
        var parameters = constructor.getParameters();
        if (constructor.getParameters()[0].isNamePresent()) {
            return Arrays.stream(parameters)
                    .map(Parameter::getName)
                    .toArray(String[]::new);
        }
        return getNamesOfRecordComponents(targetClass);
    }

    private <S> List<Object> getValuesOfFields(final S source, final Map<String, Field> sourceFields, final Map<String, Field> fieldMap,
            final Stream<String> fieldNamesForConstructor) {
        return fieldNamesForConstructor.map(fieldName -> {
                    Field field = fieldMap.get(fieldName);
                    if (field != null) {
                        return field;
                    }
                    return sourceFields.get(fieldName);
                }).map(field -> getValueFromField(source, field))
                .toList();
    }

    private Object[] getConstructorArgumentsMappedToCorrectTargetType(final Parameter[] parameters, final List<Object> values) {
        Object[] arguments = new Object[parameters.length];
        for (var i = 0; i < parameters.length; ++i) {
            if (i >= values.size() || values.get(i) == null) {
                arguments[i] = this.getConfiguration().getDefaultValueForClass(parameters[i].getType());
            } else {
                var parameterType = parameters[i].getType();
                if (Collection.class.isAssignableFrom(parameterType) || Optional.class.isAssignableFrom(parameterType)
                    || Map.class.isAssignableFrom(parameterType)) {
                    arguments[i] = this.getBeanMapper().map(values.get(i), (ParameterizedType) parameters[i].getParameterizedType());
                } else {
                    arguments[i] = values.get(i) != null && parameters[i].getType().equals(values.get(i).getClass()) ?
                            values.get(i) :
                            this.getBeanMapper().wrap().setConverterChoosable(true).build().map(values.get(i), parameterType);
                }
            }
        }
        return arguments;
    }

    private <T> T constructTargetObject(final Constructor<T> targetConstructor, final List<Object> values) {
        try {
            return targetConstructor.newInstance(getConstructorArgumentsMappedToCorrectTargetType(targetConstructor.getParameters(), values));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException(targetConstructor.getDeclaringClass(), e);
        }
    }

    private <S> Object getValueFromField(final S source, final Field field) {
        if (field == null) {
            return null;
        }
        try {
            if (!field.canAccess(source))
                field.setAccessible(true);
            return field.get(source);
        } catch (IllegalAccessException ex) {
            throw new SourceFieldAccessException(this.getConfiguration().getTargetClass(), source.getClass(), "Could not access field " + field.getName(), ex);
        }
    }

    private <T> Constructor<?> getSuitableConstructor(final Map<String, Field> sourceFields, final Class<T> targetClass) {
        List<Constructor<T>> constructors = new ArrayList<>(Records.getConstructorsAnnotatedWithRecordConstruct(targetClass));
        List<Constructor<T>> mandatoryConstructor = constructors.stream()
                .filter(constructor -> constructor.getAnnotation(BeanRecordConstruct.class).constructMode() == BeanRecordConstructMode.FORCE)
                .toList();
        if (!mandatoryConstructor.isEmpty()) {
            if (mandatoryConstructor.size() > 1) {
                throw new RecordConstructorConflictException(constructors);
            }
            return mandatoryConstructor.get(0);
        }
        if (!constructors.isEmpty()) {
            // Once we get to this point, the constructors-list can only contain constructors with the
            // RecordConstructMode.ON_DEMAND-option.
            // Sorts the list in reverse, to make constructor with the most arguments the first to be considered.
            constructors.sort((arg0, arg1) -> Integer.compare(arg1.getParameterCount(), arg0.getParameterCount()));
            return getConstructorWithMostMatchingParameters(constructors, sourceFields).orElse(Records.getCanonicalConstructorOfRecord((Class) targetClass));
        }
        var canonicalConstructor = Records.getCanonicalConstructorOfRecord((Class) targetClass);
        if (canonicalConstructor.isAnnotationPresent(BeanRecordConstruct.class)) {
            var recordConstruct = (BeanRecordConstruct) canonicalConstructor.getAnnotation(BeanRecordConstruct.class);
            if (recordConstruct.constructMode() == BeanRecordConstructMode.EXCLUDE)
                throw new RecordNoAvailableConstructorsExceptions((Class<? extends Record>) targetClass, "All available constructors have been "
                                                                                                         + "annotated with @RecordConstruct(constructMode = RecordConstructMode.EXCLUDE). To enable mapping to the target record, please make at "
                                                                                                         + "least one constructor available.");
        }
        return canonicalConstructor;
    }

    private <T> Optional<Constructor<T>> getConstructorWithMostMatchingParameters(final List<Constructor<T>> constructors,
            final Map<String, Field> sourceFields) {
        for (var constructor : constructors) {
            BeanRecordConstruct recordConstruct = constructor.getAnnotation(BeanRecordConstruct.class);
            List<Field> relevantFields = Arrays.stream(recordConstruct.value())
                    .map(sourceFields::get)
                    .toList();
            if (!relevantFields.contains(null) || recordConstruct.allowNull())
                return Optional.of(constructor);
        }
        return Optional.empty();
    }
}
