package io.beanmapper.testmodel.optional_getter;

import java.util.Map;
import java.util.Optional;

public class EntityWithMap {

    public String value;

    public Map<String, EntityWithMap> children;

    public Optional<Map<String, EntityWithMap>> getChildren() {
        return Optional.ofNullable(children);
    }

}
