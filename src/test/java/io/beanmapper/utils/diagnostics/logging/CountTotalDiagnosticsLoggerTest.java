package io.beanmapper.utils.diagnostics.logging;

import static io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel.COUNT_TOTAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.config.DiagnosticsConfiguration;
import io.beanmapper.testmodel.record.PersonRecord;
import io.beanmapper.testmodel.record.PersonResultRecord;

import org.junit.jupiter.api.Test;

class CountTotalDiagnosticsLoggerTest extends AbstractDiagnosticsLoggerTest {

    @Test
    void testTotalDiagnosticsLoggerPrintsCorrectCount() {
        String outputTemplate = """
                io.beanmapper.utils.diagnostics.logging.CountTotalDiagnosticsLogger log
                INFO: [Mapping   ] io.beanmapper.testmodel.record.PersonRecord -> io.beanmapper.testmodel.record.PersonResultRecord {total mappings: 2, total conversions: 0, max depth: 2} (CountTotalDiagnosticsLoggerTest.java:18)
                """;
        beanMapper = beanMapper.wrap(COUNT_TOTAL).build();
        (((DiagnosticsConfiguration) beanMapper.getConfiguration()).getDiagnosticsLogger().orElseThrow()).getLogger().addHandler(handler);
        beanMapper.map(new PersonRecord(1, "Henk"), PersonResultRecord.class);
        handler.flush();

        String output = outputStreamMock.toString();
        for (var line : outputTemplate.split("\n")) {
            assertTrue(outputTemplate.contains(line));
        }
        assertEquals(output.split("\n").length, outputTemplate.lines().count());
    }

}
