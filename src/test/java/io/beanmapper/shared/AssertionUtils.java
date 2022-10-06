package io.beanmapper.shared;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

/**
 * A collection of methods that can be used to perform frequently used, and often verbose, tests.
 */
public final class AssertionUtils {

    /**
     * Private constructor, used to hide the default public constructor.
     */
    private AssertionUtils() {
    }

    /**
     * Asserts that the given class contains a field with the given name, and returns the corresponding Field-object.
     *
     * @param clazz The class that should contain a field with the given field-name.
     * @param fieldName The name of the field that should be contained within the given class.
     * @return If the assertion succeeds, this method returns the Field-object declared by the given class, with the
     *         given field-name.
     * @param <T> The type of the class that should contain a field with the given name.
     */
    public static <T> Field assertClassContainsField(final Class<T> clazz, final String fieldName) {
        return assertDoesNotThrow(() -> clazz.getDeclaredField(fieldName));
    }

    /**
     * Asserts that the class of the given instance contains a field with the given name.
     *
     * <p>This method utilises the {@link AssertionUtils#assertClassContainsField(Class, String)}-method. Calling
     * this method is equivalent to calling
     * {@code AssertionUtils.assertClassContainsField(instance.getClass(), fieldName);}</p>
     *
     * @param instance The instance, of which the class should contain a field with the given name.
     * @param fieldName The name of the field that should be present in the class of the given instance.
     * @return The Field-object with the given name, contained within the class of the given instance.
     * @param <T> The type of the instance.
     */
    public static <T> Field assertInstanceContainsField(final T instance, final String fieldName) {
        return assertClassContainsField(instance.getClass(), fieldName);
    }

    /**
     * Asserts that the given class does not contain a field with the given name, and returns the NoSuchFieldException,
     * that should be returned by the assertion.
     *
     * @param clazz The class that should not contain a field with the given name.
     * @param fieldName The field-name that should not be present in the given class.
     * @return The NoSuchFieldException that should be returned by the assertion.
     * @param <T> The type of the class that should not contain a field with the given name.
     */
    public static <T> NoSuchFieldException assertClassDoesNotContainField(final Class<T> clazz, final String fieldName) {
        return assertThrows(NoSuchFieldException.class, () -> clazz.getDeclaredField(fieldName));
    }

    /**
     * Asserts that the class of the given instance does not contain a field with the given name.
     *
     * <p>This method utilises the {@link AssertionUtils#assertClassDoesNotContainField(Class, String)}-method. Calling
     * this method is equivalent to calling
     * <code>AssertionUtils.assertClassDoesNotContainField(instance.getClass(), fieldName);</code></p>
     *
     * @param instance The instance, of which the class should not contain a field with the given name.
     * @param fieldName The name that should not correspond to a field in the class of the given instance.
     * @return The NoSuchFieldException that should be returned by the assertion.
     * @param <T> The type of the instance.
     */
    public static <T> NoSuchFieldException assertInstanceDoesNotContainField(final T instance, final String fieldName) {
        return assertClassDoesNotContainField(instance.getClass(), fieldName);
    }

    /**
     * Asserts that the given instance contains a field with the given name, of which the value equals the given value.
     *
     * <p>This method utilises the {@link ReflectionUtils#getFieldWithName(Class, String)}-method to get the field in
     * the class of the given instance, with the name corresponding to the given name.</p><p>Additionally, it utilises
     * the {@link ReflectionUtils#getValueOfField(Object, Field)}-method to get the value of the field retrieved by the
     * aforementioned ReflectionUtils#getFieldWithName(Class, String)-method.</p><p>Lastly, the method asserts that the
     * value retrieved by the aforementioned ReflectionUtils#getValueOfField(Object, Field)-method equals the value
     * passed to this method.</p>
     *
     * @param instance
     * @param fieldName
     * @param value
     * @param <T>
     * @param <V>
     */
    public static <T, V> void assertFieldWithNameHasValue(final T instance, final String fieldName, final V value) {
        assertEquals(value, ReflectionUtils.getValueOfField(instance, ReflectionUtils.getFieldWithName(instance.getClass(), fieldName)));
    }

}
