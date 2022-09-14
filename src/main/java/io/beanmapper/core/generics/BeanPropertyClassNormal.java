package io.beanmapper.core.generics;

import java.lang.reflect.Type;
import java.util.Objects;

class BeanPropertyClassNormal extends AbstractBeanPropertyClass {

    private final Type type;

    BeanPropertyClassNormal(Type type) {
        this.type = type;
    }

    @Override
    public Class<?> getBasicType() {
        return (Class<?>)type;
    }

    @Override
    protected Type[] getGenericTypes() {
        return new Type[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BeanPropertyClassNormal that))
            return false;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
