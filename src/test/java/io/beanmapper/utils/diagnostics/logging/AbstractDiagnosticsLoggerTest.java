package io.beanmapper.utils.diagnostics.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractDiagnosticsLoggerTest {

    protected final ByteArrayOutputStream outputStreamMock = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;
    protected BeanMapper beanMapper;
    protected Handler handler;

    @BeforeEach
    void setup() {
        handler = new StreamHandler(outputStreamMock, new SimpleFormatter());
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }
}
