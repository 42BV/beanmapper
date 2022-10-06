package io.beanmapper.testmodel.record;

import java.util.Map;

public record SourceRecordWithMap(int id, Map<Long, SourceMapElement> fields) {
}
