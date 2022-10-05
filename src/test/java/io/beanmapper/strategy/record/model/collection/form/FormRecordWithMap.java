package io.beanmapper.strategy.record.model.collection.form;

import java.util.Map;

public record FormRecordWithMap(int id, String name, Map<Integer, String> numbers) {
}
