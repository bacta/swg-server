package io.bacta.shared.datatable;

import com.google.common.base.Preconditions;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import lombok.Data;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 2/8/15.
 * Converts an XML document, that looks like possibly exported from Excel, into the SWG DataTable format.
 */
public final class DataTableWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTableWriter.class);
    private static final String xmlExt = "xml";

    public List<NamedDataTable> tables = new ArrayList<>();

    @Setter
    private String outputPath;

    public static boolean isXmlFile(final String fileName) {
        Preconditions.checkNotNull(fileName);

        final int size = fileName.length();
        final int xmlSize = xmlExt.length();

        if (size < xmlSize)
            return false;

        for (int offset = size - xmlSize, i = 0; i < xmlSize; ++i) {
            if (fileName.charAt(offset + i) != xmlExt.charAt(i))
                return false;
        }

        return true;
    }

    public void loadFromSpreadsheet(final String fileName) {
        Preconditions.checkNotNull(fileName);

        if (!isXmlFile(fileName))
            // Original file format
            loadFromSpreadsheetTab(fileName);
        else
            LOGGER.warn("Xml files are unsupported.");
    }

    public boolean save(final String outputFileName, boolean optional) {

        Preconditions.checkNotNull(outputFileName);
        Preconditions.checkArgument(!outputFileName.isEmpty());
        Preconditions.checkArgument(tables.size() > 1, "OutputFileName not supported on DataTableWriter with multiple tables.");
        Preconditions.checkArgument(tables.size() == 0, "DataTableWriter is empty.");

        return writeTable(tables.get(0), outputFileName, optional);
    }

    public void save(final Iff iff) {
        Preconditions.checkArgument(tables.size() <= 1, "OutputFileName not supported on DataTableWriter with multiple tables.");
        Preconditions.checkArgument(tables.size() != 0, "DataTableWriter is empty.");
        NamedDataTable ndt = tables.get(0);

        saveTableToIff(iff, ndt);
    }

    public boolean save(boolean optional) {
        boolean retval = true;

        for (NamedDataTable ndt : tables) {
            String outputFile = getTableOutputFileName(ndt.getName());
            retval = writeTable(ndt, outputFile, optional) && retval;
        }

        return retval;
    }

    public String getTableOutputFileName(final String tableName) {
        Preconditions.checkArgument(!tableName.isEmpty(), "Empty table name");
        return Paths.get(outputPath, tableName + ".iff").toString();
    }

    public boolean saveTable(final String tableName, final String fileName, boolean optional) {
        boolean ret = true;

        NamedDataTable ndt = null;
        for (NamedDataTable table : tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                ndt = table;
                break;
            }
        }

        if (fileName == null || fileName.isEmpty()) {
            String outputFile = getTableOutputFileName(ndt.getName());
            ret = writeTable(ndt, outputFile, optional);
        } else {
            ret = writeTable(ndt, fileName, optional);
        }

        return ret;
    }


    private static String unquotify(final String s) {
        //First strip any beginning end quotes.
        //Then deal with imbedded quotes
        //iterate through string
        //if character is a quote
        //if next is a quote erase this one jump past next
        //else erase this one
        //else next
        final StringBuilder builder = new StringBuilder();

        int start = 0;
        int end = s.length();

        if (s.length() >= 2) {
            if (s.charAt(0) == '"')
                start = 1;

            if (s.charAt(end - 1) == '"')
                end = end - 1;
        }

        for (int i = start; i < end; ++i) {
            final char c = s.charAt(i);

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

    private static String getTableNameFromTabFile(final String fileName) {
        Preconditions.checkNotNull(fileName);
        return com.google.common.io.Files.getNameWithoutExtension(fileName);
    }

    private void loadFromSpreadsheetTab(final String filename) {
        Preconditions.checkNotNull(filename);
        Preconditions.checkArgument(Files.exists(Paths.get(filename)));

        try {
            List<String> inputFile = Files.readAllLines(Paths.get(filename));

            Preconditions.checkNotNull(inputFile.size() > 0);
            Preconditions.checkArgument(tables.isEmpty(), String.format("TAB file does not support multiple tables when loading %s.", filename));

            final NamedDataTable ndt = new NamedDataTable();
            tables.add(ndt);
            ndt.setName(getTableNameFromTabFile(filename));
            outputPath = Paths.get(filename).getParent().toString();

            String line = inputFile.remove(0);
            loadColumnNames(ndt, line);

            line = inputFile.remove(0);
            loadTypes(ndt, line);

            int lineNumber = 0;
            while (!inputFile.isEmpty()) {

                line = inputFile.remove(0);

                loadRow(ndt, line, lineNumber);
                lineNumber++;
            }

            Preconditions.checkArgument(ndt.getNumRows() > 0, String.format("No rows in the table when loading %s.", filename));

        } catch (IOException e) {
            LOGGER.error("Unable to read file {}", filename, e);
        }
    }

    private void loadColumnNames(final NamedDataTable ndt, final String line) {

        String[] columnNames = line.split("\t");

        for (String columnName : columnNames) {
            Preconditions.checkArgument(columnName.indexOf('\r') == -1, "End of line character in middle of string");
            ndt.columns.add(columnName);
        }
    }

    private void loadTypes(final NamedDataTable ndt, final String line) {

        String[] columnTypes = line.split("\t");

        Preconditions.checkArgument(line.indexOf('\r') == -1, "End of line character in middle of string");

        for (String columnType : columnTypes) {
            ndt.types.add(new DataTableColumnType(unquotify(columnType), null));
        }
    }

    private void loadRow(final NamedDataTable ndt, final String line, final int rowNumber) {

        List<DataTableCell> newRow = new ArrayList<>();

        String[] values = line.split("\t");

        int count = 0;
        for (String value : values) {
            DataTableCell newCell = getNewCell(ndt.getDataTypeForColumn(count), value);
            newRow.add(newCell);
            count++;
        }

        //Fill in empty rows:
        int oldSize = newRow.size();
        for (int i = 0; i < ndt.columns.size() - oldSize; ++i) {
            newRow.add(getNewCell(ndt.getDataTypeForColumn(i + oldSize), ""));
        }
        ndt.rows.add(newRow);
    }

    private DataTableCell getNewCell(final DataTableColumnType columnType, final String value) {
        final String newValue = unquotify(value);
        columnType.mangleValue(newValue);

        switch (columnType.getBasicType()) {
            case Int:
                return new DataTableCell(newValue.isEmpty() ? 0 : Integer.parseInt(newValue));
            case Float:
                return new DataTableCell(newValue.isEmpty() ? 0 : Float.parseFloat(newValue));
            case String:
            case Comment:
                return new DataTableCell(newValue);
            default:
                throw new RuntimeException("Type with unknown basic type specified in the types row.");
        }
    }

    private void saveTableToIff(final Iff iff, final NamedDataTable ndt) {
        Preconditions.checkNotNull(ndt);

        checkIntegrity(ndt);
        iff.insertForm(DataTable.TAG_DTII);
        iff.insertForm(Tag.TAG_0001);

        saveColumns(ndt, iff);
        saveTypes(ndt, iff);
        saveRows(ndt, iff);

        iff.exitForm(Tag.TAG_0001);
        iff.exitForm();
    }

    private void saveColumns(final NamedDataTable ndt, final Iff iff) {

        iff.insertChunk(DataTable.TAG_COLS);
        int numCols = 0;
        for (DataTableColumnType tableColumnType : ndt.types) {
            if (tableColumnType.getType() != DataTableColumnType.DataType.Comment)
                ++numCols;
        }

        iff.insertChunkData(numCols);
        for (int i = 0; i < ndt.columns.size(); ++i) {
            if (ndt.types.get(i).getType() != DataTableColumnType.DataType.Comment) {
                iff.insertChunkString(ndt.columns.get(i));
            }
        }
        iff.exitChunk(DataTable.TAG_COLS);
    }

    private void saveTypes(final NamedDataTable ndt, final Iff iff) {
        iff.insertChunk(DataTable.TAG_TYPE);

        for (DataTableColumnType columnType : ndt.types) {
            if (columnType.getType() != DataTableColumnType.DataType.Comment)
                iff.insertChunkString(columnType.getTypeSpecString());
        }

        iff.exitChunk(DataTable.TAG_TYPE);
    }

    private void saveRows(final NamedDataTable ndt, final Iff iff) {
        iff.insertChunk(DataTable.TAG_ROWS);

        int numRows = ndt.rows.size();
        iff.insertChunkData(numRows);
        for (List<DataTableCell> cellList : ndt.rows) {

            int count = 0;

            for (DataTableCell cell : cellList) {

                Preconditions.checkNotNull(cell);

                switch (ndt.types.get(count).getBasicType()) {
                    case Int: {
                        int tmp = cell.getIntValue();
                        iff.insertChunkData(tmp);
                        break;
                    }
                    case Float: {
                        float tmp = cell.getFloatValue();
                        iff.insertChunkData(tmp);
                        break;
                    }
                    case String: {
                        iff.insertChunkString(cell.getStringValue());
                        break;
                    }
                    case Comment: {
                    }
                    break;
                    default:
                        Preconditions.checkArgument(true, ("bad case"));
                        break;
                }
                ++count;
            }
            //should we enter a seperator here between rows?
        }

        iff.exitChunk(DataTable.TAG_ROWS);
    }

    private boolean writeTable(final NamedDataTable ndt, final String outputFile, boolean optional) {

//        if (!Files.isWritable(Paths.get(outputFile))) {
//            Preconditions.checkArgument(!optional, String.format("ERROR: The output file is not available for writing: %s", outputFile));
//            Preconditions.checkArgument(true, String.format("ERROR: The output file is not available for writing: %s", outputFile));
//            return false;
//        }

        Iff iff = new Iff(outputFile);
        saveTableToIff(iff, ndt);

        iff.write(outputFile);
        iff.close();
        return true;
    }

    private boolean checkIntegrity(final NamedDataTable ndt) {

        Preconditions.checkArgument(!ndt.columns.isEmpty(), ("empty columns"));
        Preconditions.checkArgument(ndt.columns.size() == ndt.types.size(), ("size mismatch"));
        Preconditions.checkArgument(!ndt.rows.isEmpty(), ("empty rows"));
        Preconditions.checkArgument(ndt.columns.size() == ndt.rows.get(0).size(), String.format("size mismatch %d %d", ndt.columns.size(), ndt.rows.get(0).size()));

        // Check unique constraints
        int column = 0;
        for (DataTableColumnType columnType : ndt.types) {

            if (columnType.areUniqueCellsRequired()) {

                for (List<DataTableCell> dataTableCellList : ndt.rows) {

                    for (DataTableCell cell : dataTableCellList) {

                        switch (columnType.getBasicType()) {
                            case Int:
                                Preconditions.checkArgument(
                                        dataTableCellList.get(column).getIntValue() == cell.getIntValue(),
                                        String.format("unique constraint not satisfied for column %s, value %d", dataTableCellList.get(column), cell.getIntValue())
                                );
                                break;
                            case Float:
                                Preconditions.checkArgument(
                                        dataTableCellList.get(column).getFloatValue() == cell.getFloatValue(),
                                        String.format("unique constraint not satisfied for column %s, value %d", dataTableCellList.get(column), cell.getFloatValue())
                                );
                                break;
                            case String:
                                Preconditions.checkArgument(
                                        dataTableCellList.get(column).getStringValue().equals(cell.getStringValue()),
                                        String.format("unique constraint not satisfied for column %s, value %d", dataTableCellList.get(column), cell.getStringValue())
                                );
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            ++column;
        }

        return true;
    }

    @Data
    private static final class NamedDataTable {

        private List<List<DataTableCell>> rows;
        private List<String> columns;
        private List<DataTableColumnType> types;
        private String name;

        public NamedDataTable() {
            rows = new ArrayList<>();
            columns = new ArrayList<>();
            types = new ArrayList<>();
        }

        public int getNumColumns() {
            return columns.size();
        }

        public int getNumRows() {
            return rows.size();
        }

        public String getColumnName(int column) {
            assert (column >= 0 && column < columns.size()) : String.format("DataTable [%s] getColumnName(): Invalid col number [%d].  Cols=[%d]", name, column, columns.size());
            return columns.get(column);
        }

        public DataTableColumnType getDataTypeForColumn(int column) {
            assert (column >= 0 && column < columns.size()) : String.format("DataTable [%s] getDataTypeForColumn(): Invalid col number [%d] for possible row [%d].  Cols=[%d]\n", name, column, 0, columns.size());
            return types.get(column);
        }
    }
}
