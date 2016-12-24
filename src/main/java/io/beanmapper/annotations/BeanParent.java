package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a target-side property needs to become the value of its parent, it must be annotated with
 * @BeanParent. The annotation works both on the source and target sides. If the annotation is set,
 * no regular mapping will take place.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanParent {
}
