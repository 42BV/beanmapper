package io.beanmapper.utils.provider;

import java.util.Optional;

public class OprtionalProvider implements Provider<Optional>{
    @Override
    public Optional getDefault() {
        return Optional.empty();
    }

    @Override
    public Optional getMaximum() {
        return Optional.empty();
    }
}
