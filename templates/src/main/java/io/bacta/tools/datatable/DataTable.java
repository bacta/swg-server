package io.bacta.shared.datatable;

import com.google.common.base.Preconditions;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 2/8/15.
 */
public final class DataTable {
    public static final int TAG_DTII = Iff.createChunkId("DTII");
    public static final int TAG_COLS = Iff.createChunkId("COLS");
    public static final int TAG_ROWS = Iff.createChunkId("ROWS");
    public static final int TAG_TYPE = Iff.createChunkId("TYPE");

    private List<DataTableColumnType> types;
    private List<DataTableCell> cells;
    private List<String> columns;
    private TObjectIntMap<String> columnIndexMap;
    @Getter
    private String name;

    public DataTable() {
    }

    /**
     * Determines if a given column exists.
     *
     * @param column The name of the column.
     * @return True if the column exists. Otherwise, false.
     */
    public boolean doesColumnExist(final String column) {
        Preconditions.checkArgument(!this.columnIndexMap.isEmpty(), "DataTable[%s]: Column index map is empty.", this.name);

        return this.columnIndexMap.containsKey(column);
    }

    /**
     * Gets the name of the column at the given index.
     *
     * @param column The index of the column name which to receive.
     * @return Returns the name of the column.
     * @throws ArrayIndexOutOfBoundsException If the column index is outside the bounds of the columns list.
     */
    public String getColumnName(int column) {
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable[%s]: INVALID col number [%s]. Cols=[%s]", this.name, column, getNumColumns());

        return this.columns.get(column);
    }

    /**
     * Searches for the index of a column by name.
     *
     * @param column The name of the column.
     * @return The index of the column.
     * @throws NullPointerException If the specified column does not exist.
     */
    public int findColumnNumber(final String column) {
        Preconditions.checkArgument(!this.columnIndexMap.isEmpty(), "DataTable[%s]: Column index map is empty.", this.name);

        return this.columnIndexMap.get(column);
    }

    /**
     * Creates a new DataTableColumnType instance based on the type described.
     *
     * @param type             The type description for the new DataTableColumnType.
     * @param dataTableManager DataTableManager instance required for columns of type 'z' which must lookup additional
     *                         DataTables to serve as enums.
     * @return New instance of a DataTableColumnType based on the passed in type.
     */
    public static DataTableColumnType getDataType(final String type, final DataTableManager dataTableManager) {
        return new DataTableColumnType(type, dataTableManager);
    }

    /**
     * Gets the DataTableColumnType for the specified column.
     *
     * @param column The name of the column.
     * @return The DataTableColumnType for the specified column.
     * @throws NullPointerException If the specified column does not exist.
     */
    public DataTableColumnType getDataTypeForColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable[%s]: Column name [%s] is invalid.", this.name, column);

        return getDataTypeForColumn(columnIndex);
    }

    /**
     * Gets the DataTableColumnType for the specified column.
     *
     * @param column The index of the column.
     * @return The DataTableColumnType for the specified column.
     * @throws ArrayIndexOutOfBoundsException If the column index is outside the bounds of the types list.
     *                                        IllegalArgumentException If the column index is out of bounds.
     */
    public DataTableColumnType getDataTypeForColumn(int column) {
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable[%s]: INVALID col number [%s]. Cols=[%s]", this.name, column, getNumColumns());

        return this.types.get(column);
    }

    public int getIntValue(final String column, int row) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable[%s]: Column name [%s] is invalid.", this.name, column);

        return getIntValue(columnIndex, row);
    }

    public int getIntValue(int column, int row) {
        Preconditions.checkArgument(row >= 0 && row < getNumRows(), "DataTable[%s]: INVALID row number [%s]. Rows=[%s]", this.name, row, getNumRows());
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable[%s]: INVALID col number [%s]. Cols=[%s]", this.name, row, getNumColumns());

        final DataTableColumnType.DataType basicType = types.get(column).getBasicType();

        if (basicType == DataTableColumnType.DataType.Int) {
            final DataTableCell cell = getDataTableCell(column, row);

            Preconditions.checkArgument(cell.getType() == DataTableCell.CellType.Int, "Could not convert row %s column %s to int value.", row, column);

            return cell.getIntValue();

        } else if (basicType == DataTableColumnType.DataType.String) {
            final DataTableCell cell = getDataTableCell(column, row);

            Preconditions.checkArgument(cell.getType() == DataTableCell.CellType.String, "Could not convert row %s column %s to string value.", row, column);

            return cell.getStringValueCrc();
        }

        Preconditions.checkArgument(true, "DataTable[%s]: Wrong data type [%s] for col [%s].", this.name, basicType, column);

        return 0;
    }

    public int getIntDefaultForColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable[%s}: INVALID col number [%s]. Cols=[%s]", this.name, columnIndex, getNumColumns());

        return getIntDefaultForColumn(columnIndex);
    }

    /**
     * Gets the Integer default value for the given column.
     *
     * @param column The index of the column.
     * @return The default value for the given column.
     */
    public int getIntDefaultForColumn(int column) {
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable[%s]: INVALID col number [%s].  Cols=[%s]", this.name, column, getNumColumns());
        Preconditions.checkArgument(types.get(column).getBasicType() == DataTableColumnType.DataType.Int, "Wrong data type for column %s.", column);

        return Integer.parseInt(getDataTypeForColumn(column).mangleValue());
    }

    public float getFloatValue(final String column, int row) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getFloatValue(columnIndex, row);
    }

    public float getFloatValue(int column, int row) {
        Preconditions.checkArgument(row >= 0 && row < getNumRows(), "DataTable[%s]: INVALID row number [%s]. Rows=[%s]", this.name, row, getNumRows());
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable[%s]: INVALID col number [%s]. Cols=[%s]", this.name, row, getNumColumns());
        Preconditions.checkArgument(types.get(column).getBasicType() == DataTableColumnType.DataType.Float, "Wrong data type for column %s.", column);

        final DataTableCell cell = getDataTableCell(column, row);

        Preconditions.checkArgument(cell.getType() == DataTableCell.CellType.Float, "Could not convert row %s column %s to float value.", row, column);

        return cell.getFloatValue();
    }

    public float getFloatDefaultForColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getFloatDefaultForColumn(columnIndex);
    }

    public float getFloatDefaultForColumn(int column) {
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable[%s]: INVALID col number [%s]. Cols=[%s]", this.name, column, getNumColumns());
        Preconditions.checkArgument(types.get(column).getBasicType() == DataTableColumnType.DataType.Float, "Wrong data type for column %s.", column);

        return Float.parseFloat(getDataTypeForColumn(column).mangleValue());
    }


    public String getStringValue(final String column, int row) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getStringValue(columnIndex, row);
    }

    public String getStringValue(int column, int row) {
        Preconditions.checkArgument(row >= 0 && row < getNumRows(), "Row [%s] is invalid. Rows [%s]", row, getNumRows());
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "Column [%s] is invalid. Columns [%s]", column, getNumColumns());
        Preconditions.checkArgument(
                types.get(column).getBasicType() == DataTableColumnType.DataType.String,
                "Wrong data type for column %s (%s). Current data type is %s.",
                getColumnName(column),
                column,
                getDataTypeForColumn(column).getTypeSpecString());


        final DataTableCell cell = getDataTableCell(column, row);

        Preconditions.checkArgument(cell.getType() == DataTableCell.CellType.String, "Could not convert row %s column %s to string value.", row, column);

        return cell.getStringValue();
    }

    public String getStringDefaultForColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getStringDefaultForColumn(columnIndex);
    }

    public String getStringDefaultForColumn(int column) {
        Preconditions.checkArgument(column >= 0 && column < getNumColumns(), "DataTable [%s]: INVALID col number [%s]. Cols=[%s]", this.name, column, getNumColumns());
        Preconditions.checkArgument(types.get(column).getBasicType() == DataTableColumnType.DataType.String, "Wrong data type for column %s.", column);

        return getDataTypeForColumn(column).mangleValue();
    }

    public int[] getIntColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getIntColumn(columnIndex);
    }

    public int[] getIntColumn(int column) {
        final int numRows = getNumRows();
        final int[] array = new int[numRows];

        for (int row = 0; row < numRows; ++row) {
            array[row] = getIntValue(column, row);
        }

        return array;
    }

    public long[] getLongColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getLongColumn(columnIndex);
    }

    public long[] getLongColumn(int column) {
        final int numRows = getNumRows();
        final long[] array = new long[numRows];

        for (int row = 0; row < numRows; ++row) {
            array[row] = getIntValue(column, row); //Seems like a bug to me, but this is how it was coded...
        }

        return array;
    }

    public float[] getFloatColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getFloatColumn(columnIndex);
    }

    public float[] getFloatColumn(int column) {
        final int numRows = getNumRows();
        final float[] array = new float[numRows];

        for (int row = 0; row < numRows; ++row) {
            array[row] = getFloatValue(column, row);
        }

        return array;
    }

    public String[] getStringColumn(final String column) {
        final int columnIndex = findColumnNumber(column);
        Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getNumColumns(), "DataTable Column [%s] is invalid.", column);

        return getStringColumn(columnIndex);
    }

    public String[] getStringColumn(int column) {
        final int numRows = getNumRows();
        final String[] array = new String[numRows];

        for (int row = 0; row < numRows; ++row) {
            array[row] = getStringValue(column, row);
        }

        return array;
    }

    /**
     * Gets the total number of columns for this DataTable.
     *
     * @return The total number of columns.
     */
    public int getNumColumns() {
        return columns.size();
    }

    /**
     * Gets the total number of rows for this DataTable.
     *
     * @return The total number of rows.
     */
    public int getNumRows() {
        return cells.size() / columns.size();
    }

    public int searchColumnString(int column, final String searchValue) {
        throw new UnsupportedOperationException("Not implemented.");
    }


    public int searchColumnFloat(int column, float searchValue) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public int searchColumnInt(int column, int searchValue) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * Loads this DataTable from an IFF file.
     *
     * @param iff The IFF file to load.
     * @throws IllegalArgumentException If the DataTable is of an unknown file format.
     */
    public void load(final Iff iff, final DataTableManager dataTableManager) {
        iff.enterForm(DataTable.TAG_DTII);

        int version = iff.getCurrentName();

        if (version == Tag.TAG_0000) {
            load0000(iff, dataTableManager);
        } else if (version == Tag.TAG_0001) {
            load0001(iff, dataTableManager);
        } else {
            Preconditions.checkArgument(false, "UNKNOWN DataTable file format [%s].", Iff.getChunkName(version));
        }

        iff.exitForm(DataTable.TAG_DTII);

        buildColumnIndexMap();

        if (iff.getFileName() != null)
            this.name = iff.getFileName();
    }

    /**
     * Gets a DataTableCell at the given column and row.
     *
     * @param column The column index for the DataTableCell.
     * @param row    The row index for the DataTableCell.
     * @return The DataTableCell at the given column and row.
     */
    private DataTableCell getDataTableCell(int column, int row) {
        return cells.get(row * getNumColumns() + column);
    }

    private void readCell(final Iff iff, int column, int row) {
        final DataTableColumnType typeCol = types.get(column);

        switch (typeCol.getBasicType()) {
            case Int: {
                this.cells.add(new DataTableCell(iff.readInt()));
                break;
            }
            case Float: {
                this.cells.add(new DataTableCell(iff.readFloat()));
                break;
            }
            case String: {
                this.cells.add(new DataTableCell(iff.readString()));
                break;
            }
            case Unknown:
            case HashString:
            case Enum:
            case Bool:
            case BitVector:
            case Comment:
            case PackedObjVars:
            default:
                Preconditions.checkArgument(false, "Bad case.");
        }
    }

    private void load0000(final Iff iff, final DataTableManager dataTableManager) {
        iff.enterForm(Tag.TAG_0000);
        iff.enterChunk(DataTable.TAG_COLS);

        int numCols = iff.readInt();

        this.columns = new ArrayList<>(numCols);
        this.types = new ArrayList<>(numCols);

        for (int i = 0; i < numCols; ++i) {
            this.columns.add(iff.readString());
        }

        iff.exitChunk(DataTable.TAG_COLS);

        iff.enterChunk(DataTable.TAG_TYPE);

        for (int i = 0; i < numCols; ++i) {
            DataTableColumnType.DataType dataType = DataTableColumnType.DataType.values()[iff.readInt()];

            switch (dataType) {
                case Int: {
                    this.types.add(new DataTableColumnType("i", dataTableManager));
                    break;
                }
                case Float: {
                    this.types.add(new DataTableColumnType("f", dataTableManager));
                    break;
                }
                case String: {
                    this.types.add(new DataTableColumnType("s", dataTableManager));
                    break;
                }
                default: {
                    Preconditions.checkArgument(false, "UNKNOWN column type loaded from version 0000.");
                }
            }
        }

        iff.exitChunk(DataTable.TAG_TYPE);

        iff.enterChunk(DataTable.TAG_ROWS);

        int numRows = iff.readInt();

        this.cells = new ArrayList<>(numRows * numCols);

        for (int r = 0; r < numRows; ++r) {
            for (int c = 0; c < numCols; ++c) {
                readCell(iff, c, r);
            }
        }

        iff.exitChunk(DataTable.TAG_ROWS);
        iff.exitForm(Tag.TAG_0000);
    }

    private void load0001(Iff iff, final DataTableManager dataTableManager) {
        iff.enterForm(Tag.TAG_0001);

        iff.enterChunk(DataTable.TAG_COLS);

        final int numCols = iff.readInt();

        this.columns = new ArrayList<>(numCols);
        this.types = new ArrayList<>(numCols);

        for (int i = 0; i < numCols; ++i) {
            this.columns.add(iff.readString());
        }

        iff.exitChunk(DataTable.TAG_COLS);
        iff.enterChunk(DataTable.TAG_TYPE);

        for (int i = 0; i < numCols; ++i) {
            this.types.add(new DataTableColumnType(iff.readString(), dataTableManager));
        }

        iff.exitChunk(DataTable.TAG_TYPE);

        iff.enterChunk(DataTable.TAG_ROWS);

        final int numRows = iff.readInt();

        this.cells = new ArrayList<>(numRows * numCols);

        for (int r = 0; r < numRows; ++r) {
            for (int c = 0; c < numCols; ++c) {
                readCell(iff, c, r);
            }
        }

        iff.exitChunk(DataTable.TAG_ROWS);
        iff.exitForm(Tag.TAG_0001);

    }

    /**
     * Builds a reverse lookup index for finding the index of a column by name.
     *
     * @
     */
    private void buildColumnIndexMap() {
        final int numCols = this.columns.size();

        this.columnIndexMap = new TObjectIntHashMap<>(numCols);

        for (int columnIndex = 0; columnIndex < numCols; ++columnIndex) {
            final String columnName = this.columns.get(columnIndex);
            this.columnIndexMap.put(columnName, columnIndex);
        }
    }
}
