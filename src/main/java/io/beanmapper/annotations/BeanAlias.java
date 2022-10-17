package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BeanAlias provides a way for a source-class to indicate which field in the target-class the annotated field should be
 * mapped to.
 *
 * <p>The BeanAlias-annotation works well in concert with the {@link BeanProperty}-annotation.</p>
 *
 * @apiNote While a target-class may contain field annotated with the BeanAlias-annotation, during mapping, those
 *          annotations will be ignored. For equivalent functionality on the target-side, use the
 *          BeanProperty-annotation on the relevant fields.
 * @see BeanProperty
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanAlias {

    /**
     * <p>Returns the name of the field in the target to which the annotated element must be mapped.</p>
     * @return The name of the target field.
     */
    String value();
}
