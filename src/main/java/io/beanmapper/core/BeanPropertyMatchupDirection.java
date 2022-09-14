package io.beanmapper.core;

import io.beanmapper.core.inspector.PropertyAccessor;

public enum BeanPropertyMatchupDirection {

    SOURCE_TO_TARGET {
        @Override
        public BeanPropertyAccessType accessType(PropertyAccessor accessor) {
            if (!accessor.isReadable())
                return BeanPropertyAccessType.NO_ACCESS;
            else if (accessor.getReadMethod() == null)
                return BeanPropertyAccessType.FIELD;
            return BeanPropertyAccessType.GETTER;
        }

        @Override
        public boolean checkFieldForCollectionProperty() {
            return false;
        }
    },
    TARGET_TO_SOURCE {
        @Override
        public BeanPropertyAccessType accessType(PropertyAccessor accessor) {
            if (!accessor.isWritable())
                return BeanPropertyAccessType.NO_ACCESS;
            else if (accessor.getWriteMethod() == null)
                return BeanPropertyAccessType.FIELD;
            return BeanPropertyAccessType.SETTER;
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
