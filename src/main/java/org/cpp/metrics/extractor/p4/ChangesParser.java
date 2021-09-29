/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChangesParser {
    public List<Integer> parse(ArrayList<String> input) throws IOException {        
        ArrayList<Integer> l = new ArrayList<Integer>();

        for (String line : input) {
            parseLine(l, line);            
        }

        return l;
    }

    private void parseLine(ArrayList<Integer> l, String line) {        
        var pattern = Pattern.compile("^Change\\s{1}(\\d+)\\s{1}on\\s{1}");
        var matcher = pattern.matcher(line);

        if (!matcher.find())
            return;
                
        l.add(Integer.parseInt(matcher.group(1)));        
    }    
}
