package io.beanmapper.testmodel.record;

import java.util.Queue;

public record SourceRecordWithQueue(int id, Queue<String> numbers) {
}
