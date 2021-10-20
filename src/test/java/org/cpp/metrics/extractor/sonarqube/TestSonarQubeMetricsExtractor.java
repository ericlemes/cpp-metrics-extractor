/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.sonarqube;

import org.assertj.core.api.SoftAssertions;
import org.cpp.metrics.extractor.infrastructure.FileStreamFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestSonarQubeMetricsExtractor {
    private SonarQubeMetricsExtractor metricsExtractor;

    @Mock
    private FileStreamFactory mockFileStreamFactory;

    @Before
    public void SetUp() {
        MockitoAnnotations.openMocks(this);
        metricsExtractor = new SonarQubeMetricsExtractor(mockFileStreamFactory);
    }

    @Test
    public void whenComputingMetricsShouldReturnFunctionNameStartAndEndLine() throws IOException {
        when(mockFileStreamFactory.createFileInputStream(any())).thenReturn(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("FunctionComplexity.cc"));

        var forcedIncludes = new ArrayList<String>();
        forcedIncludes.add("src/test/resources/forcedIncludes.h");

        FunctionLevelData functionLevelData = metricsExtractor.computeMetrics("FunctionComplexity.cc", forcedIncludes);

        var softly = new SoftAssertions();
        softly.assertThat(functionLevelData).isNotNull();
        softly.assertThat(functionLevelData.functions.get(0).functionName).isEqualTo("func1()");
        softly.assertThat(functionLevelData.functions.get(0).startLine).isEqualTo(1);
        softly.assertThat(functionLevelData.functions.get(0).endLine).isEqualTo(3);
        softly.assertThat(functionLevelData.functions.get(1).functionName).isEqualTo("func2(int a)");
        softly.assertThat(functionLevelData.functions.get(1).startLine).isEqualTo(5);
        softly.assertThat(functionLevelData.functions.get(1).endLine).isEqualTo(11);
        softly.assertThat(functionLevelData.functions.get(2).functionName).isEqualTo("func3(int a, int b)");
        softly.assertThat(functionLevelData.functions.get(2).startLine).isEqualTo(13);
        softly.assertThat(functionLevelData.functions.get(2).endLine).isEqualTo(27);
        softly.assertThat(functionLevelData.functions.get(3).functionName).isEqualTo("MyClass()");
        softly.assertThat(functionLevelData.functions.get(3).startLine).isEqualTo(31);
        softly.assertThat(functionLevelData.functions.get(3).endLine).isEqualTo(31);
        softly.assertThat(functionLevelData.functions.get(4).functionName).isEqualTo("Method1(int a, int b)");
        softly.assertThat(functionLevelData.functions.get(4).startLine).isEqualTo(32);
        softly.assertThat(functionLevelData.functions.get(4).endLine).isEqualTo(32);
        softly.assertThat(functionLevelData.functions.get(5).functionName).isEqualTo("Method2(int a, int b)");
        softly.assertThat(functionLevelData.functions.get(5).startLine).isEqualTo(33);
        softly.assertThat(functionLevelData.functions.get(5).endLine).isEqualTo(47);
        softly.assertThat(functionLevelData.functions.get(6).functionName).isEqualTo("MyClass::Method3(int a, int b)");
        softly.assertThat(functionLevelData.functions.get(6).startLine).isEqualTo(51);
        softly.assertThat(functionLevelData.functions.get(6).endLine).isEqualTo(65);
        softly.assertThat(functionLevelData.functions.get(10).functionName).isEqualTo("func4(int a,int b)");
        softly.assertThat(functionLevelData.functions.get(10).startLine).isEqualTo(89);
        softly.assertThat(functionLevelData.functions.get(10).endLine).isEqualTo(93);
        softly.assertThat(functionLevelData.functions.get(11).functionName).isEqualTo("func5(int a,int b, int c)");
        softly.assertThat(functionLevelData.functions.get(11).startLine).isEqualTo(97);
        softly.assertThat(functionLevelData.functions.get(11).endLine).isEqualTo(100);
        softly.assertAll();
    }
}
