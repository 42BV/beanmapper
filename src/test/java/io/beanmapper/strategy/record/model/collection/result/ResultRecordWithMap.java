package io.beanmapper.strategy.record.model.collection.result;

import java.util.Map;

public record ResultRecordWithMap(int id, String name, Map<Integer, Integer> numbers) {
}
