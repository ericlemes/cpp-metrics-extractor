/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */
package org.cpp.metrics.extractor.sonarqube;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

import org.sonar.cxx.parser.CxxGrammarImpl;
import org.sonar.cxx.squidbridge.SquidAstVisitor;
import org.sonar.cxx.squidbridge.api.SourceCode;
import org.sonar.cxx.squidbridge.api.SourceFile;

/**
 * Visitor that computes the NCLOCs in function body, leading and trailing {} do not count
 *
 * @param <GRAMMAR>
 */
public class CxxFunctionLevelMetricsVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR>  {

  @Override
  public void init() {    
    subscribeTo(CxxGrammarImpl.functionDefinition);    
  }

  public SourceFile getSourceFile(){
    SourceCode curr = getContext().peekSourceCode();
    while (! (curr instanceof SourceFile)){
      curr = curr.getParent();
    }
    return (SourceFile)curr;
  }

  public FunctionData getFunctionDataForNode(AstNode node) {
    FunctionData functionData = new FunctionData();
    FileRange functionNameRange = new FileRange(node.getFirstDescendant(CxxGrammarImpl.declarator));    

    String line = getContext().getInputFileLines().get(functionNameRange.startLine - 1);
    if (functionNameRange.endLine <= functionNameRange.startLine){
      line = line.substring(functionNameRange.startColumn, functionNameRange.endColumn);
    }
    else {
      StringBuilder b = new StringBuilder();
      int currLine = functionNameRange.startLine + 1;
      b.append(line.substring(functionNameRange.startColumn));
      while (currLine <= functionNameRange.endLine){
        if (currLine == functionNameRange.endLine)
          b.append(getContext().getInputFileLines().get(currLine - 1).substring(0, functionNameRange.endColumn));
        else
          b.append(getContext().getInputFileLines().get(currLine -1 ));
        currLine++;
      }
      line = b.toString();
    }
    functionData.functionName = line;

    FileRange functionRange = new FileRange(node);
    functionData.startLine = functionRange.startLine;
    functionData.endLine = functionRange.endLine;

    return functionData;
  }

  @Override
  public void visitNode(AstNode node) {
    FunctionLevelData data = (FunctionLevelData)getSourceFile().getData(FunctionLevelMetrics.FUNCTION_LEVEL_METRICS);

    if (data == null) {      
      data = new FunctionLevelData();
      getSourceFile().addData(FunctionLevelMetrics.FUNCTION_LEVEL_METRICS, data);
    }    

    data.functions.add(getFunctionDataForNode(node));
  }

}
