/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.infrastructure;

public class ConsoleLogger implements Logger {

    @Override
    public void logMessage(String message) {
        System.out.println(message);
    }

}
