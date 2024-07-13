package io.beanmapper.core.inspector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.annotations.model.bean_property.Person;
import io.beanmapper.annotations.model.bean_property.PersonResult;
import io.beanmapper.core.BeanPropertyMatchupDirection;
import io.beanmapper.core.BeanPropertyWrapper;
import io.beanmapper.core.inspector.models.bean_property_selector.BeanPropertyImpl;
import io.beanmapper.exceptions.DuplicateBeanPropertyTargetException;
import io.beanmapper.exceptions.FieldShadowingException;
import io.beanmapper.utils.BeanMapperTraceLogger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class BeanPropertySelectorTest {

    private BeanPropertySelector selector;

    @BeforeEach
    void setUp() {
        selector = spy(new BeanPropertySelector());
    }

    @Test
    void testHasAccessibleWriteMethodReturnsTrueWhenAvailable() {
        assertTrue(assertDoesNotThrow(() -> selector.hasAccessibleWriteMethod(Person.class, "name")));
    }

    @Test
    void testHasAccessibleWriteMethodReturnsFalseWhenNotAvailable() {
        assertFalse(assertDoesNotThrow(() -> selector.hasAccessibleWriteMethod(Person.class, "noName")));
    }

    @Test
    void testHasAccessibleWriteMethodReturnsFalseWhenGivenNullClass() {
        assertFalse(assertDoesNotThrow(() -> selector.hasAccessibleWriteMethod(null, "")));
    }

    @Test
    void testHasAccessibleWriteMethodReturnsFalseWhenGivenNullName() {
        assertFalse(assertDoesNotThrow(() -> selector.hasAccessibleWriteMethod(Person.class, null)));
    }

    @Test
    void testGetBeanPropertyNameWithNameProperty() {
        BeanProperty beanProperty = new BeanPropertyImpl("Henk", "Jan", new Class[] { Person.class });
        assertEquals("Henk", selector.getBeanPropertyName(beanProperty));
    }

    @Test
    void testGetBeanPropertyNameWithBlankName() {
        BeanProperty beanProperty = new BeanPropertyImpl("", "Jan", new Class[] { Person.class });
        assertEquals("Jan", selector.getBeanPropertyName(beanProperty));
    }

    @Test
    void testHasBeanPropertyReturnsTrueBeanPropertyAvailable() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(true);
        assertTrue(selector.hasBeanProperty(accessor));
    }

    @Test
    void testHasBeanPropertyReturnsTrueWhenBeanPropertiesAvailable() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(false);
        when(accessor.isAnnotationPresent(BeanProperty.BeanProperties.class)).thenReturn(true);
        assertTrue(selector.hasBeanProperty(accessor));
    }

    @Test
    void testHasBeanPropertyReturnsFalseBeanPropertyNotAvailable() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(false);
        when(accessor.isAnnotationPresent(BeanProperty.BeanProperties.class)).thenReturn(false);
        assertFalse(selector.hasBeanProperty(accessor));
    }

    @Test
    void testHasValidTargetReturnsTrueWhenOtherTypeInArray() {
        assertTrue(selector.hasValidTarget(Person.class, new Class[] { Person.class }));
    }

    @Test
    void testHasValidTargetReturnsFalseWhenOtherTypeNotInArray() {
        assertFalse(selector.hasValidTarget(Person.class, new Class[] { PersonResult.class }));
    }

    @Test
    void testHasValidTargetReturnsTrueWhenArrayContainsVoid() {
        assertTrue(selector.hasValidTarget(Person.class, new Class[] { Void.class }));
    }

    @Test
    void testGetUnhandledBeanPropertyReturnsNullWhenNoValidTargetExists() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(true);

        BeanProperty beanProperty = new BeanPropertyImpl("Henk", "Jan", new Class[] { String.class });
        when(accessor.findAnnotation(BeanProperty.class)).thenReturn(beanProperty);

        assertNull(selector.getUnhandledBeanProperty(accessor, Person.class));
    }

    @Test
    void testGetUnhandledBeanPropertyReturnsBeanPropertyWhenValidTargetExists() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(true);

        BeanProperty beanProperty = new BeanPropertyImpl("Henk", "Jan", new Class[] { Person.class });
        when(accessor.findAnnotation(BeanProperty.class)).thenReturn(beanProperty);

        assertEquals(beanProperty, selector.getUnhandledBeanProperty(accessor, Person.class));
    }

    @Test
    void testGetUnhandledBeanPropertyReturnsBeanPropertyWhenBeanPropertiesPresent() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(false);
        when(accessor.isAnnotationPresent(BeanProperty.BeanProperties.class)).thenReturn(true);

        BeanProperty beanProperty = new BeanPropertyImpl("Henk", "Jan", new Class[] { Person.class });
        BeanProperty.BeanProperties beanProperties = mock(BeanProperty.BeanProperties.class);
        when(beanProperties.value()).thenReturn(new BeanProperty[] { beanProperty });
        when(accessor.findAnnotation(BeanProperty.BeanProperties.class)).thenReturn(beanProperties);

        assertEquals(beanProperty, selector.getUnhandledBeanProperty(accessor, Person.class));
    }

    @Test
    void testGetUnhandledBeanPropertyReturnsNullWhenNoBeanPropertyExists() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.isAnnotationPresent(BeanProperty.class)).thenReturn(false);
        when(accessor.isAnnotationPresent(BeanProperty.BeanProperties.class)).thenReturn(false);

        assertNull(selector.getUnhandledBeanProperty(accessor, Person.class));
    }

    @Test
    void testIsFieldAccessibleReturnsTrueWhenFieldIsPublic() {
        Field field = mock(Field.class);
        when(field.getModifiers()).thenReturn(Modifier.PUBLIC);
        assertTrue(assertDoesNotThrow(() -> selector.isFieldAccessible(field)));
    }

    @Test
    void testIsFieldAccessibleReturnsFalseWhenFieldIsPrivateWithNoAccessor() {
        Field field = mock(Field.class);
        when(field.getModifiers()).thenReturn(Modifier.PRIVATE);
        when(field.getDeclaringClass()).thenReturn(null);
        assertFalse(assertDoesNotThrow(() -> selector.isFieldAccessible(field)));
    }

    @Test
    void testFieldNameNotEqualToAccessorNameReturnsTrueWhenNotTheSame() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        Field field = mock(Field.class);

        when(field.getName()).thenReturn("fieldName");
        when(accessor.getName()).thenReturn("accessorName");

        assertTrue(selector.isFieldNameNotEqualToAccessorName(accessor, field));
    }

    @Test
    void testFieldNameNotEqualToAccessorNameReturnsFalseWhenEqual() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        Field field = mock(Field.class);

        when(field.getName()).thenReturn("name");
        when(accessor.getName()).thenReturn("name");

        assertFalse(selector.isFieldNameNotEqualToAccessorName(accessor, field));
    }

    @Test
    void testDetermineRelevantBeanPropertyForBeanMatchThrowsWhenRelevantBeanPropertiesSizeGreaterThanOne() {
        BeanProperty beanProperty1 = mock(BeanProperty.class);
        BeanProperty beanProperty2 = mock(BeanProperty.class);

        Class<?>[] targets = new Class[] { String.class };

        when(beanProperty1.targets()).thenReturn(targets);
        when(beanProperty2.targets()).thenReturn(targets);

        BeanProperty.BeanProperties beanProperties = mock(BeanProperty.BeanProperties.class);
        when(beanProperties.value()).thenReturn(new BeanProperty[] { beanProperty1, beanProperty2 });

        assertThrows(DuplicateBeanPropertyTargetException.class, () -> selector.determineRelevantBeanPropertyForBeanMatch(beanProperties, String.class));
    }

    @Test
    void testDetermineRelevantBeanPropertyForBeanMatchReturnsNullWhenNoRelevantBeanPropertyAndNoWildcard() {
        BeanProperty.BeanProperties beanProperties = mock(BeanProperty.BeanProperties.class);
        when(beanProperties.value()).thenReturn(new BeanProperty[] {});

        assertNull(selector.determineRelevantBeanPropertyForBeanMatch(beanProperties, String.class));
    }

    @Test
    void testDetermineRelevantBeanPropertyForBeanMatchThrowsWhenMultipleWildcards() {
        BeanProperty beanProperty1 = mock(BeanProperty.class);
        BeanProperty beanProperty2 = mock(BeanProperty.class);

        when(beanProperty1.targets()).thenReturn(new Class[] { Void.class });
        when(beanProperty2.targets()).thenReturn(new Class[] { Void.class });

        BeanProperty.BeanProperties beanProperties = mock(BeanProperty.BeanProperties.class);
        when(beanProperties.value()).thenReturn(new BeanProperty[] { beanProperty1, beanProperty2 });

        assertThrows(DuplicateBeanPropertyTargetException.class, () -> selector.determineRelevantBeanPropertyForBeanMatch(beanProperties, String.class));
    }

    @Test
    void testDetermineRelevantBeanPropertyForBeanMatchReturnsWildcardWhenNoRelevantBean() {
        BeanProperty beanProperty1 = mock(BeanProperty.class);
        BeanProperty beanProperty2 = mock(BeanProperty.class);

        when(beanProperty1.targets()).thenReturn(new Class[] { String.class });
        when(beanProperty2.targets()).thenReturn(new Class[] { Void.class });

        BeanProperty.BeanProperties beanProperties = mock(BeanProperty.BeanProperties.class);
        when(beanProperties.value()).thenReturn(new BeanProperty[] { beanProperty1, beanProperty2 });

        assertEquals(beanProperty2, selector.determineRelevantBeanPropertyForBeanMatch(beanProperties, Person.class));
    }

    @Test
    void testDetermineRelevantBeanPropertyForBeanMatchReturnsRelevantBeanProperty() {
        BeanProperty beanProperty1 = mock(BeanProperty.class);
        BeanProperty beanProperty2 = mock(BeanProperty.class);

        when(beanProperty1.targets()).thenReturn(new Class[] { String.class });
        when(beanProperty2.targets()).thenReturn(new Class[] { Person.class });

        BeanProperty.BeanProperties beanProperties = mock(BeanProperty.BeanProperties.class);
        when(beanProperties.value()).thenReturn(new BeanProperty[] { beanProperty1, beanProperty2 });

        assertEquals(beanProperty2, selector.determineRelevantBeanPropertyForBeanMatch(beanProperties, Person.class));
    }

    @Test
    void testDealWithBeanPropertyReturnsDefaultWrapperWhenNoBeanPropertyExists() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.getName()).thenReturn("henk");

        when(selector.getUnhandledBeanProperty(accessor, Object.class)).thenReturn(null);

        BeanPropertyWrapper wrapper = new BeanPropertyWrapper(accessor.getName());
        assertEquals(wrapper, selector.dealWithBeanProperty(null, null, null, accessor));
    }

    @Test
    void testDealWithBeanPropertyThrowsWhenFieldShadowingDetected() {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(accessor.getName()).thenReturn("test");

        BeanProperty beanProperty = mock(BeanProperty.class);
        when(selector.getUnhandledBeanProperty(accessor, Void.class)).thenReturn(beanProperty);

        doThrow(FieldShadowingException.class).when(selector).detectBeanPropertyFieldShadowing(accessor, beanProperty);

        assertThrows(FieldShadowingException.class, () -> selector.dealWithBeanProperty(null, null, Void.class, accessor));
    }

    @Test
    void testDealWithBeanPropertyLogsErrorWhenBeanNoSuchPropertyExceptionIsThrown() {
        try (MockedStatic<BeanMapperTraceLogger> logger = mockStatic(BeanMapperTraceLogger.class); MockedStatic<PropertyAccessors> accessors = mockStatic(
                PropertyAccessors.class)) {
            var accessor = mock(PropertyAccessor.class);
            when(accessor.getName()).thenReturn("test");

            var beanProperty = mock(BeanProperty.class);
            when(beanProperty.name()).thenReturn("test");
            when(selector.getUnhandledBeanProperty(accessor, String.class)).thenReturn(beanProperty);
            doNothing().when(selector).detectBeanPropertyFieldShadowing(accessor, beanProperty);

            accessors.when(() -> PropertyAccessors.findProperty(any(), anyString())).thenReturn(null);

            var result = selector.dealWithBeanProperty(BeanPropertyMatchupDirection.SOURCE_TO_TARGET, new HashMap<>(), String.class, accessor);
            var expected = new BeanPropertyWrapper(accessor.getName());
            expected.setMustMatch();
            assertEquals(expected, result);

            logger.verify(() -> BeanMapperTraceLogger.log(anyString(), anyString(), anyString()), timeout(1));
        }
    }

    @Test
    void testDetectBeanPropertyFieldShadowingShouldDetectBeanPropertyFieldShadowing() throws ClassNotFoundException {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        String beanPropertyName = "name";

        doReturn(beanPropertyName).when(selector).getBeanPropertyName(beanProperty);

        when(accessor.getDeclaringClass()).thenReturn((Class<Object>) Class.forName("io.beanmapper.annotations.model.bean_property.Person"));

        doReturn(true).when(selector).shadowsField(any(), any(), any());

        doReturn(true).when(selector).hasBeanPropertyAnnotationAndOtherPropertyName(any(), any());

        assertThrows(FieldShadowingException.class, () -> selector.detectBeanPropertyFieldShadowing(accessor, beanProperty));
    }

    @Test
    void testDetectBeanPropertyFieldShadowingShouldNotDetectBeanPropertyFieldShadowingWhenNotShadowsFieldAndNotHasBeanPropertyAndOtherPropertyName() throws ClassNotFoundException {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        String beanPropertyName = "name";

        doReturn(beanPropertyName).when(selector).getBeanPropertyName(beanProperty);

        when(accessor.getDeclaringClass()).thenReturn((Class<Object>) Class.forName("io.beanmapper.annotations.model.bean_property.Person"));

        doReturn(false).when(selector).shadowsField(any(), any(), any());

        doReturn(false).when(selector).hasBeanPropertyAnnotationAndOtherPropertyName(any(), any());

        assertDoesNotThrow(() -> selector.detectBeanPropertyFieldShadowing(accessor, beanProperty));
    }

    @Test
    void testDetectBeanPropertyFieldShadowingShouldNotDetectBeanPropertyFieldShadowingWhenNotHasBeanPropertyAndOtherBeanPropertyName() throws ClassNotFoundException {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        String beanPropertyName = "name";

        doReturn(beanPropertyName).when(selector).getBeanPropertyName(beanProperty);

        when(accessor.getDeclaringClass()).thenReturn((Class<Object>) Class.forName("io.beanmapper.annotations.model.bean_property.Person"));

        doReturn(true).when(selector).shadowsField(any(), any(), any());

        doReturn(false).when(selector).hasBeanPropertyAnnotationAndOtherPropertyName(any(), any());

        assertDoesNotThrow(() -> selector.detectBeanPropertyFieldShadowing(accessor, beanProperty));
    }

    @Test
    void testDetectBeanPropertyFieldShadowingShouldNotDetectBeanPropertyFieldShadowingWhenNotShadowsField() throws ClassNotFoundException {
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        String beanPropertyName = "name";

        doReturn(beanPropertyName).when(selector).getBeanPropertyName(beanProperty);

        when(accessor.getDeclaringClass()).thenReturn((Class<Object>) Class.forName("io.beanmapper.annotations.model.bean_property.Person"));

        doReturn(false).when(selector).shadowsField(any(), any(), any());

        doReturn(true).when(selector).hasBeanPropertyAnnotationAndOtherPropertyName(any(), any());

        assertDoesNotThrow(() -> selector.detectBeanPropertyFieldShadowing(accessor, beanProperty));
    }

    @Test
    void testHasBeanPropertyAnnotationAndOtherPropertyName() {
        Field field = mock(Field.class);
        when(field.isAnnotationPresent(BeanProperty.class)).thenReturn(false);

        assertTrue(selector.hasBeanPropertyAnnotationAndOtherPropertyName(field, "test"));
    }

    @Test
    void testHasBeanPropertyAnnotationAndOtherPropertyNameShouldReturnTrueWhenHasBeanPropertyAndSamePropertyName() {
        Field field = mock(Field.class);
        BeanProperty beanProperty = mock(BeanProperty.class);
        when(beanProperty.name()).thenReturn("test");
        when(field.isAnnotationPresent(BeanProperty.class)).thenReturn(true);
        when(field.getName()).thenReturn("test");
        when(field.getAnnotation(BeanProperty.class)).thenReturn(beanProperty);

        assertTrue(selector.hasBeanPropertyAnnotationAndOtherPropertyName(field, "test"));
    }

    @Test
    void testShadowsFieldShouldReturnTrueWhenAllTrueAndDoesNotHaveBeanIgnored() throws IntrospectionException {
        String name = "test";

        Field field = mock(Field.class);
        when(selector.isFieldAccessible(field)).thenReturn(true);
        when(field.isAnnotationPresent(BeanIgnore.class)).thenReturn(false);
        when(field.getName()).thenReturn(name);

        PropertyAccessor accessor = mock(PropertyAccessor.class);

        when(selector.isFieldNameNotEqualToAccessorName(accessor, field)).thenReturn(true);
        assertTrue(selector.shadowsField(accessor, field, name));
    }

    @Test
    void testShadowsFieldShouldReturnFalseWhenFieldInaccessible() throws IntrospectionException {
        String name = "test";

        Field field = mock(Field.class);
        when(selector.isFieldAccessible(field)).thenReturn(false);
        when(field.isAnnotationPresent(BeanIgnore.class)).thenReturn(true);
        when(field.getName()).thenReturn(name);

        PropertyAccessor accessor = mock(PropertyAccessor.class);

        when(selector.isFieldNameNotEqualToAccessorName(accessor, field)).thenReturn(false);
        assertFalse(selector.shadowsField(accessor, field, name));
    }

    @Test
    void testShadowsFieldShouldReturnFalseWhenFieldNameNotEqualToBeanPropertyName() throws IntrospectionException {
        String name = "test";

        Field field = mock(Field.class);
        when(selector.isFieldAccessible(field)).thenReturn(true);
        when(field.isAnnotationPresent(BeanIgnore.class)).thenReturn(true);
        when(field.getName()).thenReturn("differentName");

        PropertyAccessor accessor = mock(PropertyAccessor.class);

        when(selector.isFieldNameNotEqualToAccessorName(accessor, field)).thenReturn(false);
        assertFalse(selector.shadowsField(accessor, field, name));
    }

    @Test
    void testShadowsFieldShouldThrowRuntimeExceptionWhenIntrospectionExceptionIsCaught() throws IntrospectionException {
        Field field = mock(Field.class);
        PropertyAccessor accessor = mock(PropertyAccessor.class);
        when(field.getName()).thenReturn("test");
        when(accessor.getName()).thenReturn("test");
        doThrow(IntrospectionException.class).when(selector).isFieldAccessible(field);
        assertThrows(RuntimeException.class, () -> selector.shadowsField(accessor, field, ""));
    }

    @Test
    void testShadowsFieldShouldReturnFalseWhenFieldNameIsEqualToAccessorName() throws IntrospectionException {
        String name = "test";

        Field field = mock(Field.class);
        when(selector.isFieldAccessible(field)).thenReturn(true);
        when(field.isAnnotationPresent(BeanIgnore.class)).thenReturn(true);
        when(field.getName()).thenReturn("test");

        PropertyAccessor accessor = mock(PropertyAccessor.class);

        when(selector.isFieldNameNotEqualToAccessorName(accessor, field)).thenReturn(false);
        assertFalse(selector.shadowsField(accessor, field, name));
    }
}
