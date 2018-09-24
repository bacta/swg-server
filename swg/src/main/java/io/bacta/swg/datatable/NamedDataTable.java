package io.bacta.swg.datatable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple container for a data table. Contains the columns, types, and row.
 */
@Getter
@RequiredArgsConstructor
public final class NamedDataTable {
    private final String name;
    private final List<String> columns;
    private final List<DataTableColumnType> types;
    private final List<List<DataTableCell>> rows;

    public NamedDataTable(final String name) {
        this.name = name;

        rows = new ArrayList<>();
        columns = new ArrayList<>();
        types = new ArrayList<>();
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
