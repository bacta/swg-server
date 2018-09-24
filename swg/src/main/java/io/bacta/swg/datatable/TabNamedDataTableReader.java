package io.bacta.swg.datatable;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.io.Files.getNameWithoutExtension;
import static java.nio.file.Files.newBufferedReader;

/**
 * Reads a tab delimited data table file. The file should have the first row as the column names, second row as the
 * column types, and the remaining rows as the cells of the data table.
 */
@Slf4j
public class TabNamedDataTableReader implements NamedDataTableReader {
    private static final String DELIMITER_REGEX = "\t";

    private final DataTableManager dataTableManager;

    public TabNamedDataTableReader(DataTableManager dataTableManager) {
        this.dataTableManager = dataTableManager;
    }

    @Override
    public NamedDataTable read(final Path filePath) throws IOException {
        final String tableName = getTableNameFromFileName(filePath);

        try (final BufferedReader reader = newBufferedReader(filePath, Charset.forName("ISO-8859-1"))) {

            final String columnsLine = reader.readLine();
            final List<String> columnNames = readColumnNames(columnsLine);

            final String typesLine = reader.readLine();
            final List<DataTableColumnType> columnTypes = readColumnTypes(typesLine);

            final List<List<DataTableCell>> cells = reader.lines()
                    .map(s -> readRow(s, columnTypes))
                    .collect(Collectors.toList());

            return new NamedDataTable(tableName, columnNames, columnTypes, cells);
        }
    }

    private String getTableNameFromFileName(Path filePath) {
        return getNameWithoutExtension(filePath.getFileName().toString());
    }

    private List<String> readColumnNames(final String line) {
        final String[] columnNames = line.split(DELIMITER_REGEX);
        return Arrays.asList(columnNames);
    }

    private List<DataTableColumnType> readColumnTypes(final String line) {
        final String[] columnTypeAbbreviations = line.split(DELIMITER_REGEX);
        final List<DataTableColumnType> columnTypes = new ArrayList<>(columnTypeAbbreviations.length);

        for (final String columnTypeAbbreviation : columnTypeAbbreviations) {
            final DataTableColumnType columnType = new DataTableColumnType(unescapeQuotes(columnTypeAbbreviation), dataTableManager);
            columnTypes.add(columnType);
        }

        return columnTypes;
    }

    private List<DataTableCell> readRow(final String line, final List<DataTableColumnType> columnTypes) {
        final String[] values = line.split(DELIMITER_REGEX);
        final List<DataTableCell> row = new ArrayList<>(values.length);

        final int totalValues = values.length;
        final int totalColumns = columnTypes.size();

        for (int columnIndex = 0; columnIndex < totalColumns; ++columnIndex) {
            final DataTableColumnType columnType = columnTypes.get(columnIndex);
            //If we don't have a value for this column, set its value to an empty string.
            final String value = columnIndex < totalValues ? values[columnIndex] : "";

            final DataTableCell cell = createCell(columnType, value);
            row.add(cell);
        }

        return row;
    }

    private static DataTableCell createCell(final DataTableColumnType columnType, final String value) {
        final String unescapedValue = unescapeQuotes(value);
        final String mangledValue = columnType.mangleValue(unescapedValue);

        switch (columnType.getBasicType()) {
            case INT:
                return new DataTableCell(mangledValue.isEmpty() ? 0 : Integer.parseInt(mangledValue));
            case FLOAT:
                return new DataTableCell(mangledValue.isEmpty() ? 0 : Float.parseFloat(mangledValue));
            case STRING:
            case COMMENT:
                return new DataTableCell(mangledValue);
            default:
                throw new RuntimeException("Type with unknown basic type specified in the types row.");
        }
    }

    private static String unescapeQuotes(final String s) {
        //First strip any beginning end quotes.
        //Then deal with embedded quotes
        //iterate through string
        //if character is a quote
        //if next is a quote erase this one jump past next
        //else erase this one
        //else next
        int start = 0;
        int end = s.length();

        if (s.length() >= 2) {
            if (s.charAt(0) == '"')
                start = 1;

            if (s.charAt(end - 1) == '"')
                end = end - 1;
        }

        //Initializing the string builder to the size of the string as the string can never be larger than this.
        final StringBuilder builder = new StringBuilder(end - start );

        for (int i = start; i < end; ++i) {
            final char c = s.charAt(i);

            //If the character at the position is a quote, and the next character is also a quote, then just append
            //a single quote and skip the next character.
            if (c == '"') {
                if ((i + 1) < end && s.charAt(i + 1) == '"') {
                    builder.append('"');
                    ++i;
                }
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
