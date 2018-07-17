package io.beanmapper.core.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

class BeanPropertyClassParameterized extends AbstractBeanPropertyClass {

    private final ParameterizedType type;

    BeanPropertyClassParameterized(ParameterizedType type) {
        this.type = type;
    }

    @Override
    public Class<?> getBasicType() {
        return (Class<?>)type.getRawType();
    }

    @Override
    protected Type[] getGenericTypes() {
        return type.getActualTypeArguments();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BeanPropertyClassParameterized))
            return false;
        BeanPropertyClassParameterized that = (BeanPropertyClassParameterized) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
