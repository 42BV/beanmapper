package io.beanmapper.utils.diagnostics.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.config.DiagnosticsConfiguration;
import io.beanmapper.testmodel.collections.CollectionListSource;
import io.beanmapper.testmodel.collections.CollectionListTarget;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.record.PersonRecord;
import io.beanmapper.testmodel.record.PersonResultRecord;
import io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CountPerPairDiagnosticsLoggerTest extends AbstractDiagnosticsLoggerTest {

    @Test
    void testCountPerPairDiagnosticsShouldReturnPrintCorrectCounts() {
        String outputTemplate = """
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                io.beanmapper.testmodel.record.PersonRecord -> io.beanmapper.testmodel.record.PersonResultRecord : 1
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                java.lang.Integer -> int : 1
                """;

        beanMapper = beanMapper.wrap(DiagnosticsDetailLevel.COUNT_PER_PAIR).build();
        (((DiagnosticsConfiguration) beanMapper.configuration()).getDiagnosticsLogger().orElseThrow()).getLogger().addHandler(handler);
        beanMapper.map(new PersonRecord(1, "Henk"), PersonResultRecord.class);
        handler.flush();
        String output = outputStreamMock.toString();
        String[] outputLines = output.lines().toArray(String[]::new);
        assertEquals(6, outputLines.length);
        for (String s : outputTemplate.split("\n")) {
            assertTrue(output.contains(s));
        }
    }

    @Test
    @Disabled("Does not work properly in pipelines, due to dynamic class.")
    void testCountPerPairDiagnosticsShouldReturnPrintCorrectCountsForList() {
        String outputTemplate = """
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] io.beanmapper.testmodel.record.PersonRecordDyn0 -> io.beanmapper.testmodel.record.PersonResultRecord : 1
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] io.beanmapper.testmodel.record.PersonRecord -> io.beanmapper.testmodel.record.PersonResultRecord : 1
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] java.lang.Integer -> int : 1
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] java.util.ArrayList<io.beanmapper.testmodel.record.PersonRecord> -> java.util.ArrayList<io.beanmapper.testmodel.record.PersonResultRecord> : 1
                """;

        beanMapper = beanMapper.wrap(DiagnosticsDetailLevel.COUNT_PER_PAIR).build();
        (((DiagnosticsConfiguration) beanMapper.configuration()).getDiagnosticsLogger().orElseThrow()).getLogger().addHandler(handler);
        beanMapper.map(new ArrayList<>(List.of(new PersonRecord(1, "Henk"))), PersonResultRecord.class);
        handler.flush();
        String output = outputStreamMock.toString();
        String[] outputLines = output.lines().toArray(String[]::new);
        assertEquals(10, outputLines.length);
        for (String s : outputTemplate.split("\n")) {
            assertTrue(output.contains(s));
        }
    }

    @Test
    void testCountForComplexDiagnostics() {
        String outputTemplate = """
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO: [Source    ]: java.util.ArrayList<io.beanmapper.testmodel.collections.CollectionListSource> -> java.util.ArrayList<io.beanmapper.testmodel.collections.CollectionListTarget>
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] java.util.ArrayList<io.beanmapper.testmodel.collections.CollectionListSource> -> java.util.ArrayList<io.beanmapper.testmodel.collections.CollectionListTarget> : 1
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Conversion] java.lang.String -> java.lang.String (io.beanmapper.core.converter.impl.ObjectToStringConverter) : 4
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Conversion] java.util.Collection<io.beanmapper.testmodel.person.Person> -> java.util.List<io.beanmapper.testmodel.person.PersonView> (io.beanmapper.core.converter.collections.CollectionConverter) : 2
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] java.util.ArrayList<io.beanmapper.testmodel.person.Person> -> java.util.ArrayList<io.beanmapper.testmodel.person.PersonView> : 2
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] io.beanmapper.testmodel.collections.CollectionListSource -> io.beanmapper.testmodel.collections.CollectionListTarget : 2
                io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger log
                INFO:   [Mapping   ] io.beanmapper.testmodel.person.Person -> io.beanmapper.testmodel.person.PersonView : 2
                """;

        beanMapper = beanMapper.wrap(DiagnosticsDetailLevel.COUNT_PER_PAIR).build();
        (((DiagnosticsConfiguration) beanMapper.configuration()).getDiagnosticsLogger().orElseThrow()).getLogger().addHandler(handler);
        beanMapper.map(
                new ArrayList<>(List.of(new CollectionListSource() {{items.add(new Person());}}, new CollectionListSource() {{items.add(new Person());}})),
                CollectionListTarget.class);
        handler.flush();
        String output = outputStreamMock.toString();
        String[] outputLines = output.lines().toArray(String[]::new);
        assertEquals(14, outputLines.length);
        for (String s : outputTemplate.split("\n")) {
            assertTrue(output.contains(s));
        }
    }

}
