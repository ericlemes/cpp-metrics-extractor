/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import org.cpp.metrics.extractor.codechurn.CodeChurnPerFunctionProcessor;
import org.cpp.metrics.extractor.infrastructure.FileStreamFactory;
import org.cpp.metrics.extractor.infrastructure.ProcessWrapper;
import org.cpp.metrics.extractor.sonarqube.FunctionLevelData;
import org.cpp.metrics.extractor.sonarqube.SonarQubeMetricsExtractor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class TestP4CodeChurnExtractorImpl {
    @Mock
    private ProcessWrapper processWrapper;

    @Mock
    private ChangesParser changesParser;

    @Mock
    private SonarQubeMetricsExtractor metricsExtractor;

    private P4CodeChurnExtractorImpl churnExtractor;

    private ArrayList<String> changesInputStream;

    private ArrayList<String> fileRev3;
    private ArrayList<String> fileRev2;
    private ArrayList<String> fileRev1;

    @Mock
    private FileStreamFactory fileStreamFactory;

    @Mock
    private P4DiffParser diffParser;

    @Mock
    private CodeChurnPerFunctionProcessor codeChurnProcessor;

    private FunctionLevelData rev1Data;
    private FunctionLevelData rev2Data;
    private FunctionLevelData rev3Data;

    private ArrayList<LineRange> diff1to2Lines;
    private ArrayList<LineRange> diff2to3Lines;

    private ArrayList<String> diff1to2Output;
    private ArrayList<String> diff2to3Output;

    private ArrayList<String> forceIncludes;

    @Before
    public void SetUp() throws IOException, InterruptedException {
        MockitoAnnotations.openMocks(this);

        churnExtractor = new P4CodeChurnExtractorImpl(processWrapper, changesParser, diffParser, fileStreamFactory, metricsExtractor, codeChurnProcessor);

        changesInputStream = new ArrayList<String>();
        when(processWrapper.executeProcess("changesCommand")).thenReturn(changesInputStream);

        ArrayList<Integer> changesets = new ArrayList<Integer>();
        changesets.add(3);
        changesets.add(2);
        changesets.add(1);
        when(changesParser.parse(changesInputStream)).thenReturn(changesets);

        fileRev1 = new ArrayList<String>();
        fileRev2 = new ArrayList<String>();
        fileRev3 = new ArrayList<String>();
        when(processWrapper.executeProcess("p4 print repositoryPath@3")).thenReturn(fileRev3);
        when(processWrapper.executeProcess("p4 print repositoryPath@2")).thenReturn(fileRev2);
        when(processWrapper.executeProcess("p4 print repositoryPath@1")).thenReturn(fileRev1);

        rev1Data = new FunctionLevelData();
        rev2Data = new FunctionLevelData();
        rev3Data = new FunctionLevelData();
        forceIncludes = new ArrayList<String>();
        when(metricsExtractor.computeMetrics("tempDir/filePrefix1", forceIncludes)).thenReturn(rev1Data);
        when(metricsExtractor.computeMetrics("tempDir/filePrefix2", forceIncludes)).thenReturn(rev2Data);
        when(metricsExtractor.computeMetrics("tempDir/filePrefix3", forceIncludes)).thenReturn(rev3Data);

        diff1to2Output = new ArrayList<String>();
        diff1to2Output.add("diff1to2Output");
        diff2to3Output = new ArrayList<String>();
        diff2to3Output.add("diff2to3Output");
        when(processWrapper.executeProcess("p4 diff2 repositoryPath@2 repositoryPath@3")).thenReturn(diff2to3Output);
        when(processWrapper.executeProcess("p4 diff2 repositoryPath@1 repositoryPath@2")).thenReturn(diff1to2Output);

        diff1to2Lines = new ArrayList<LineRange>();
        diff2to3Lines = new ArrayList<LineRange>();
        when(diffParser.parse(diff1to2Output)).thenReturn(diff1to2Lines);
        when(diffParser.parse(diff2to3Output)).thenReturn(diff2to3Lines);

        when(codeChurnProcessor.getResultsInJson()).thenReturn("someJson");
    }

    @Test
    public void whenExtractingChurnThenShouldInvokeP4Command() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("p4 changes -s submitted %s@%s,%s", "printCommand", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(processWrapper).executeProcess("p4 changes -s submitted repositoryPath@startDate,endDate");
    }

    @Test
    public void whenExtractingChurnThenShouldParseChanges() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "printCommand", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(changesParser, times(1)).parse(changesInputStream);
    }

    @Test
    public void whenExtractingChurnShouldGetFileContents() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(processWrapper, times(1)).executeProcess("p4 print repositoryPath@3");
        verify(processWrapper, times(1)).executeProcess("p4 print repositoryPath@2");
        verify(processWrapper, times(1)).executeProcess("p4 print repositoryPath@1");
    }

    @Test
    public void whenExtractingChurnShouldSaveFilesInTempDir() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(fileStreamFactory, times(1)).writeLinesToFile(fileRev1, "tempDir/filePrefix1");
        verify(fileStreamFactory, times(1)).writeLinesToFile(fileRev2, "tempDir/filePrefix2");
        verify(fileStreamFactory, times(1)).writeLinesToFile(fileRev3, "tempDir/filePrefix3");
    }

    @Test
    public void whenExtractingChurnShouldGetMetricsForEachFileRevision() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(metricsExtractor, times(1)).computeMetrics("tempDir/filePrefix1", forceIncludes);
        verify(metricsExtractor, times(1)).computeMetrics("tempDir/filePrefix2", forceIncludes);
    }

    @Test
    public void whenExtractingChurnShouldGetDiffsForEachFileRevision() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(processWrapper, times(1)).executeProcess("p4 diff2 repositoryPath@2 repositoryPath@3");
        verify(processWrapper, times(1)).executeProcess("p4 diff2 repositoryPath@1 repositoryPath@2");
    }

    @Test
    public void whenExtractingChurnShouldParseDiffs() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(diffParser, times(1)).parse(diff1to2Output);
        verify(diffParser, times(1)).parse(diff2to3Output);
    }

    @Test
    public void whenExtractingChurnShouldComputeChurnForEachDiff() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        InOrder inOrder = inOrder(codeChurnProcessor);
        inOrder.verify(codeChurnProcessor, times(1)).process(diff1to2Lines, rev1Data);
        inOrder.verify(codeChurnProcessor, times(1)).process(diff2to3Lines, rev2Data);
    }

    @Test
    public void whenExtractingChurnShouldSaveJsonInOutputFile() throws IOException, InterruptedException {
        churnExtractor.extractCodeChurn("changesCommand", "p4 print %s@%d", "p4 diff2 %s@%d %s@%d", "repositoryPath", "startDate", "endDate",
                "outputFile", "tempDir", "filePrefix", forceIncludes);

        verify(fileStreamFactory, times(1)).writeStringToFile("outputFile", "someJson");
    }
}
