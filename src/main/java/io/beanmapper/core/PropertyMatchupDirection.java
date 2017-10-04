package io.beanmapper.core;

import io.beanmapper.core.inspector.PropertyAccessor;

public enum PropertyMatchupDirection {

    SOURCE_TO_TARGET {
        @Override
        public boolean validAccessor(PropertyAccessor accessor) {
            return accessor.isReadable();
        }
    },
    TARGET_TO_SOURCE {
        @Override
        public boolean validAccessor(PropertyAccessor accessor) {
            return accessor.isWritable();
        }
    };

    public abstract boolean validAccessor(PropertyAccessor accessor);
}
