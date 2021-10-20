/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.codechurn;

import org.cpp.metrics.extractor.p4.LineRange;
import org.cpp.metrics.extractor.sonarqube.FunctionData;
import org.cpp.metrics.extractor.sonarqube.FunctionLevelData;
import org.sonar.api.internal.google.gson.JsonArray;
import org.sonar.api.internal.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeMap;

public class CodeChurnPerFunctionProcessor {

    public Hashtable<String, Integer> output;

    public CodeChurnPerFunctionProcessor() {
        this.output = new Hashtable<String, Integer>();
    }

    public void process(ArrayList<LineRange> linesChanged, FunctionLevelData functionLevelData) {
        var processed = new Hashtable<FunctionData, Boolean>();
        for (LineRange lineChanged : linesChanged) {
            for (var functionData : functionLevelData.functions) {
                if (processed.containsKey(functionData))
                    continue;

                if (!output.containsKey(functionData.functionName))
                    output.put(functionData.functionName, 0);

                if (((functionData.startLine >= lineChanged.startLine) && (functionData.startLine <= lineChanged.endLine)) ||
                        ((functionData.endLine >= lineChanged.startLine) && (functionData.endLine <= lineChanged.endLine)) ||
                        ((lineChanged.startLine >= functionData.startLine) && (lineChanged.endLine <= functionData.endLine))) {
                    output.put(functionData.functionName, output.get(functionData.functionName) + 1);
                    processed.put(functionData, true);
                }
            }
        }
    }

    private TreeMap<String, Integer> getOutputSortedByValueDescending() {
        var sortedMap = new TreeMap<String, Integer>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                var valueCompare = output.get(s1).compareTo(output.get(s2));
                if (valueCompare == 0)
                    return s1.compareTo(s2);
                else
                    return valueCompare * -1;
            }
        });

        for (String key : output.keySet()) {
            sortedMap.put(key, output.get(key));
        }
        return sortedMap;
    }

    public String getResultsInJson() {
        var sortedMap = getOutputSortedByValueDescending();

        var jsonFunctions = new JsonArray();
        for (String key : sortedMap.keySet()) {
            var jsonFunction = new JsonObject();
            jsonFunction.addProperty("functionName", key);
            jsonFunction.addProperty("numberOfChanges", sortedMap.get(key));
            jsonFunctions.add(jsonFunction);
        }

        var jsonMasterNode = new JsonObject();
        jsonMasterNode.add("data", jsonFunctions);

        return jsonMasterNode.toString();
    }
}