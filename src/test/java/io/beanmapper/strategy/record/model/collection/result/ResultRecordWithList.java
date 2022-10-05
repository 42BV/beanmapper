package io.beanmapper.strategy.record.model.collection.result;

import java.util.List;

public record ResultRecordWithList(int id, String name, List<Integer> numbers) {
}
