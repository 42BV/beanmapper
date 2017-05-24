package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determines the type of the other side in a collection. When this annotation is set, beanmapper
 * logic is activated on it.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanCollection {

    Class elementType();

    Class targetCollectionType() default void.class;

    BeanCollectionUsage beanCollectionUsage() default BeanCollectionUsage.REUSE;
}

// 3 zaken interessant om op te geven:
// * het feit dat een deep inspection moet plaatsvinden OF type source / target expliciet meegeven
//   * overwegen; alleen target meegeven, omdat source al impliciet bepaald is
//   * overwegen; als lijst slim afgehandeld moet worden, dus altijd BeanMapper activeren?
// * het feit dat een lijst nieuw geconstrueerd moet worden (new) + type, OF aanvullen van bestaande lijst
// * of een reset moet plaatsvinden op een lijst (zoals bij aliassen wenselijk is)
