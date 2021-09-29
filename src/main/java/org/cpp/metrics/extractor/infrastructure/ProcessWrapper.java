/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.infrastructure;

import java.io.IOException;
import java.util.ArrayList;

public interface ProcessWrapper {
    ArrayList<String> executeProcess(String commandLine) throws IOException, InterruptedException;
}
