package io.beanmapper.utils.diagnostics.logging;

import static io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel.TREE_COMPLETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.config.DiagnosticsConfiguration;
import io.beanmapper.testmodel.collections.CollectionMapSource;
import io.beanmapper.testmodel.collections.CollectionMapTarget;
import io.beanmapper.testmodel.nested_classes.Layer1;
import io.beanmapper.testmodel.nested_classes.Layer1Result;
import io.beanmapper.testmodel.person.Person;

import org.junit.jupiter.api.Test;

class TreeCompleteDiagnosticsLoggerTest extends AbstractDiagnosticsLoggerTest {

    @Test
    void testCompleteTreePrintsCorrectTree() {
        String outputTemplate = """
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO: [Mapping   ] io.beanmapper.testmodel.nested_classes.Layer1 -> io.beanmapper.testmodel.nested_classes.Layer1Result (TreeCompleteDiagnosticsLogger.java
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:   [Mapping   ] io.beanmapper.testmodel.nested_classes.Layer2 -> io.beanmapper.testmodel.nested_classes.Layer2Result
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:     [Mapping   ] io.beanmapper.testmodel.nested_classes.Layer3 -> io.beanmapper.testmodel.nested_classes.Layer3Result
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:       [Conversion] java.lang.String -> java.lang.String (io.beanmapper.core.converter.impl.ObjectToStringConverter)
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:       [Conversion] java.lang.Long -> java.lang.Long (io.beanmapper.core.converter.impl.NumberToNumberConverter)
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:     [Conversion] java.lang.String -> java.lang.String (io.beanmapper.core.converter.impl.ObjectToStringConverter)
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:   [Conversion] java.lang.String -> java.lang.String (io.beanmapper.core.converter.impl.ObjectToStringConverter)
                """;

        beanMapper = beanMapper.wrap(TREE_COMPLETE).build();
        ((DiagnosticsConfiguration) beanMapper.getConfiguration()).getDiagnosticsLogger().orElseThrow().getLogger().addHandler(handler);
        beanMapper.map(Layer1.createNestedClassObject(), Layer1Result.class);
        handler.flush();

        String output = outputStreamMock.toString();
        String[] outputLines = output.lines().toArray(String[]::new);
        assertEquals(14, outputLines.length);
        for (String s : outputTemplate.split("\n")) {
            assertTrue(output.contains(s));
        }
    }

    @Test
    void testCompleteTreePrintsCorrectTreeWhenConvertingMap() {
        String outputTemplate = """
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO: [Mapping   ] io.beanmapper.testmodel.collections.CollectionMapSource -> io.beanmapper.testmodel.collections.CollectionMapTarget (TreeCompleteDiagnosticsLogger.java:
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:   [Conversion] java.util.Map<java.lang.String, io.beanmapper.testmodel.person.Person> -> java.util.Map<java.lang.String, io.beanmapper.testmodel.person.PersonView>\s
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:   [Mapping   ] java.util.TreeMap<java.lang.String, io.beanmapper.testmodel.person.Person> -> java.util.HashMap<java.lang.String, io.beanmapper.testmodel.person.PersonView>\s
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:     [Mapping   ] io.beanmapper.testmodel.person.Person -> io.beanmapper.testmodel.person.PersonView\s
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:       [Conversion] java.lang.String -> java.lang.String (io.beanmapper.core.converter.impl.ObjectToStringConverter)\s
                io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger logConversion
                INFO:       [Conversion] java.lang.String -> java.lang.String (io.beanmapper.core.converter.impl.ObjectToStringConverter)\s
                """;

        beanMapper = beanMapper.wrap(TREE_COMPLETE).build();
        ((DiagnosticsConfiguration) beanMapper.getConfiguration()).getDiagnosticsLogger().orElseThrow().getLogger().addHandler(handler);
        beanMapper.map(new CollectionMapSource() {{items.put("henk", new Person());}}, CollectionMapTarget.class);
        handler.flush();

        String output = outputStreamMock.toString();
//        String[] outputLines = output.lines().toArray(String[]::new);
        assertEquals(12, output.split("\n").length);
        for (String s : outputTemplate.split("\n")) {
            assertTrue(output.contains(s));
        }
    }

}
