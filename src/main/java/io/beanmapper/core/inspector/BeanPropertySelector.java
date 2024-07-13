package io.beanmapper.core.inspector;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.core.BeanPropertyCreator;
import io.beanmapper.core.BeanPropertyMatchupDirection;
import io.beanmapper.core.BeanPropertyWrapper;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;
import io.beanmapper.exceptions.DuplicateBeanPropertyTargetException;
import io.beanmapper.exceptions.FieldShadowingException;
import io.beanmapper.utils.BeanMapperTraceLogger;

public class BeanPropertySelector {

    /**
     * Determines whether a field exposes an accessible mutator-method.
     *
     * <p>This method first retrieves the array of PropertyDescriptors, and turns it into a Stream. If any of the
     * PropertyDescriptor-objects have the same name as the field, this method returns true. If not, this method returns
     * false.</p>
     * <p>
     * In this context, an accessible mutator-method is any method which is public and adheres to the Java
     * Beans definition of a mutator.
     *
     * @param clazz     Class containing the field.
     * @param fieldName Name of the field.
     * @return True, if the field exposes an accessible mutator, false otherwise.
     * @throws IntrospectionException May be thrown whenever an Exception occurs during the introspection of the
     *                                relevant bean.
     */
    public <T> boolean hasAccessibleWriteMethod(final Class<T> clazz, String fieldName) throws IntrospectionException {
        if (clazz == null || fieldName == null) {
            return false;
        }

        return Arrays.stream(Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                     .anyMatch(propertyDescriptor -> propertyDescriptor.getName().equals(fieldName));
    }

    public String getBeanPropertyName(BeanProperty annotation) {
        return annotation.name().isBlank() ? annotation.value() : annotation.name();
    }

    private <T> void validateHasNoMoreThanOneRelevantBeanProperty(List<BeanProperty> beanProperties, Class<T> otherType) {
        if (beanProperties.size() > 1) {
            throw new DuplicateBeanPropertyTargetException("(Target: %s)".formatted(otherType.getSimpleName()));
        }
    }

    private void validateHasNoMoreThanOneWildcardBeanProperty(List<BeanProperty> beanProperties) {
        if (beanProperties.size() > 1) {
            throw new DuplicateBeanPropertyTargetException(
                    "(Target: any; Only one BeanProperty may have Void.class (the wildcard-type for BeanProperties) used in its targets-property.)");
        }
    }

    private BeanProperty handlePossibleWildcardBeanProperties(BeanProperty.BeanProperties beanProperties) {
        var wildCardBeanProperties = Arrays.stream(beanProperties.value())
                                           .filter(beanProperty -> Arrays.stream(beanProperty.targets()).anyMatch(clazz -> clazz == BeanProperty.WILDCARD_TYPE))
                                           .toList();
        if (wildCardBeanProperties.isEmpty()) {
            return null;
        }
        validateHasNoMoreThanOneWildcardBeanProperty(wildCardBeanProperties);
        return wildCardBeanProperties.get(0);
    }

    /**
     * Determines which BeanProperty-annotation should be used for the mapping of the current field, based on the type of the class on the opposite side of the mapping.
     * <p>
     * If a single BeanProperty contains the otherType in its targets-property, that BeanProperty will be selected.
     * If none of the BeanProperty-annotation contain the otherType in its targets-property, we look for a BeanProperty with the Void-type in its targets-property.
     * If multiple BeanProperty-annotations contain the otherType in its targets-property, an Exception is thrown, to indicate that it is not possible to determine a BeanProperty to use.
     * If no relevant BeanProperty can be found, this method returns null.
     * </p>
     *
     * @param beanProperties All BeanProperty-annotations applied to the current field.
     * @param otherType      The type of the class on the other side of the mapping.
     * @return The most relevant BeanProperty.
     */
    public BeanProperty determineRelevantBeanPropertyForBeanMatch(
            BeanProperty.BeanProperties beanProperties, Class<?> otherType) {
        List<BeanProperty> relevantBeanProperties = Arrays.stream(beanProperties.value())
                                                          .filter(beanProperty -> Arrays.stream(beanProperty.targets())
                                                                                        .anyMatch(pairedBean -> pairedBean == otherType))
                                                          .toList();

        validateHasNoMoreThanOneRelevantBeanProperty(relevantBeanProperties, otherType);

        if (relevantBeanProperties.isEmpty()) {
            return handlePossibleWildcardBeanProperties(beanProperties);
        }

        return relevantBeanProperties.get(0);
    }

    public boolean hasBeanProperty(PropertyAccessor accessor) {
        return accessor.isAnnotationPresent(BeanProperty.class) || accessor.isAnnotationPresent(BeanProperty.BeanProperties.class);
    }

    public <T> boolean hasValidTarget(Class<T> otherType, Class<?>[] targets) {
        return Arrays.stream(targets).anyMatch(clazz -> clazz == otherType || clazz == Void.class);
    }

    public <T> BeanProperty getUnhandledBeanProperty(PropertyAccessor accessor, Class<T> otherType) {
        if (!hasBeanProperty(accessor)) {
            return null;
        }

        BeanProperty beanProperty;

        if (accessor.isAnnotationPresent(BeanProperty.class)) {
            beanProperty = accessor.findAnnotation(BeanProperty.class);
            return hasValidTarget(otherType, beanProperty.targets()) ? beanProperty : null;
        }

        BeanProperty.BeanProperties beanProperties = accessor.findAnnotation(BeanProperty.BeanProperties.class);
        beanProperty = determineRelevantBeanPropertyForBeanMatch(beanProperties, otherType);

        return beanProperty;
    }

    public BeanPropertyWrapper dealWithBeanProperty(BeanPropertyMatchupDirection matchupDirection, Map<String, io.beanmapper.core.BeanProperty> otherNodes,
            Class<?> otherType, PropertyAccessor accessor) {
        BeanPropertyWrapper wrapper = new BeanPropertyWrapper(accessor.getName());

        BeanProperty beanProperty = getUnhandledBeanProperty(accessor, otherType);

        if (beanProperty == null) {
            return wrapper;
        }

        detectBeanPropertyFieldShadowing(accessor, beanProperty);

        wrapper.setMustMatch();
        wrapper.setName(getBeanPropertyName(beanProperty));
        // Get the other field from the location that is specified in the beanProperty annotation.
        // If the field is referred to by a path, store the custom field in the other map
        try {
            otherNodes.put(wrapper.getName(), new BeanPropertyCreator(matchupDirection.getInverse(), otherType, wrapper.getName()).determineNodesForPath());
        } catch (BeanNoSuchPropertyException err) {
            BeanMapperTraceLogger.log("""
                                              BeanNoSuchPropertyException thrown by BeanMatchStore#dealWithBeanProperty(BeanPropertyMatchupDirection, Map<String, BeanProperty>, Class, PropertyAccessor), for {}.
                                              {}""", wrapper.getName(), err.getMessage());
        }
        return wrapper;
    }

    public void detectBeanPropertyFieldShadowing(final PropertyAccessor accessor, final BeanProperty beanProperty) {
        var beanPropertyName = getBeanPropertyName(beanProperty);
        Arrays.stream(accessor.getDeclaringClass().getDeclaredFields())
              .filter(field -> shadowsField(accessor, field, beanPropertyName))
              .filter(field -> hasBeanPropertyAnnotationAndOtherPropertyName(field, beanPropertyName))
              .findAny()
              .ifPresent(field -> {
                  throw new FieldShadowingException(
                          String.format("%s %s.%s shadows %s.%s.", beanProperty, accessor.getDeclaringClass().getName(), accessor.getName(),
                                        field.getDeclaringClass().getName(), field.getName()
                                       ));
              });
    }

    public boolean hasBeanPropertyAnnotationAndOtherPropertyName(Field field, String beanPropertyName) {
        return !field.isAnnotationPresent(BeanProperty.class) || getBeanPropertyName(field.getAnnotation(BeanProperty.class)).equals(beanPropertyName);
    }

    public boolean shadowsField(PropertyAccessor accessor, Field field, String beanPropertyName) {
        try {
            boolean fieldNameNotEqualToAccessorName = isFieldNameNotEqualToAccessorName(accessor, field);
            boolean isFieldAccessible = isFieldAccessible(field);
            return fieldNameNotEqualToAccessorName && !field.isAnnotationPresent(BeanIgnore.class) && field.getName().equals(beanPropertyName)
                   && isFieldAccessible;
        } catch (IntrospectionException ex) {
            throw new FieldShadowingException("Could verify lack of field shadowing. IntrospectionException: " + ex.getMessage());
        }
    }

    public boolean isFieldAccessible(Field field) throws IntrospectionException {
        return Modifier.isPublic(field.getModifiers()) || hasAccessibleWriteMethod(field.getDeclaringClass(), field.getName());
    }

    public boolean isFieldNameNotEqualToAccessorName(PropertyAccessor accessor, Field field) {
        return !field.getName().equals(accessor.getName());
    }
}
