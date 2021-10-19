/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.codechurn;

import org.cpp.metrics.extractor.p4.LineRange;
import org.cpp.metrics.extractor.sonarqube.FunctionData;
import org.cpp.metrics.extractor.sonarqube.FunctionLevelData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestCodeChurnPerFunctionProcessor {
    private CodeChurnPerFunctionProcessor codeChurnProcessor;

    @Before
    public void SetUp() {
        codeChurnProcessor = new CodeChurnPerFunctionProcessor();
    }

    @Test
    public void whenProcessingSingleFileShouldComputeNumberOfChangesPerFunction() {
        var linesChanged = new ArrayList<LineRange>();
        linesChanged.add(new LineRange(57, 57));
        linesChanged.add(new LineRange(58, 58));

        var functionLevelData = new FunctionLevelData();
        var f1 = new FunctionData();
        f1.functionName = "TEST_F(GivenAStack, WhenPoppingShouldDestroyStackItem)";
        f1.startLine = 42;
        f1.endLine = 53;
        functionLevelData.functions.add(f1);

        var f2 = new FunctionData();
        f2.functionName = "TEST_F(GivenAStack, WhenPushingMultipleElementsShouldReturnExpectedValue)";
        f2.startLine = 55;
        f2.endLine = 63;
        functionLevelData.functions.add(f2);

        codeChurnProcessor.process(linesChanged, functionLevelData);

        assertEquals(codeChurnProcessor.output.get(f1.functionName).intValue(), 0);
        assertEquals(codeChurnProcessor.output.get(f2.functionName).intValue(), 1);
    }

    @Test
    public void whenProcessingSingleFileWithRange1ShouldComputeNumberOfChangesPerFunction() {
        var linesChanged = new ArrayList<LineRange>();
        linesChanged.add(new LineRange(40, 57));

        var functionLevelData = new FunctionLevelData();
        var f1 = new FunctionData();
        f1.functionName = "TEST_F(GivenAStack, WhenPoppingShouldDestroyStackItem)";
        f1.startLine = 42;
        f1.endLine = 53;
        functionLevelData.functions.add(f1);

        var f2 = new FunctionData();
        f2.functionName = "TEST_F(GivenAStack, WhenPushingMultipleElementsShouldReturnExpectedValue)";
        f2.startLine = 55;
        f2.endLine = 63;
        functionLevelData.functions.add(f2);

        codeChurnProcessor.process(linesChanged, functionLevelData);

        assertEquals(codeChurnProcessor.output.get(f1.functionName).intValue(), 1);
        assertEquals(codeChurnProcessor.output.get(f2.functionName).intValue(), 1);
    }

    @Test
    public void whenProcessingSingleFileWithRange2ShouldComputeNumberOfChangesPerFunction() {
        var linesChanged = new ArrayList<LineRange>();
        linesChanged.add(new LineRange(40, 57));

        var functionLevelData = new FunctionLevelData();
        var f1 = new FunctionData();
        f1.functionName = "TEST_F(GivenAStack, WhenPoppingShouldDestroyStackItem)";
        f1.startLine = 42;
        f1.endLine = 60;
        functionLevelData.functions.add(f1);

        codeChurnProcessor.process(linesChanged, functionLevelData);

        assertEquals(codeChurnProcessor.output.get(f1.functionName).intValue(), 1);
    }

    @Test
    public void whenProcessingSingleFileWithRange3ShouldComputeNumberOfChangesPerFunction() {
        var linesChanged = new ArrayList<LineRange>();
        linesChanged.add(new LineRange(40, 57));

        var functionLevelData = new FunctionLevelData();
        var f1 = new FunctionData();
        f1.functionName = "TEST_F(GivenAStack, WhenPoppingShouldDestroyStackItem)";
        f1.startLine = 35;
        f1.endLine = 40;
        functionLevelData.functions.add(f1);

        codeChurnProcessor.process(linesChanged, functionLevelData);

        assertEquals(codeChurnProcessor.output.get(f1.functionName).intValue(), 1);
    }

    @Test
    public void whenProcessingSingleFileWithRange4ShouldComputeNumberOfChangesPerFunction() {
        var linesChanged = new ArrayList<LineRange>();
        linesChanged.add(new LineRange(40, 42));

        var functionLevelData = new FunctionLevelData();
        var f1 = new FunctionData();
        f1.functionName = "TEST_F(GivenAStack, WhenPoppingShouldDestroyStackItem)";
        f1.startLine = 35;
        f1.endLine = 45;
        functionLevelData.functions.add(f1);

        codeChurnProcessor.process(linesChanged, functionLevelData);

        assertEquals(codeChurnProcessor.output.get(f1.functionName).intValue(), 1);
    }

    @Test
    public void whenProcessingSingleFileWithNoOverlapShouldComputeNumberOfChangesPerFunction() {
        var linesChanged = new ArrayList<LineRange>();
        linesChanged.add(new LineRange(40, 57));

        var functionLevelData = new FunctionLevelData();
        var f1 = new FunctionData();
        f1.functionName = "TEST_F(GivenAStack, WhenPoppingShouldDestroyStackItem)";
        f1.startLine = 60;
        f1.endLine = 61;
        functionLevelData.functions.add(f1);

        codeChurnProcessor.process(linesChanged, functionLevelData);

        assertEquals(codeChurnProcessor.output.get(f1.functionName).intValue(), 0);
    }

    @Test
    public void whenExportingThenShouldGenerateOutputOnExpectedFormat() {
        codeChurnProcessor.output.put("func2", 10);
        codeChurnProcessor.output.put("func1", 1);

        var json = codeChurnProcessor.getResultsInJson();

        assertEquals("{\"data\":[{\"functionName\":\"func2\",\"numberOfChanges\":10},{\"functionName\":\"func1\",\"numberOfChanges\":1}]}", json);
    }
}
