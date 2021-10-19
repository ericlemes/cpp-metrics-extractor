/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor;

import org.cpp.metrics.extractor.sonarqube.FunctionLevelData;

import java.io.IOException;
import java.util.ArrayList;

public interface MetricsExtractor {
    void extractMetrics(String sourceFile, String outputFile, ArrayList<String> forceIncludes) throws IOException;

    FunctionLevelData computeMetrics(String fileName, ArrayList<String> forceIncludes) throws IOException;
}