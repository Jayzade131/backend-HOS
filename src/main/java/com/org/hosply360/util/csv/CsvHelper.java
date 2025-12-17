package com.org.hosply360.util.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CsvHelper {

    public static <T> List<T> parseCsv(InputStream is, Function<CSVRecord, T> mapper) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            List<T> result = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                result.add(mapper.apply(record));
            }
            return result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV: " + e.getMessage());
        }
    }
}
