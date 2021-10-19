/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import org.cpp.metrics.extractor.MetricsExtractor;
import org.cpp.metrics.extractor.codechurn.CodeChurnPerFunctionProcessor;
import org.cpp.metrics.extractor.infrastructure.FileStreamFactory;
import org.cpp.metrics.extractor.infrastructure.ProcessWrapper;

import java.io.IOException;
import java.util.ArrayList;

public class P4CodeChurnExtractorImpl implements P4CodeChurnExtractor {
    private final ProcessWrapper processWrapper;

    private final ChangesParser changesParser;

    private final FileStreamFactory fileStreamFactory;

    private final MetricsExtractor metricsExtractor;

    private final P4DiffParser diffParser;

    private final CodeChurnPerFunctionProcessor codeChurnProcessor;

    public P4CodeChurnExtractorImpl(ProcessWrapper processWrapper, ChangesParser changesParser, P4DiffParser diffParser,
                                    FileStreamFactory fileStreamFactory, MetricsExtractor metricsExtractor, CodeChurnPerFunctionProcessor codeChurnProcessor) {
        this.processWrapper = processWrapper;
        this.changesParser = changesParser;
        this.diffParser = diffParser;
        this.fileStreamFactory = fileStreamFactory;
        this.metricsExtractor = metricsExtractor;
        this.codeChurnProcessor = codeChurnProcessor;
    }

    public void extractCodeChurn(String changesCommand, String printCommand, String diffCommand, String repositoryPath, String startDate,
                                 String endDate, String outputFile, String tempDir, String tempFilePrefix, ArrayList<String> forceIncludes) throws IOException, InterruptedException {
        var stdOut = processWrapper.executeProcess(String.format(changesCommand, repositoryPath, startDate, endDate));
        var changesets = changesParser.parse(stdOut);

        for (int i = changesets.size() - 1; i >= 0; i--) {
            var changeset = changesets.get(i);
            var fileRevStream = processWrapper.executeProcess(String.format(printCommand, repositoryPath, changeset));
            var fileName = tempDir + "/" + tempFilePrefix + changeset;
            fileStreamFactory.writeLinesToFile(fileRevStream, fileName);
            if (i != changesets.size() - 1) {
                var previousChangeset = changesets.get(i + 1);
                var previousFileName = tempDir + "/" + tempFilePrefix + previousChangeset;
                var functionLevelData = metricsExtractor.computeMetrics(previousFileName, forceIncludes);

                var diffProcessOutput = processWrapper.executeProcess(String.format(diffCommand, repositoryPath,
                        previousChangeset, repositoryPath, changesets.get(i)));
                var linesChanged = diffParser.parse(diffProcessOutput);
                codeChurnProcessor.process(linesChanged, functionLevelData);
            }
        }
        fileStreamFactory.writeStringToFile(outputFile, codeChurnProcessor.getResultsInJson());
    }
}
