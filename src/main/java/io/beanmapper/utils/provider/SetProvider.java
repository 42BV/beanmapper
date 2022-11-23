package io.beanmapper.utils.provider;

import java.util.Collections;
import java.util.Set;

public class SetProvider implements Provider<Set> {

    @Override
    public Set getDefault() {
        return Collections.emptySet();
    }

    @Override
    public Set getMaximum() {
        return Collections.emptySet();
    }
}
