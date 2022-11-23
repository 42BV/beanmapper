package io.beanmapper.utils.provider;

public class FloatProvider implements Provider<Float> {
    @Override
    public Float getDefault() {
        return 0f;
    }

    @Override
    public Float getMaximum() {
        return Float.MAX_VALUE;
    }
}
