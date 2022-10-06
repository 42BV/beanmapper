package io.beanmapper.strategy.record.model.collection.result;

import java.util.Queue;

public record ResultRecordWithQueue(int id, String name, Queue<Integer> numbers) {
}
