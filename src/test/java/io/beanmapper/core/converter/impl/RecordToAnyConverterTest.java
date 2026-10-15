package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.core.converter.models.record_to_any.InaccessibleRecordFactory;
import io.beanmapper.core.converter.models.record_to_any.Intermediary;
import io.beanmapper.core.converter.models.record_to_any.IntermediaryWithExtraFields;
import io.beanmapper.core.converter.models.record_to_any.IntermediaryWithStaticNameField;
import io.beanmapper.core.converter.models.record_to_any.NameAndAgeRecord;
import io.beanmapper.core.converter.models.record_to_any.NameRecord;
import io.beanmapper.core.converter.models.record_to_any.ThrowingAccessorRecord;
import io.beanmapper.exceptions.RecordMappingToIntermediaryException;
import io.beanmapper.testmodel.record.PersonRecordWithNestedRecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AccessFlag;

class RecordToAnyConverterTest {

    private RecordToAnyConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new RecordToAnyConverter();
    }

    @Test
    void isMatchTrueIfSourceIsRecord() {
        assertTrue(converter.match(PersonRecordWithNestedRecord.class, Object.class));
    }

    @Test
    void isMatchFalseIfSourceIsNotRecord() {
        assertFalse(converter.match(Object.class, Object.class));
    }

    @Test
    void testCopyFieldsToIntermediaryWithNullValueInMap() {
        var intermediary = new Intermediary();
        var copyFieldsToIntermediary = assertDoesNotThrow(() -> this.converter.getClass().getDeclaredMethod("copyFieldsToIntermediary", Object.class, Map.class));
        copyFieldsToIntermediary.setAccessible(true);
        var map = new HashMap<>();
        map.put("name", null);
        assertDoesNotThrow(() -> copyFieldsToIntermediary.invoke(this.converter, intermediary, map));
    }

    @Test
    void testCopyFieldToIntermediaryWithNullKeyInMapShouldThrowInvocationTargetException() {
        var intermediary = new Intermediary();
        var copyFieldToIntermediary = assertDoesNotThrow(() -> this.converter.getClass().getDeclaredMethod("copyFieldsToIntermediary", Object.class, Map.class));
        copyFieldToIntermediary.setAccessible(true);
        var map = new HashMap<>();
        map.put(null, "");
        var exception = assertThrows(InvocationTargetException.class, () -> copyFieldToIntermediary.invoke(this.converter, intermediary, map));
        assertEquals(NullPointerException.class, exception.getCause().getClass());
    }

    @Test
    void testConvertRecordToClass() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        var result = this.converter.convert(beanMapper, new NameRecord("Henk"), Intermediary.class, null);
        assertEquals("Henk", result.name);
    }

    @Test
    void testCopyRecordComponentsToIntermediaryIgnoresFieldsThatAreNotRecordComponents() throws Exception {
        var intermediary = new IntermediaryWithExtraFields();
        var result = (IntermediaryWithExtraFields) getCopyRecordComponentsToIntermediary().invoke(this.converter, new NameRecord("Henk"), intermediary);
        assertEquals("Henk", result.name);
        assertNull(result.fieldNotInRecord);
    }

    /**
     * Simulates an intermediary class that was instrumented with a static, synthetic field
     * {@code $$exampleSyntheticField} to every class it measures. See issue #224.
     */
    @Test
    void testCopyRecordComponentsToIntermediaryIgnoresSyntheticFieldsAddedByInstrumentation() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(Intermediary.class.getPackageName() + ".InstrumentedIntermediary");
        CtField nameField = new CtField(classPool.get(String.class.getName()), "name", ctClass);
        nameField.setModifiers(AccessFlag.PUBLIC);
        ctClass.addField(nameField);
        CtField syntheticField = new CtField(classPool.get(boolean[].class.getName()), "$$exampleSyntheticField", ctClass);
        syntheticField.setModifiers(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.SYNTHETIC);
        ctClass.addField(syntheticField);
        Class<?> instrumentedClass = ctClass.toClass(Intermediary.class);

        var synthetic = instrumentedClass.getDeclaredField("$$exampleSyntheticField");
        assertTrue(synthetic.isSynthetic());
        assertTrue(Modifier.isStatic(synthetic.getModifiers()));

        var intermediary = instrumentedClass.getConstructor().newInstance();
        var result = getCopyRecordComponentsToIntermediary().invoke(this.converter, new NameRecord("Henk"), intermediary);
        assertEquals("Henk", instrumentedClass.getDeclaredField("name").get(result));
        assertNull(synthetic.get(null));
    }

    @Test
    void testCopyRecordComponentsToIntermediarySkipsRecordComponentsWithoutFieldInIntermediary() throws Exception {
        var intermediary = new Intermediary();
        var result = (Intermediary) getCopyRecordComponentsToIntermediary().invoke(this.converter, new NameAndAgeRecord("Henk", 42), intermediary);
        assertEquals("Henk", result.name);
    }

    @Test
    void testCopyRecordComponentsToIntermediarySkipsStaticFieldWithSameNameAsRecordComponent() throws Exception {
        IntermediaryWithStaticNameField.name = null;
        getCopyRecordComponentsToIntermediary().invoke(this.converter, new NameRecord("Henk"), new IntermediaryWithStaticNameField());
        assertNull(IntermediaryWithStaticNameField.name);
    }

    @Test
    void testCopyRecordComponentsToIntermediarySkipsSyntheticFieldWithSameNameAsRecordComponent() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(Intermediary.class.getPackageName() + ".IntermediaryWithSyntheticNameField");
        CtField nameField = new CtField(classPool.get(String.class.getName()), "name", ctClass);
        nameField.setModifiers(AccessFlag.PUBLIC | AccessFlag.SYNTHETIC);
        ctClass.addField(nameField);
        Class<?> intermediaryClass = ctClass.toClass(Intermediary.class);

        var name = intermediaryClass.getDeclaredField("name");
        assertTrue(name.isSynthetic());
        assertFalse(Modifier.isStatic(name.getModifiers()));

        var intermediary = intermediaryClass.getConstructor().newInstance();
        var result = getCopyRecordComponentsToIntermediary().invoke(this.converter, new NameRecord("Henk"), intermediary);
        assertNull(name.get(result));
    }

    @Test
    void testCopyRecordComponentsToIntermediaryWrapsExceptionThrownByAccessor() throws Exception {
        var copyRecordComponentsToIntermediary = getCopyRecordComponentsToIntermediary();
        var record = new ThrowingAccessorRecord("Henk");
        var intermediary = new Intermediary();
        var exception = assertThrows(InvocationTargetException.class,
                () -> copyRecordComponentsToIntermediary.invoke(this.converter, record, intermediary));
        assertInstanceOf(RecordMappingToIntermediaryException.class, exception.getCause());
        assertInstanceOf(IllegalStateException.class, exception.getCause().getCause());
    }

    @Test
    void testConvertThrowsRecordMappingToIntermediaryExceptionIfRecordIsNotAccessible() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        var record = InaccessibleRecordFactory.create("Henk");
        var exception = assertThrows(RecordMappingToIntermediaryException.class,
                () -> this.converter.convert(beanMapper, record, Intermediary.class, null));
        assertInstanceOf(IllegalAccessException.class, exception.getCause());
    }

    private Method getCopyRecordComponentsToIntermediary() throws NoSuchMethodException {
        var method = this.converter.getClass().getDeclaredMethod("copyRecordComponentsToIntermediary", Record.class, Object.class);
        method.setAccessible(true);
        return method;
    }

}
