package io.beanmapper.strategy.record.model.collection.form;

import java.util.List;

public record FormRecordWithList(int id, String name, List<String> numbers) {
}
