package io.bacta.shared.utility;

import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;

/**
 * Created by crush on 4/19/2016.
 */
public class TriggerVolumeParam extends TemplateBase<TriggerVolumeParamData, TriggerVolumeData> {
    @Override
    protected TemplateBase<TriggerVolumeParamData, TriggerVolumeData> createNewParam() {
        return null;
    }

    @Override
    protected void cleanSingleParam() {
        dataSingle.name.cleanData();
        dataSingle.radius.cleanData();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TriggerVolumeData getValue() {
        if (dataType == DataTypeId.SINGLE) {
            return new TriggerVolumeData(
                    dataSingle.name.getValue(),
                    dataSingle.radius.getValue());
        } else if (dataType == DataTypeId.WEIGHTED_LIST) {
            int weight = random.nextInt(100) + 1;

            for (final WeightedValue weightedValue : (WeightedList) data) {
                weight -= weightedValue.weight;

                if (weight <= 0)
                    return weightedValue.value.getValue();
            }

            throw new IllegalStateException("weighted list does not equal 100");
        }

        return new TriggerVolumeData();
    }

    @Override
    public void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        final DataTypeId dataType = DataTypeId.from(iff.readByte());

        switch (dataType) {
            case SINGLE:
                final TriggerVolumeParamData data = new TriggerVolumeParamData();
                data.name.loadFromIff(resourceList, iff);
                data.radius.loadFromIff(resourceList, iff);
                setValue(data);
                loaded = true;
                break;
            case WEIGHTED_LIST:
                setValue(new WeightedList());
                loadWeightedListFromIff(resourceList, iff);
                break;
            case NONE:
                cleanData();
                break;
            case RANGE:
            case DIE_ROLL:
            default:
                break;
        }
    }

    @Override
    public void saveToIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        iff.insertChunkData(dataType.value);

        switch (dataType) {
            case SINGLE:
                dataSingle.name.saveToIff(resourceList, iff);
                dataSingle.radius.saveToIff(resourceList, iff);
                break;
            case WEIGHTED_LIST:
                saveWeightedListToIff(resourceList, iff);
                break;
            case NONE:
                break;
            case RANGE:
            case DIE_ROLL:
            default:
                break;
        }
    }
}
