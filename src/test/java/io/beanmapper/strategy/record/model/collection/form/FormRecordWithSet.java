package io.beanmapper.strategy.record.model.collection.form;

import java.util.Set;

public record FormRecordWithSet(int id, String name, Set<String> numbers) {
}
