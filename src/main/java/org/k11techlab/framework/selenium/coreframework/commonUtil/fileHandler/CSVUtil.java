package org.k11techlab.framework.selenium.coreframework.commonUtil.fileHandler;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVUtil {

    public static void writeJavaMapToCSVFile(String filename, Map<String, String> map) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            String[] keysArray = new String[map.keySet().size()];
            String[] valuesArray = new String[map.values().size()];
            writer.writeNext(keysArray);
            writer.writeNext(valuesArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param listOfMap
     * @param writer
     * @throws IOException
     */
    public static void csvWriter(List<HashMap<String, String>> listOfMap, Writer writer) throws IOException {
        CsvSchema schema = null;
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        if (listOfMap != null && !listOfMap.isEmpty()) {
            for (String col : listOfMap.get(0).keySet()) {
                schemaBuilder.addColumn(col);
            }
            schema = schemaBuilder.build().withLineSeparator(System.lineSeparator()).withHeader();
        }
        CsvMapper mapper = new CsvMapper();
        mapper.writer(schema).writeValues(writer).writeAll(listOfMap);
        writer.flush();
    }


    public static String toCSV(List<Map<String, String>> list) {
        List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.get(i));
            sb.append(i == headers.size()-1 ? "\n" : ",");
        }
        for (Map<String, String> map : list) {
            for (int i = 0; i < headers.size(); i++) {
                sb.append(map.get(headers.get(i)));
                sb.append(i == headers.size()-1 ? "\n" : ",");
            }
        }
        return sb.toString();
    }

}
