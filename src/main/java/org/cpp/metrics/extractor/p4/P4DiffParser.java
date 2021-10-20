/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class P4DiffParser {
    public ArrayList<LineRange> parse(ArrayList<String> lines) throws IOException {
        var l = new ArrayList<LineRange>();

        for (String line : lines) {
            parseLine(l, line);
        }

        return l;
    }

    private void parseLine(ArrayList<LineRange> l, String line) {
        if ((line == null) || (line.trim().equals("")) || (line.charAt(0) == '>') || (line.charAt(0) == '<')
                || (line.startsWith("---")) || (line.startsWith("====")))
            return;
        var patternForSingleLine = Pattern.compile("^(\\d+)[a-z]\\d+");
        var matcher = patternForSingleLine.matcher(line);

        if (matcher.matches())
            l.add(new LineRange(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(1))));

        var patternForLineRange = Pattern.compile("^(\\d+),(\\d+)[a-z]\\d+");
        matcher = patternForLineRange.matcher(line);
        if (matcher.matches())
            l.add(new LineRange(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))));

    }
}
