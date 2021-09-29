/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import java.io.IOException;
import java.util.ArrayList;

public interface P4CodeChurnExtractor {
    void extractCodeChurn(String changesCommand, String printCommand, String diffCommand, String repositoryPath, String startDate, 
        String endDate, String outputFile, String tempDir, String tempFilePrefix, ArrayList<String> forceIncludes) throws IOException, InterruptedException;    
}
