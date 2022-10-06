package io.beanmapper.strategy.record.model.collection.result;

import java.util.Set;

public record ResultRecordWithSet(int id, String name, Set<Integer> numbers) {
}
