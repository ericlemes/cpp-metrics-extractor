/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.p4;

import org.cpp.metrics.extractor.infrastructure.FileStreamFactoryImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestChangesParser {
    private ChangesParser changesParser;

    @Before
    public void SetUp() {
        changesParser = new ChangesParser();
    }

    @Test
    public void whenParsingChangesWithValidFileShouldReturnChangesetNumbers() throws IOException {
        var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ChangesFiles1.txt");

        var changeNumbers = changesParser.parse(FileStreamFactoryImpl.streamToLines(stream));

        assertEquals(144545, changeNumbers.get(0).intValue());
        assertEquals(144544, changeNumbers.get(1).intValue());
        assertEquals(144541, changeNumbers.get(2).intValue());
        assertEquals(144540, changeNumbers.get(3).intValue());
        assertEquals(144538, changeNumbers.get(4).intValue());
        assertEquals(144537, changeNumbers.get(5).intValue());
        assertEquals(144535, changeNumbers.get(6).intValue());
        assertEquals(144530, changeNumbers.get(7).intValue());
        assertEquals(144527, changeNumbers.get(8).intValue());
        assertEquals(144526, changeNumbers.get(9).intValue());
    }
}
