package io.beanmapper.testmodel.optional_getter;

import java.util.List;
import java.util.Optional;

public class EntityFormWithOptionalOfOptional {

    public String value;

    public Optional<Optional<List<EntityFormWithOptionalOfOptional>>> children;

}
