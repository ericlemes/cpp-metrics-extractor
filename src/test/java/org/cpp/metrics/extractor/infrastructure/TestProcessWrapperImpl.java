/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.infrastructure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class TestProcessWrapperImpl {

    @Mock
    private Logger logger;

    @Before
    public void SetUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenRunningProcessShouldReturnStdOut() throws IOException, InterruptedException {
        var processWrapper = new ProcessWrapperImpl(logger);
        var content = processWrapper.executeProcess("cmd /c dir");
        assertNotNull(content);
        assertNotEquals("", content);
    }
}
