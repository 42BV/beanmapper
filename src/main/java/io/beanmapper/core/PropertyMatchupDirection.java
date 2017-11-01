package io.beanmapper.core;

import io.beanmapper.core.inspector.PropertyAccessor;

public enum PropertyMatchupDirection {

    SOURCE_TO_TARGET {
        @Override
        public boolean validAccessor(PropertyAccessor accessor) {
            return accessor.isReadable();
        }

        @Override
        public boolean checkFieldForCollectionProperty() {
            return false;
        }
    },
    TARGET_TO_SOURCE {
        @Override
        public boolean validAccessor(PropertyAccessor accessor) {
            return accessor.isWritable();
        }

        @Override
        public boolean checkFieldForCollectionProperty() {
            return true;
        }
    };

    public abstract boolean validAccessor(PropertyAccessor accessor);

    public abstract boolean checkFieldForCollectionProperty();

}
