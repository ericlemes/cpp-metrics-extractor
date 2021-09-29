/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.infrastructure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public interface FileStreamFactory {
    InputStream createFileInputStream(File inputFile) throws FileNotFoundException;
    void writeStreamToFile(InputStream inputStream, String outputFile) throws IOException;
    void writeStringToFile(String fileName, String content) throws IOException;
    void writeLinesToFile(ArrayList<String> lines, String fileName) throws IOException;
}
