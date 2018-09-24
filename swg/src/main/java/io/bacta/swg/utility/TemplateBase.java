package io.bacta.swg.utility;

import io.bacta.swg.foundation.DataResourceList;
import io.bacta.swg.iff.Iff;
import io.bacta.swg.template.ObjectTemplate;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by crush on 4/19/2016.
 */
public abstract class TemplateBase<DataType, ReturnType> {
    protected static final Random random = new Random();

    protected DataType dataSingle; //storage for simple-type data
    protected DataTypeId dataType; //type of data for this param
    protected TemplateBaseData data; //storage for complex-type data
    protected boolean loaded; //flag that parameter has been loaded

    public TemplateBase() {
        dataType = DataTypeId.NONE;
    }

    /**
     * Does cleanup for a single-value parameter.
     */
    protected void cleanSingleParam() {
    }

    /**
     * Loads a weighted list from an Iff file.
     *
     * @param iff the file to load from.
     */
    @SuppressWarnings("unchecked")
    protected void loadWeightedListFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        int count = iff.readInt();

        if (count <= 0)
            return;

        for (int i = 0; i < count; ++i) {
            final WeightedValue weightedValue = new WeightedValue();
            weightedValue.weight = iff.readInt();
            weightedValue.value = createNewParam();
            weightedValue.value.loadFromIff(resourceList, iff);

            ((WeightedList) data).add(weightedValue);
        }

        loaded = true;
    }

    @SuppressWarnings("unchecked")
    protected void saveWeightedListToIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        final WeightedList weightedList = (WeightedList) data;

        final int count = weightedList.size();
        iff.insertChunkData(count);

        for (final WeightedValue weightedValue : weightedList) {
            final int weight = weightedValue.weight;
            iff.insertChunkData(weight);
            weightedValue.value.saveToIff(resourceList, iff);
        }
    }

    protected abstract TemplateBase<DataType, ReturnType> createNewParam();

    protected DataType getSingle() {
        return dataSingle;
    }

    protected DataType getRange() {
        throw new UnsupportedOperationException();
    }

    protected DataType getDieRoll() {
        throw new UnsupportedOperationException();
    }

    public void setValue(final DataType value) {
        cleanData();
        dataType = DataTypeId.SINGLE;
        dataSingle = value;
        loaded = true;
    }

    protected void setValue(final DataType minValue, final DataType maxValue) {
        cleanData();
        dataType = DataTypeId.RANGE;
        final Range range = new Range();
        range.minValue = minValue;
        range.maxValue = maxValue;
        data = range;
        loaded = true;
    }

    protected void setValue(final DataType numDice, final DataType dieSides, final DataType base) {
        cleanData();
        dataType = DataTypeId.DIE_ROLL;
        final DieRoll dieRoll = new DieRoll();
        dieRoll.numDice = numDice;
        dieRoll.dieSides = dieSides;
        dieRoll.base = base;
        data = dieRoll;
        loaded = true;
    }

    public void setValue(final WeightedList list) {
        cleanData();
        dataType = DataTypeId.WEIGHTED_LIST;
        data = list;
        loaded = true;
    }


    public DataTypeId getType() {
        return dataType;
    }

    public void cleanData() {
        switch (dataType) {
            case SINGLE:
                cleanSingleParam();
                break;
            case WEIGHTED_LIST:
            case RANGE:
            case DIE_ROLL:
            case NONE:
            default:
                data = null;
                break;
        }
        dataType = DataTypeId.NONE;
        loaded = false;
    }

    public abstract void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff);

    public abstract void saveToIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff);

    @SuppressWarnings("unchecked")
    public ReturnType getValue() {
        switch (dataType) {
            case SINGLE:
                return (ReturnType) getSingle();
            case WEIGHTED_LIST: {
                int weight = random.nextInt(100) + 1;

                for (final WeightedValue weightedValue : (WeightedList) data) {
                    weight -= weightedValue.weight;

                    if (weight <= 0)
                        return (ReturnType) weightedValue.value.getValue();
                }

                throw new IllegalStateException("weighted list does not equal 100");
            }
            case RANGE:
                return (ReturnType) getRange();
            case DIE_ROLL:
                return (ReturnType) getDieRoll();
            case NONE:
            default:
                throw new IllegalStateException(
                        String.format("unknown data type %s for template param", dataType.toString()));
        }
    }

    /**
     * Gets the raw range object from this parameter. Requires the data type to be DataTypeId.RANGE.
     *
     * @return The range object for this parameters data.
     */
    @SuppressWarnings("unchecked")
    public Range getRawRange() {
        if (dataType == DataTypeId.RANGE)
            return (Range) data;

        return null;
    }

    @SuppressWarnings("unchecked")
    public DieRoll GetRawDieRoll() {
        if (dataType == DataTypeId.DIE_ROLL)
            return (DieRoll) data;

        return null;
    }

    @SuppressWarnings("unchecked")
    public WeightedList GetRawWeightedList() {
        if (dataType == DataTypeId.WEIGHTED_LIST)
            return (WeightedList) data;

        return null;
    }

    public boolean isLoaded() {
        return loaded;
    }


    private interface TemplateBaseData {
    }

    public class WeightedValue {
        TemplateBase<DataType, ReturnType> value;
        int weight;
    }

    public class WeightedList extends ArrayList<WeightedValue> implements TemplateBaseData {
    }

    public class Range implements TemplateBaseData {
        DataType minValue;
        DataType maxValue;

        Range() {
        }

        Range(final Range range) {
            this.minValue = range.minValue;
            this.maxValue = range.maxValue;
        }
    }

    public class DieRoll implements TemplateBaseData {
        DataType numDice;
        DataType dieSides;
        DataType base;

        DieRoll() {
        }

        DieRoll(final DieRoll dieRoll) {
            this.numDice = dieRoll.numDice;
            this.dieSides = dieRoll.dieSides;
            this.base = dieRoll.base;
        }
    }

    public enum DataTypeId {
        NONE((byte) 0),
        SINGLE((byte) 1),
        WEIGHTED_LIST((byte) 2),
        RANGE((byte) 3),
        DIE_ROLL((byte) 4);

        private static final DataTypeId[] values = values();
        public final byte value;

        DataTypeId(final byte value) {
            this.value = value;
        }

        public static DataTypeId from(final byte value) {
            for (int i = 0; i < values.length; ++i)
                if (values[i].value == value)
                    return values[i];

            throw new IllegalArgumentException(
                    String.format("Invalid DataTypeId %d", value));
        }
    }
}
