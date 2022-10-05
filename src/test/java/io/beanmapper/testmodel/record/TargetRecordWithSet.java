package io.beanmapper.testmodel.record;

import java.util.Set;

public record TargetRecordWithSet(int id, Set<Long> numbers) {
}
