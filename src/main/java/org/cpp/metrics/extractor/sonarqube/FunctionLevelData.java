/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.sonarqube;

import java.util.ArrayList;
import java.util.List;

public class FunctionLevelData {
    public final List<FunctionData> functions;

    public FunctionLevelData() {
        this.functions = new ArrayList<FunctionData>();
    }
}
