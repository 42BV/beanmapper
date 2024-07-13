package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Setting a name allows you to map the property to a property on the other side with a different
 * name. This annotation can be used on both sides.
 */
@Repeatable(BeanProperty.BeanProperties.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanProperty {

    Class<?> WILDCARD_TYPE = Void.class;

    /**
     * Making this property available makes application code require guard clauses. As such, best to remove this.
     */
    @Deprecated(forRemoval = true)
    String name() default "";

    String value() default "";

    /**
     * Allows the user to specify which mappings this BeanProperty should apply to. When this property contains Void.class, the BeanProperty will apply to any mappings that do not have a BeanProperty specified.
     *
     * @return The mappings to which this BeanProperty should apply.
     */
    Class<?>[] targets() default { Void.class };

    /**
     * Allows the BeanProperty-annotation to be repeated.
     */
    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface BeanProperties {
        BeanProperty[] value();
    }
}
