/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.sonarqube;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.cpp.metrics.extractor.MetricsExtractor;
import org.cpp.metrics.extractor.infrastructure.FileStreamFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultIndexedFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.Metadata;
import org.sonar.api.batch.fs.internal.SensorStrategy;
import org.sonar.api.internal.google.gson.JsonArray;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.cxx.CxxAstScanner;
import org.sonar.cxx.config.CxxSquidConfiguration;
import org.sonar.cxx.toolkit.CxxConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationModel;

public class SonarQubeMetricsExtractor implements MetricsExtractor {

    private FileStreamFactory fileStreamFactory;

    public SonarQubeMetricsExtractor(FileStreamFactory fileStreamFactory)
    {
      this.fileStreamFactory = fileStreamFactory;
    }

    @Override
    public void extractMetrics(String sourceFile, String outputFile, ArrayList<String> forceIncludes) throws IOException {
        exportDataToJson(computeMetrics(sourceFile, forceIncludes), outputFile);
    }

    public FunctionLevelData computeMetrics(String fileName, ArrayList<String> forceIncludes) throws IOException{
        java.io.File f = new java.io.File(fileName);

        ConfigurationModel configurationModel = new CxxConfigurationModel();    

        var inputFile = createInputFile(f.getAbsolutePath(), "", configurationModel.getCharset());
        
        var visitor = new CxxFunctionLevelMetricsVisitor<>();
    
        var squidConfig = new CxxSquidConfiguration(f.getPath(), configurationModel.getCharset());

        if (forceIncludes != null && forceIncludes.size() > 0) {
          squidConfig.add(CxxSquidConfiguration.SONAR_PROJECT_PROPERTIES, CxxSquidConfiguration.FORCE_INCLUDES,
            forceIncludes);             
            
          System.out.println("Has forced includes: " + forceIncludes.get(0));
        }
        else {
          System.out.println("No forced includes");
        }
    
        @SuppressWarnings("unchecked")
        var sourceFile = CxxAstScanner.scanSingleInputFileConfig(inputFile, squidConfig, visitor);    
        FunctionLevelData data = (FunctionLevelData)sourceFile.getData(FunctionLevelMetrics.FUNCTION_LEVEL_METRICS);

        return data;
    }

    private String getSourceCode(File filename, Charset defaultCharset) throws IOException {
        try ( var bomInputStream = new BOMInputStream(fileStreamFactory.createFileInputStream(filename),
                                                  ByteOrderMark.UTF_8,
                                                  ByteOrderMark.UTF_16LE,
                                                  ByteOrderMark.UTF_16BE,
                                                  ByteOrderMark.UTF_32LE,
                                                  ByteOrderMark.UTF_32BE)) {
          ByteOrderMark bom = bomInputStream.getBOM();
          Charset charset = bom != null ? Charset.forName(bom.getCharsetName()) : defaultCharset;
          byte[] bytes = bomInputStream.readAllBytes();
          return new String(bytes, charset);
        }
      }      
    
      private static void exportDataToJson(FunctionLevelData data, String outputFile) throws IOException{
        var jsonFunctions = new JsonArray();
        for (FunctionData functionData : data.functions) {
          var jsonFunction = new JsonObject();
          jsonFunction.addProperty("functionName", functionData.functionName);
          jsonFunction.addProperty("startLine", functionData.startLine);
          jsonFunction.addProperty("endLine", functionData.endLine);
          jsonFunctions.add(jsonFunction);
        }
        var jsonMasterNode = new JsonObject();
        jsonMasterNode.add("data", jsonFunctions);
        
        java.nio.file.Files.write(Paths.get(outputFile), jsonMasterNode.toString().getBytes(), (OpenOption)StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    
      }

      public InputFile createInputFile(String absolutePath, String moduleKey, Charset charset) throws IOException{
        var contents = getSourceCode(new File(absolutePath), charset);        
    
        DefaultIndexedFile indexedFile = new DefaultIndexedFile(Paths.get(absolutePath), "", "", "",  InputFile.Type.MAIN, "", 0, new SensorStrategy());
        
        DefaultInputFile inputFile = new DefaultInputFile(indexedFile, 
        f -> f.setMetadata(new Metadata(0, 0, "", null, null, 0)),
        contents);
    
        return inputFile;
    }
    
}
