package io.beanmapper.utils.provider;

public class IntegerProvider implements Provider<Integer> {

    @Override
    public Integer getDefault() {
        return 0;
    }

    @Override
    public Integer getMaximum() {
        return Integer.MAX_VALUE;
    }

}
