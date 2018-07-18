package io.beanmapper.core;

import io.beanmapper.core.inspector.PropertyAccessor;

public enum BeanPropertyMatchupDirection {

    SOURCE_TO_TARGET {
        @Override
        public BeanPropertyAccessType accessType(PropertyAccessor accessor) {
            return !accessor.isReadable() ? BeanPropertyAccessType.NO_ACCESS :
                    accessor.getReadMethod() == null ? BeanPropertyAccessType.FIELD : BeanPropertyAccessType.GETTER;
        }

        @Override
        public boolean checkFieldForCollectionProperty() {
            return false;
        }
    },
    TARGET_TO_SOURCE {
        @Override
        public BeanPropertyAccessType accessType(PropertyAccessor accessor) {
            return !accessor.isWritable() ? BeanPropertyAccessType.NO_ACCESS :
                    accessor.getWriteMethod() == null ? BeanPropertyAccessType.FIELD : BeanPropertyAccessType.SETTER;
        }

        @Override
        public boolean checkFieldForCollectionProperty() {
            return true;
        }
    };

    public BeanPropertyMatchupDirection getInverse() {
        return values()[(values().length - 1) - ordinal()];
    }

    public abstract BeanPropertyAccessType accessType(PropertyAccessor accessor);

    public abstract boolean checkFieldForCollectionProperty();

}
