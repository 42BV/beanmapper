package io.beanmapper.utils.provider;

public class LongProvider implements Provider<Long>{
    @Override
    public Long getDefault() {
        return 0L;
    }

    @Override
    public Long getMaximum() {
        return Long.MAX_VALUE;
    }
}
