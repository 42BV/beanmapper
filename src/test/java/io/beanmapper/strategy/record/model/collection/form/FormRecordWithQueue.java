package io.beanmapper.strategy.record.model.collection.form;

import java.util.Queue;

public record FormRecordWithQueue(int id, String name, Queue<String> numbers) {
}
