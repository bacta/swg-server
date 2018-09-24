package io.bacta.swg.datatable;

import com.google.common.base.Preconditions;
import io.bacta.swg.datatable.DataTableColumnType.DataType;
import io.bacta.swg.foundation.Tag;
import io.bacta.swg.iff.Iff;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by crush on 2/8/15.
 * Writes a named data table to an IFF Data Table.
 */
@Slf4j
public final class DataTableWriter {
    public void write(final NamedDataTable table, final Path outputPath) {
        final Iff iff = new Iff(outputPath.getFileName().toString());
        write(table, iff);

        iff.write(outputPath);
        iff.close();
    }

    public void write(final NamedDataTable table, final Iff iff) {
        Preconditions.checkNotNull(table);
        Preconditions.checkNotNull(iff);

        ensureIntegrity(table);

        iff.insertForm(DataTable.TAG_DTII);
        iff.insertForm(Tag.TAG_0001);

        writeColumns(table, iff);
        writeTypes(table, iff);
        writeRows(table, iff);

        iff.exitForm(Tag.TAG_0001);
        iff.exitForm();
    }

    private void writeColumns(final NamedDataTable table, final Iff iff) {
        iff.insertChunk(DataTable.TAG_COLS);

        final List<String> columns = table.getColumns();

        final int startingPosition = iff.getPositionInChunk();

        int totalColumns = 0;
        //Filter out the comment columns as we don't want to write these to the IFF since they are unnecessary data.
        for (int columnIndex = 0; columnIndex < columns.size(); ++columnIndex) {
            final String columnName = columns.get(columnIndex);
            final DataTableColumnType columnType = table.getDataTypeForColumn(columnIndex);

            if (!columnType.isComment()) {
                iff.insertChunkString(columnName);
                ++totalColumns;
            }
        }

        iff.seekWithinChunk(startingPosition, Iff.SeekType.BEGIN);
        iff.insertChunkData(totalColumns); //Write the number of columns.
        iff.seekWithinChunk(0,Iff.SeekType.END);

        iff.exitChunk(DataTable.TAG_COLS);
    }

    private void writeTypes(final NamedDataTable table, final Iff iff) {
        iff.insertChunk(DataTable.TAG_TYPE);

        for (final DataTableColumnType columnType : table.getTypes()) {
            if (!columnType.isComment()) {
                iff.insertChunkString(columnType.getTypeSpecString());
            }
        }

        iff.exitChunk(DataTable.TAG_TYPE);
    }

    private void writeRows(final NamedDataTable table, final Iff iff){
        iff.insertChunk(DataTable.TAG_ROWS);

        final List<List<DataTableCell>> rows = table.getRows();
        final int totalRows = rows.size();

        iff.insertChunkData(totalRows);

        for (List<DataTableCell> row : rows) {
            for (int cellIndex = 0; cellIndex < row.size(); ++cellIndex) {
                final DataTableCell cell = row.get(cellIndex);

                Preconditions.checkNotNull(cell);

                final DataType cellType = table.getTypes().get(cellIndex).getBasicType();

                switch (cellType) {
                    case INT: {
                        final int value = cell.getIntValue();
                        iff.insertChunkData(value);
                        break;
                    }
                    case FLOAT: {
                        final float value = cell.getFloatValue();
                        iff.insertChunkData(value);
                        break;
                    }
                    case STRING: {
                        final String value = cell.getStringValue();
                        iff.insertChunkString(value);
                        break;
                    }
                    case COMMENT: {
                        //Ignore comments.
                        break;
                    }
                    default: {
                        throw new UnsupportedOperationException(String.format("Unhandled cell type %s", cellType));
                    }
                }
            }
        }

        iff.exitChunk(DataTable.TAG_ROWS);
    }


    private void ensureIntegrity(final NamedDataTable ndt) {
        final List<String> columns = ndt.getColumns();
        final List<DataTableColumnType> types = ndt.getTypes();
        final List<List<DataTableCell>> rows = ndt.getRows();

        Preconditions.checkArgument(!columns.isEmpty(), ("empty columns"));
        Preconditions.checkArgument(columns.size() == types.size(), ("size mismatch"));
        Preconditions.checkArgument(!rows.isEmpty(), ("empty rows"));
        Preconditions.checkArgument(columns.size() == rows.get(0).size(), String.format("size mismatch %d %d", columns.size(), rows.get(0).size()));

        // Check unique constraints
        for (int columnIndex = 0; columnIndex < types.size(); ++columnIndex) {
            final DataTableColumnType columnType = types.get(columnIndex);

            if (columnType.areUniqueCellsRequired()) {
                for (final List<DataTableCell> dataTableCellList : rows) {
                    final DataTableCell currentCell = dataTableCellList.get(columnIndex);

                    //Loop over every cell and check if it has the same value as this cell.
                    for (final DataTableCell cell : dataTableCellList) {
                        //Skip the current cell of course...
                        if (cell == currentCell)
                            continue;

                        switch (columnType.getBasicType()) {
                            case INT: {
                                final int cellValue = cell.getIntValue();
                                Preconditions.checkArgument(currentCell.getIntValue() == cellValue,
                                        String.format("unique constraint not satisfied for column %s, value %d", columns.get(columnIndex), cellValue));
                                break;
                            }
                            case FLOAT: {
                                final float cellValue = cell.getFloatValue();
                                Preconditions.checkArgument(currentCell.getIntValue() == cellValue,
                                        String.format("unique constraint not satisfied for column %s, value %f", columns.get(columnIndex), cellValue));
                                break;
                            }
                            case STRING: {
                                final String cellValue = cell.getStringValue();
                                Preconditions.checkArgument(currentCell.getStringValue().equals(cellValue),
                                        String.format("unique constraint not satisfied for column %s, value %s", columns.get(columnIndex), cellValue));
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }
}
