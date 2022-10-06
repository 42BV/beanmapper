package io.beanmapper.testmodel.record;

import java.util.Map;

public record TargetRecordWithMap(int id, Map<Long, TargetMapElement> fields) {
}
