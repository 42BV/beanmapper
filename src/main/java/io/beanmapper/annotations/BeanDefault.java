package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a result value of an invoked getter is null, the result will be overwritten by the value. Will only
 * be used if set on the target class.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanDefault {

    /**
     * @deprecated
     */
    @Deprecated(since = "v4.1.0", forRemoval = true)
    String value();

}
