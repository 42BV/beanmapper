package io.beanmapper.utils.provider;

public class BooleanProvider implements Provider<Boolean>{
    @Override
    public Boolean getDefault() {
        return false;
    }

    @Override
    public Boolean getMaximum() {
        return true;
    }
}
