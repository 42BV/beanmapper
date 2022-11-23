package io.beanmapper.utils.provider;

import java.util.Collections;
import java.util.Map;

public class MapProvider implements Provider<Map>{
    @Override
    public Map getDefault() {
        return Collections.emptyMap();
    }

    @Override
    public Map getMaximum() {
        return  Collections.emptyMap();
    }
}
