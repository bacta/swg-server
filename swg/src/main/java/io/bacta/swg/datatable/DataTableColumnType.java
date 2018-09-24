package io.bacta.swg.datatable;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.bacta.swg.util.SOECRC32;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 2/8/15.
 */
public final class DataTableColumnType {
    private static final Logger logger = LoggerFactory.getLogger(DataTableColumnType.class);

    private static final char valueOpeningDelimiter = '[';
    private static final char valueClosingDelimiter = ']';
    private static final char ENUM_OPENING_DELIMITER = '(';
    private static final char ENUM_CLOSING_DELIMITER = ')';

    @Getter
    private String typeSpecString;
    @Getter
    private DataType type;
    @Getter
    private DataType basicType;
    private String defaultValue;
    private TObjectIntMap<String> enumMap;
    @Getter
    private DataTableCell defaultCell;

    /**
     * Creates a new instance of a DataTableColumnType
     *
     * @param desc             The description for the column type.
     * @param dataTableManager An instance to a DataTableManager. This is only used for types of 'z' which need to
     *                         lookup other DataTables to use for the enum values.
     */
    public DataTableColumnType(
            final String desc,
            final DataTableManager dataTableManager) {

        this.typeSpecString = desc;
        this.type = DataType.UNKNOWN;
        this.basicType = DataType.UNKNOWN;

        char descType = Character.toLowerCase(desc.charAt(0));
        this.defaultValue = DataTableUtility.getDelimitedString(desc, valueOpeningDelimiter, valueClosingDelimiter);

        if (descType == 'i') { //DataType::INT
            this.type = DataType.INT;
            this.basicType = DataType.INT;

            if (this.defaultValue.length() == 0)
                this.defaultValue = "0";

        } else if (descType == 'f') { //DataType::FLOAT
            this.type = DataType.FLOAT;
            this.basicType = DataType.FLOAT;

            if (this.defaultValue.length() == 0)
                this.defaultValue = "0";

        } else if (descType == 's') { //DataType::STRING
            this.type = DataType.STRING;
            this.basicType = DataType.STRING;

        } else if (descType == 'c') { //DataType::COMMENT
            this.type = DataType.COMMENT;
            this.basicType = DataType.COMMENT;

        } else if (descType == 'h') { //DataType::HASH_STRING
            this.type = DataType.HASH_STRING;
            this.basicType = DataType.INT;

        } else if (descType == 'p') { //DataType::PACKED_OBJ_VARS
            this.type = DataType.PACKED_OBJ_VARS;
            this.basicType = DataType.STRING;

        } else if (descType == 'b') { //DataType::BOOL
            this.type = DataType.BOOL;
            this.basicType = DataType.INT;

            if (!"1".equals(this.defaultValue))
                this.defaultValue = "0";

        } else if (descType == 'e') { //DataType::ENUM
            this.type = DataType.ENUM;
            this.basicType = DataType.INT;
            this.enumMap = new TObjectIntHashMap<>(); //We don't know how many entries up front.

            final String enumList = DataTableUtility.getDelimitedString(desc, ENUM_OPENING_DELIMITER, ENUM_CLOSING_DELIMITER) + ',';

            int currentPosition = 0;
            int equalsPosition;
            while ((equalsPosition = enumList.indexOf('=', currentPosition)) != -1) {
                int endPosition = enumList.indexOf(',', equalsPosition);

                final String label = enumList.substring(currentPosition, equalsPosition);
                final String value = enumList.substring(equalsPosition + 1, endPosition);
                currentPosition = endPosition + 1; //Skip the comma

                this.enumMap.put(label, (int) Long.parseLong(value));
            }

            if (!this.enumMap.containsKey(this.defaultValue)) {
                logger.warn("Default value [%s] is not a member of enumeration.", this.defaultValue);
                this.basicType = DataType.UNKNOWN;
            }

        } else if (descType == 'v') { //DataType::BIT_VECTOR
            this.type = DataType.BIT_VECTOR;
            this.basicType = DataType.INT;
            this.enumMap = new TObjectIntHashMap<>();

            final String enumList = DataTableUtility.getDelimitedString(desc, ENUM_OPENING_DELIMITER, ENUM_CLOSING_DELIMITER) + ',';

            int currentPosition = 0;
            int equalsPosition;
            while ((equalsPosition = enumList.indexOf('=', currentPosition)) != -1) {
                int endPosition = enumList.indexOf(',', equalsPosition);

                final String label = enumList.substring(currentPosition, equalsPosition);
                final String value = enumList.substring(equalsPosition + 1, endPosition);
                currentPosition = endPosition + 1;

                int bit = (int) Long.parseUnsignedLong(value);

                if ((bit < 1) || (bit > 32)) {
                    logger.warn("Flags value [%s] is not a whole number from 1 to 32.", label);
                    this.basicType = DataType.UNKNOWN;
                }

                this.enumMap.put(label, 1 << (bit - 1));
            }

            if (!this.defaultValue.equals("NONE")) {
                if (!this.enumMap.containsKey(this.defaultValue)) {
                    logger.warn("Default value [%s] is not a member of enumeration.", this.defaultValue);
                    this.basicType = DataType.UNKNOWN;
                }
            }

        } else if (descType == 'z') {
            this.type = DataType.ENUM;

            if (dataTableManager != null) {
                this.basicType = DataType.INT;

                final String fileName = DataTableUtility.getDelimitedString(desc, ENUM_OPENING_DELIMITER, ENUM_CLOSING_DELIMITER);

                final DataTable enumTable = dataTableManager.getTable(fileName, true);

                if (enumTable == null) {
                    this.basicType = DataType.UNKNOWN;
                    return;
                }

                int enumCount = enumTable.getNumRows();
                this.enumMap = new TObjectIntHashMap<>(enumCount);

                String firstKey = "";

                for (int i = 0; i < enumCount; ++i) {
                    final String key = enumTable.getStringValue(0, i).trim();
                    int value = enumTable.getIntValue(1, i);

                    if (i == 0)
                        firstKey = key;

                    this.enumMap.put(key, value);
                }

                if (!this.enumMap.containsKey(this.defaultValue)) {
                    this.defaultValue = firstKey;
                }

            } else {
                logger.warn("DataTableType 'z' requires an instance to a DataTableManager so that it may look up other data tables.");
                this.basicType = DataType.UNKNOWN;
            }

        } else {
            this.basicType = DataType.UNKNOWN;
        }


        createDefaultCell();
    }

    public String mangleValue() {
        return mangleValue("");
    }

    /**
     * Seems to return the default value if the basic type is not DataType.INT, and the starting value is not
     * empty when the default value is equal to "required" or "unique". If is of basic type DataType.INT, then
     * then different logic takes place depending on the actual type.
     * <ul>
     * <li>
     * DataType.BOOL - Return the value if it's equal to "0" or "1".
     * </li>
     * <li>
     * DataType.HASH_STRING - Return the CRC of the value passed in (or default value if starting
     * value is empty).
     * </li>
     * <li>
     * DataType.ENUM - returns the enum value for the entry with label equal to starting or default value.
     * </li>
     * <li>
     * DataType.BIT_VECTOR - returns the bit value for the entry with the label equal to the starting
     * or default value.
     * </li>
     * </ul>
     *
     * @param startingValue The value to start the manging process.
     * @return The mangled value.
     * @throws IllegalArgumentException - If the starting value is required or must be unique, but nothing was passed.
     *                                  Also, if mangling process fails for the starting value.
     */
    public String mangleValue(final String startingValue) {
        String value = startingValue;

        if (startingValue.isEmpty()) {
            if (this.defaultValue.equals("required") || this.defaultValue.equals("unique")) {
                return startingValue; //Apparently we should just send back what we started with here.
            }

            value = this.defaultValue;
        }

        //special validation code for packed objvars???? why
        if (this.type == DataType.PACKED_OBJ_VARS) {
            // nameString|typeInt|valueString|nameString|typeInt|valueString|$|
            // where || may be used in the string fields to represent a |
            //for (int i = 0; i < value.length(); ++i) {
            //    if (value.charAt(i) == '$' && value.charAt(i + 1) == '|' && i + 2 == value.length())
            //        break; //END of packedObjVars found...

            //}
        }

        //Only basic type DataType.INT is a complex type that requires mangling other than default values.
        if (this.basicType != DataType.INT || this.type == DataType.INT)
            return value;

        switch (this.type) {
            case BOOL:
                if (value.equals("0") || value.equals("1"))
                    return value;

                break;

            case HASH_STRING:
                if (value.length() > 0)
                    return String.valueOf(SOECRC32.hashCode(value));

                return String.valueOf(SOECRC32.NULL);

            case ENUM:
                return String.valueOf(lookupEnum(value));

            case BIT_VECTOR:
                return String.valueOf(lookupBitVector(value));

            default:
                break;
        }

        throw new IllegalArgumentException("Unable to mangle value.");
    }

    public boolean areUniqueCellsRequired() {
        return this.defaultValue.equals("unique");
    }

    public boolean isComment() {
        return DataType.COMMENT.equals(type);
    }

    private int lookupEnum(final String label) {
        if (this.enumMap == null)
            throw new NullPointerException("This data table column type does not have an enum.");

        return this.enumMap.get(label.trim());
    }

    private int lookupBitVector(final String label) {
        try {
            if (label.equals("NONE")) {
                return 0;
            }

            int result = 0;
            final String localLabel = label + ',';

            int currentPosition = 0;
            int position;
            while ((position = localLabel.indexOf(',')) != -1) {
                final String subLabel = localLabel.substring(currentPosition, position).trim();
                int subResult = lookupEnum(subLabel);
                result |= subResult;
                currentPosition = position;
            }

            return result;
        } catch (Exception ex) {
            logger.error("Exception: %s", ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Creates the default cell internally. Run after the parsing process is completed.
     */
    private void createDefaultCell() {
        if (this.defaultCell != null)
            return;

        final String value = mangleValue();

        switch (this.basicType) {
            case INT:
                this.defaultCell = new DataTableCell((int) Long.parseLong(value));
                break;
            case FLOAT:
                this.defaultCell = new DataTableCell(Float.parseFloat(value));
                break;
            case STRING:
            case COMMENT:
            case UNKNOWN:
            default:
                this.defaultCell = new DataTableCell(value);
                break;
        }
    }

    public enum DataType {
        INT(0),
        FLOAT(1),
        STRING(2),
        UNKNOWN(3),
        COMMENT(4),
        HASH_STRING(5),
        ENUM(6),
        BOOL(7),
        PACKED_OBJ_VARS(8),
        BIT_VECTOR(9);

        public final int value;

        DataType(final int value) {
            this.value = value;
        }
    }
}
