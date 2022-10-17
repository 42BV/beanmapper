package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BeanConstruct provides a way to indicate that the target-object should be created by passing the values of the
 * specified fields in the source to a suitable constructor in the target.
 *
 * @apiNote <p>Before passing the values to the target constructor, the values must be mapped to the types of the target
 *          fields.</p>
 *          <p>Since JDK 8, Java provides an {@link java.lang.reflect.Executable}-interface. Every
 *          {@link java.lang.reflect.Constructor} - and {@link java.lang.reflect.Method}-object implements the
 *          {@code Executable}-interface. As such, it is possible to get the name and {@link java.lang.reflect.Type} /
 *          {@link java.lang.reflect.ParameterizedType} of each constructor-parameter, without needing to analyse the
 *          entire target-class.</p>
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanConstruct {

    /**
     * Contains the names of the constructor-parameters.
     *
     * <p>Every entry in this array should correspond to the name or {@link BeanAlias}-value of a field in the
     * source-object.</p>
     *
     * @return The names of the constructor-parameters.
     */
    String[] value();

}
