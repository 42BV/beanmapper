package io.beanmapper.utils.provider;

import java.util.Collections;
import java.util.List;

public class ListProvider implements Provider<List>{
    @Override
    public List getDefault() {
        return Collections.emptyList();
    }

    @Override
    public List getMaximum() {
        return Collections.emptyList();
    }
}
