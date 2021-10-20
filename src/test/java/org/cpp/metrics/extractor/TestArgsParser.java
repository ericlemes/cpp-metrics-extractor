/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.cpp.metrics.extractor.p4.P4CodeChurnExtractor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestArgsParser {
    private ArgsParser parser;

    @Mock
    private MetricsExtractor metricsExtractor;

    @Mock
    private P4CodeChurnExtractor p4CodeChurnExtractor;

    @Captor
    private ArgumentCaptor<ArrayList<String>> forcedIncludesCaptor;

    @Before
    public void SetUp() {
        MockitoAnnotations.openMocks(this);

        parser = new ArgsParser(metricsExtractor, p4CodeChurnExtractor);
    }

    @Test
    public void whenCommandExtractMetricsThenShouldInvokeMetricsExtractor() throws IOException, ArgumentParserException, InterruptedException {
        var args = new String[]{"extractmetrics", "--input-file", "sourcefile", "--output-file", "outputfile", "--force-includes", "forceIncludes1"};

        parser.parse(args);

        verify(metricsExtractor, times(1)).extractMetrics(eq("sourcefile"), eq("outputfile"), forcedIncludesCaptor.capture());
        assertEquals(1, forcedIncludesCaptor.getValue().size());
        assertEquals("forceIncludes1", forcedIncludesCaptor.getValue().get(0));
    }

    @Test
    public void whenCommandExtractChurnThenShouldInvokeChurnExtractor() throws IOException, ArgumentParserException, InterruptedException {
        var args = new String[]{"p4churn", "--changes", "p4 changes command", "--print", "p4 print command",
                "--diff", "p4 diff command", "--repository-path", "repositoryPath", "--start-date", "startDate", "--end-date", "endDate",
                "--output-file", "outputfile", "--temp-dir", "tempDir", "--temp-file-prefix", "filePrefix", "--force-includes", "forceIncludes1", "forceIncludes2"};

        parser.parse(args);

        verify(p4CodeChurnExtractor, times(1)).extractCodeChurn(eq("p4 changes command"), eq("p4 print command"), eq("p4 diff command"),
                eq("repositoryPath"), eq("startDate"), eq("endDate"), eq("outputfile"), eq("tempDir"), eq("filePrefix"), forcedIncludesCaptor.capture());

        assertEquals(2, forcedIncludesCaptor.getValue().size());
        assertEquals("forceIncludes1", forcedIncludesCaptor.getValue().get(0));
        assertEquals("forceIncludes2", forcedIncludesCaptor.getValue().get(1));
    }

    @Test
    public void whenCommandExtractChurnWithoutOptionalArgumentsThenShouldInvokeChurnExtractor() throws IOException, ArgumentParserException, InterruptedException {
        var args = new String[]{"p4churn", "--repository-path", "repositoryPath", "--start-date", "startDate", "--end-date", "endDate",
                "--output-file", "outputfile", "--temp-dir", "tempDir", "--temp-file-prefix", "filePrefix"};

        parser.parse(args);

        verify(p4CodeChurnExtractor, times(1)).extractCodeChurn("p4 changes -s submitted %s@%s,%s", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputfile", "tempDir", "filePrefix", null);
    }

}
