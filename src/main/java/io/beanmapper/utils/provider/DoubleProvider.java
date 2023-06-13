package io.beanmapper.utils.provider;

public class DoubleProvider implements Provider<Double> {
    @Override
    public Double getDefault() {
        return 0d;
    }

    @Override
    public Double getMaximum() {
        return Double.MAX_VALUE;
    }
}
