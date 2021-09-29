/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.cpp.metrics.extractor.p4.P4CodeChurnExtractor;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ArgsParser {

    private MetricsExtractor metricsExtractor;

    private P4CodeChurnExtractor p4CodeChurnExtractor;
    
    public ArgsParser(MetricsExtractor metricsExtractor, P4CodeChurnExtractor p4CodeChurnExtractor) {
        this.metricsExtractor = metricsExtractor;
        this.p4CodeChurnExtractor = p4CodeChurnExtractor;
    }

    public void parse(String[] args) throws IOException, ArgumentParserException, InterruptedException {
        var argParser = ArgumentParsers.newFor("cpp-metrics-extractor").addHelp(true).build();
        argParser.description("Extracts metrics for C++ files");        
        var subParser = argParser.addSubparsers().title("commands").        
            description("Select valid command for cpp-metrics-extractor");
        
        setupExtractMethodsArguments(subParser);
        setupExtractP4CodeChurnArguments(subParser);

        Namespace ns = null;
        try {
            ns = argParser.parseArgs(args);
        }
        catch (ArgumentParserException e) {
            argParser.handleError(e);
            throw e;        
        }
        
        processArguments(ns);
    }

    private void setupExtractMethodsArguments(Subparsers subParser) {
        var extractMetricsParser = subParser.addParser("extractmetrics").
            description("Extract metrics for a single file and outputs to json file").setDefault("extractmetrics", true);
        extractMetricsParser.addArgument("--input-file").type(String.class).help("File name to extract metrics from").nargs(1).required(true);
        extractMetricsParser.addArgument("--output-file").type(String.class).help("File name for output json file").nargs(1).required(true);
        extractMetricsParser.addArgument("--force-includes").type(String.class).help("Files to be forcedly added as include files. Good for specifying defines.").
            nargs("*");        
    }

    private void setupExtractP4CodeChurnArguments(Subparsers subParser) {        
        var extractMetricsParser = subParser.addParser("p4churn").
            description("Extract code churn per function for a file in perforce repository over a period of time and outputs to json file").setDefault("p4churn", true);
        extractMetricsParser.addArgument("--repository-path").type(String.class).help("Full path to file on p4 repo, for example //repos/full/path/to/file.cpp").
            nargs("?").required(true);
        extractMetricsParser.addArgument("--start-date").type(String.class).help("Start date in the format YYYY/MM/DD. This will be used to call p4 changes command.").
            nargs("?").required(true);
        extractMetricsParser.addArgument("--end-date").type(String.class).help("End date in the format YYYY/MM/DD. This will be used to call p4 changes command and should consider " + 
            "D+1 for enddate, otherwise it won't consider the end date itself. For example, if you want changes from 2020/01/01 to 2020/01/02, you should " +
            "specify 2020/01/03 as end date, otherwise changes that happen after midnight of 2020/01/02 won't be included (this is p4 behaviour)").
            nargs("?").required(true);
        extractMetricsParser.addArgument("--changes").type(String.class).help("p4 changes command line to get changesets. Usually \"p4 changes -s submitted %s@%s,%s\" or something similar. The %s will be replaced by --repository path, --start-date, --end-date in this order").
            nargs("?").setDefault("p4 changes -s submitted %s@%s,%s");
        extractMetricsParser.addArgument("--print").type(String.class).help("p4 print command to get file revisions. Usually \"p4 print %s@%d\". %s and %d will be replaced by --repository-path and changeset number (return by p4 changes command)").
            nargs("?").setDefault("p4 print %s@%d");
        extractMetricsParser.addArgument("--diff").type(String.class).help("p4 diff command to get file revisions. Usually \"p4 diff2 %s@%d %s@%d\". %s and %d will be replaced by --repository-path and changeset number (return by p4 changes command)").
            nargs("?").setDefault("p4 diff2 %s@%d %s@%d");
        extractMetricsParser.addArgument("--output-file").type(String.class).help("File name for output json file").nargs("?").required(true);
        extractMetricsParser.addArgument("--temp-dir").type(String.class).help("Temp directory to download file revisions").nargs("?").required(true);
        extractMetricsParser.addArgument("--temp-file-prefix").type(String.class).help("File prefix for file revisions").nargs("?").required(true);
        extractMetricsParser.addArgument("--force-includes").type(String.class).help("Files to be forcedly added as include files. Good for specifying defines.").
            nargs("*");
    }    

    private void processArguments(Namespace ns) throws IOException, InterruptedException {
        System.out.println("force includes " + ns.getString("force_includes"));

        if (ns.get("extractmetrics") != null && ns.getBoolean("extractmetrics")) {
            this.metricsExtractor.extractMetrics(ns.getList("input_file").get(0).toString(), ns.getList("output_file").get(0).toString(), 
                ns.getList("force_includes") == null ? null : new ArrayList<String>(ns.getList("force_includes")));
        }
        else if (ns.get("p4churn") != null && ns.getBoolean("p4churn")){
            this.p4CodeChurnExtractor.extractCodeChurn(ns.getString("changes"), ns.getString("print"),
            ns.getString("diff"), ns.getString("repository_path"), ns.getString("start_date"), 
            ns.getString("end_date"), ns.getString("output_file"), ns.getString("temp_dir"), 
            ns.getString("temp_file_prefix"), ns.getList("force_includes") == null ? null : new ArrayList<String>(ns.getList("force_includes")));    
        }
    }
}
