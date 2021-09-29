/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

public class LineRange {
    public int startLine;
    public int endLine;

    public LineRange(int startLine, int endLine){
        this.startLine = startLine;
        this.endLine = endLine;
    }    
}
