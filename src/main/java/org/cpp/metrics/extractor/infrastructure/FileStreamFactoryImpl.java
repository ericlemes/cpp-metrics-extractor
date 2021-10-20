/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.infrastructure;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class FileStreamFactoryImpl implements FileStreamFactory {

    public static ArrayList<String> streamToLines(InputStream stream) throws IOException {
        var result = new ArrayList<String>();
        var reader = new BufferedReader(
                new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    @Override
    public InputStream createFileInputStream(File inputFile) throws FileNotFoundException {
        return new FileInputStream(inputFile);
    }

    @Override
    public void writeStreamToFile(InputStream inputStream, String outputFile) throws IOException {
        java.nio.file.Files.copy(
                inputStream,
                new File(outputFile).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void writeStringToFile(String fileName, String content) throws IOException {
        java.nio.file.Files.writeString(new File(fileName).toPath(), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public void writeLinesToFile(ArrayList<String> lines, String fileName) throws IOException {
        File outputFile = new File(fileName);

        var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
}
