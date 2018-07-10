package io.beanmapper.core.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.beanmapper.core.BeanPropertyAccessType;
import io.beanmapper.core.BeanPropertyMatchupDirection;
import io.beanmapper.core.inspector.PropertyAccessor;

/**
 * <p>
 *     Knows the side of the BeanProperty (either source or target) and because
 *     of that knows how the property will be accessed. The way the property will
 *     be accessed, determines its property. For example:
 * </p>
 * <ul>
 *     <li>field; the type of the field determines the class</li>
 *     <li>setter; the first parameter type determines the class</li>
 *     <li>getter; the return type of the getter determines the class</li>
 *     <li></li>
 * </ul>
 * <p>
 *     Also keeps tracks of the bean field (ie, the member variable) if it
 *     exists, in order to allow it to be reused. This is needed for example
 *     for Hibernate collection reuse.
 * </p>
 * @author Robert Bor
 */
public class DirectedBeanProperty {

    /**
     * the class of the property with regards to the way it will be accessed
     * (getter or field for source, setter or field for target)
     */
    private final BeanPropertyClass beanPropertyClass;

    /**
     * If it exists, this will contain the class of the field.
     */
    private final BeanPropertyClass beanFieldClass;

    public DirectedBeanProperty(
            BeanPropertyMatchupDirection matchupDirection,
            PropertyAccessor accessor,
            Class containingClass) {
        BeanPropertyAccessType accessType = matchupDirection.accessType(accessor);
        this.beanPropertyClass = determineBeanClass(accessType, containingClass, accessor);
        this.beanFieldClass = determineBeanClass(BeanPropertyAccessType.FIELD, containingClass, accessor);
    }

    private BeanPropertyClass determineBeanClass(
            BeanPropertyAccessType accessType,
            Class containingClass,
            PropertyAccessor accessor) {
        Type type = accessType.getGenericType(containingClass, accessor);
        if (type == null) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            return new BeanPropertyClassParameterized((ParameterizedType) type);
        } else {
            return new BeanPropertyClassNormal(type);
        }
    }

    public BeanPropertyClass getBeanPropertyClass() {
        return beanPropertyClass;
    }

    public BeanPropertyClass getBeanFieldClass() {
        return beanFieldClass;
    }

    public Class<?> getGenericClassOfProperty(int index) {
        return isBeanFieldAvailable() ?
                beanFieldClass.getParameterizedType(index) :
                beanPropertyClass.getParameterizedType(index);
    }

    public boolean isBeanFieldAvailable() {
        return beanPropertyClass.equals(beanFieldClass);
    }

}
