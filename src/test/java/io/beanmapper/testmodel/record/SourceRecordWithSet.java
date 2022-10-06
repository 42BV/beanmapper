package io.beanmapper.testmodel.record;

import java.util.Set;

public record SourceRecordWithSet(int id, Set<String> numbers) {
}
