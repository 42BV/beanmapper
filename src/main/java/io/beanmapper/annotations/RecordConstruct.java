package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.RecordComponent;

import io.beanmapper.BeanMapper;
import io.beanmapper.exceptions.RecordConstructorConflictException;
import io.beanmapper.exceptions.RecordNoAvailableConstructorsExceptions;

/**
 * An annotation applicable to {@link Record}-classes, that can be used to force {@link BeanMapper} to use a specific constructor of a
 * record.
 *
 * <p>By default, BeanMapper will use the
 * <a href="https://docs.oracle.com/javase/specs/jls/se16/html/jls-8.html#jls-8.10.4">canonical constructor</a> of a
 * record. However, when a source-class contains fewer fields than the amount of {@link RecordComponent}-objects of the record,
 * it may be wise to declare an overloaded constructor that allows the user to set only those fields that are present in
 * the source, and fills the remaining fields with default values.</p>
 *
 * <p>If a record contains multiple overloaded constructors, this annotation may offer some clarity. By setting the
 * {@link #constructMode}-field of the annotation, the user may give hints on which constructor BeanMapper should use. This is
 * especially useful in cases where BeanMapper should only use a specific constructor, or should exclude certain
 * from consideration.</p>
 *
 * @apiNote <p>Annotating multiple constructors with the {@link RecordConstructMode#FORCE}-option will lead to a
 * {@link RecordConstructorConflictException} being thrown.</p>
 *
 * <p>Annotating a single constructor with {@link RecordConstructMode#FORCE}, and (an)other constructor(s) with
 * {@link RecordConstructMode#ON_DEMAND} will always result in the constructor annotated with the
 * RecordConstructMode.FORCE-option being used. This will, however, not throw an Exception, as it will not cause any
 * issues for BeanMapper.</p>
 *
 * <p>Annotating a constructor with the {@link RecordConstructMode#EXCLUDE}-option, will actively exclude that
 * constructor from consideration.</p>
 *
 * <p>Annotating all constructors with the RecordConstructMode.EXCLUDE-option, will result in a
 * {@link RecordNoAvailableConstructorsExceptions} being thrown.</p>
 *
 * @see RecordConstructMode
 * @see io.beanmapper.strategy.MapToRecordStrategy
 * @see io.beanmapper.utils.Records
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR })
public @interface RecordConstruct {

    /**
     * Should contain the names of the fields that need to be used to invoke the constructor.
     *
     * @return The names of the fields that will be used to invoke the constructor.
     */
    String[] value() default "";

    RecordConstructMode constructMode() default RecordConstructMode.ON_DEMAND;

    boolean allowNull() default false;

}
