/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.cpp.metrics.extractor.codechurn.CodeChurnPerFunctionProcessor;
import org.cpp.metrics.extractor.infrastructure.ConsoleLogger;
import org.cpp.metrics.extractor.infrastructure.FileStreamFactoryImpl;
import org.cpp.metrics.extractor.infrastructure.ProcessWrapperImpl;
import org.cpp.metrics.extractor.p4.ChangesParser;
import org.cpp.metrics.extractor.p4.P4CodeChurnExtractorImpl;
import org.cpp.metrics.extractor.p4.P4DiffParser;
import org.cpp.metrics.extractor.sonarqube.SonarQubeMetricsExtractor;

import java.io.IOException;

public class MainClass {
    public static void main(String[] args) throws IOException, InterruptedException {
        var fileStreamFactory = new FileStreamFactoryImpl();

        var argsParser = new ArgsParser(
                new SonarQubeMetricsExtractor(
                        new FileStreamFactoryImpl()),
                new P4CodeChurnExtractorImpl(new ProcessWrapperImpl(new ConsoleLogger()), new ChangesParser(), new P4DiffParser(),
                        fileStreamFactory,
                        new SonarQubeMetricsExtractor(fileStreamFactory), new CodeChurnPerFunctionProcessor()));

        try {
            argsParser.parse(args);
        } catch (ArgumentParserException e) {
            System.exit(1);
        }
    }
}