package io.beanmapper.testmodel.record;

import java.util.Queue;

public record TargetRecordWithQueue(int id, Queue<Long> numbers) {}
