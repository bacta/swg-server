package io.bacta.shared.utility;


import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import org.checkerframework.com.google.common.base.Preconditions;

/**
 * Created by crush on 4/19/2016.
 */
public class FloatParam extends TemplateBase<Float, Float> {
    private byte dataDeltaType; //if '+' or '-', this param is a delta on a derived template param.

    public FloatParam() {
        this.dataDeltaType = ' ';
    }


    public byte getDeltaType() {
        return dataDeltaType;
    }

    public void setDeltaType(final byte type) {
        dataDeltaType = type;
    }

    /**
     * Returns the minimum value that a getValue() can return. Not supported for weighted list.
     *
     * @return the minimum getValue() return value.
     */
    @SuppressWarnings("unchecked")
    public float getMinValue() {
        switch (dataType) {
            case SINGLE:
                return dataSingle;
            case RANGE:
                Preconditions.checkNotNull(data);
                final Range range = (Range) data;
                return range.minValue;
            case DIE_ROLL:
                Preconditions.checkNotNull(data);
                final DieRoll dieRoll = (DieRoll) data;
                return dieRoll.base + dieRoll.numDice;
            case NONE:
            case WEIGHTED_LIST:
            default:
                break;
        }
        return 0;
    }

    /**
     * Returns the maximum value that a getValue() can return. Not supported for weighted list.
     *
     * @return the maximum getValue() return value.
     */
    @SuppressWarnings("unchecked")
    public float getMaxValue() {
        switch (dataType) {
            case SINGLE:
                return dataSingle;
            case RANGE:
                Preconditions.checkNotNull(data);
                final Range range = (Range) data;
                return range.maxValue;
            case DIE_ROLL:
                Preconditions.checkNotNull(data);
                final DieRoll dieRoll = (DieRoll) data;
                return dieRoll.base + dieRoll.numDice * dieRoll.dieSides;
            case NONE:
            case WEIGHTED_LIST:
            default:
                break;
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public Range getRangeStruct() {
        return dataType == DataTypeId.RANGE
                ? (Range) data
                : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Float getRange() {
        Preconditions.checkState(dataType == DataTypeId.RANGE, "getRange on non-range integer param");
        Preconditions.checkNotNull(data);

        final Range range = (Range) data;
        return random.nextFloat() * (range.maxValue - range.minValue) + range.minValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Float getDieRoll() {
        Preconditions.checkState(dataType == DataTypeId.RANGE, "getRange on non-range integer param");
        Preconditions.checkNotNull(data);

        final DieRoll dieRoll = (DieRoll) data;
        float result = dieRoll.base;

        for (int i = 0; i < dieRoll.numDice; ++i)
            result += random.nextFloat() * dieRoll.dieSides + 1;

        return result;
    }

    @Override
    protected TemplateBase<Float, Float> createNewParam() {
        return new FloatParam();
    }

    @Override
    public void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        final DataTypeId dataType = DataTypeId.from(iff.readByte());
        this.dataDeltaType = iff.readByte();

        switch (dataType) {
            case SINGLE:
                setValue(iff.readFloat());
                loaded = true;
                break;
            case WEIGHTED_LIST:
                setValue(new WeightedList());
                loadWeightedListFromIff(resourceList, iff);
                break;
            case RANGE:
                final float minValue = iff.readFloat();
                final float maxValue = iff.readFloat();
                setValue(minValue, maxValue);
                loaded = true;
                break;
            case DIE_ROLL:
                final float numDice = iff.readFloat();
                final float dieSides = iff.readFloat();
                final float base = iff.readFloat();
                setValue(numDice, dieSides, base);
                loaded = true;
                break;
            case NONE:
                cleanData();
                break;
            default:
                break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveToIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        iff.insertChunkData(dataType.value);
        iff.insertChunkData(dataDeltaType);

        switch (dataType) {
            case SINGLE:
                iff.insertChunkData(dataSingle);
                break;
            case WEIGHTED_LIST:
                saveWeightedListToIff(resourceList, iff);
                break;
            case RANGE:
                Preconditions.checkNotNull(data);
                final Range range = (Range) data;
                iff.insertChunkData(range.minValue);
                iff.insertChunkData(range.maxValue);
                break;
            case DIE_ROLL:
                Preconditions.checkNotNull(data);
                final DieRoll dieRoll = (DieRoll) data;
                iff.insertChunkData(dieRoll.numDice);
                iff.insertChunkData(dieRoll.dieSides);
                iff.insertChunkData(dieRoll.base);
                break;
            case NONE:
                break;
            default:
                break;
        }
    }
}