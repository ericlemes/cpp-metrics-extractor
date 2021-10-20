/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.sonarqube;

public class FunctionData {
    public String functionName;
    public int startLine;
    public int endLine;

    public FunctionData() {
        this.functionName = "";
        this.startLine = -1;
        this.endLine = -1;
    }
}
