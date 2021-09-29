/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.cpp.metrics.extractor.infrastructure.FileStreamFactoryImpl;
import org.junit.*;

public class TestP4DiffParser {
  
  private P4DiffParser diffParser;

  @Before
  public void SetUp(){
    diffParser = new P4DiffParser();
  }

  @Test
  public void whenParsingValidDiffShouldReturnChangedLines() throws IOException{
    var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("p4diff1.txt");
    var changedLines = diffParser.parse(FileStreamFactoryImpl.streamToLines(stream));

    assertEquals(57, changedLines.get(0).startLine);
    assertEquals(57, changedLines.get(0).endLine);
    assertEquals(58, changedLines.get(1).startLine);
    assertEquals(58, changedLines.get(1).endLine);
  }

  @Test
  public void whenParsingDiffWithRangesShouldReturnChangedLines() throws IOException {
    var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("p4diff2.txt");
    var changedLines = diffParser.parse(FileStreamFactoryImpl.streamToLines(stream));

    assertEquals(65, changedLines.get(0).startLine);
    assertEquals(74, changedLines.get(0).endLine);
  }
}
