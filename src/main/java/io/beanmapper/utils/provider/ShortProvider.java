package io.beanmapper.utils.provider;

public class ShortProvider implements Provider<Short> {
    @Override
    public Short getDefault() {
        return (short) 0;
    }

    @Override
    public Short getMaximum() {
        return Short.MAX_VALUE;
    }
}
