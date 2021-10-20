/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.sonarqube;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;

public class FileRange {
    private static final String NEWLINE_REGEX = "(\r)?\n|\r";
    public int startLine;
    public int startColumn;
    public int endLine;
    public int endColumn;

    public FileRange() {
        this.startLine = -1;
        this.startColumn = -1;
        this.endLine = -1;
        this.endColumn = -1;
    }

    public FileRange(AstNode node) {
        Token startToken = node.getToken();
        Token endToken = node.getLastToken();
        String[] tokenLines = endToken.getOriginalValue().split(NEWLINE_REGEX, -1);
        int tokenLastLine = endToken.getLine() + tokenLines.length - 1;
        int tokenLastLineColumn = (tokenLines.length > 1 ? 0 : endToken.getColumn()) + tokenLines[tokenLines.length - 1].length();

        this.startLine = startToken.getLine();
        this.startColumn = startToken.getColumn();
        this.endLine = tokenLastLine;
        this.endColumn = tokenLastLineColumn;
    }

}
