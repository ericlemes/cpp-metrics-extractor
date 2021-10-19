/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProcessWrapperImpl implements ProcessWrapper {

    private final Logger logger;

    public ProcessWrapperImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ArrayList<String> executeProcess(String commandLine) throws IOException, InterruptedException {
        logger.logMessage("Executing command: " + commandLine);

        var result = new ArrayList<String>();
        Process process = Runtime.getRuntime().exec(commandLine);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }

        reader.close();
        process.waitFor();
        if (process.exitValue() != 0)
            throw new IOException("Process returning non-zero error code: " + process.exitValue());

        return result;
    }

}
