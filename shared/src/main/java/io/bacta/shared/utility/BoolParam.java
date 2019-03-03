package io.bacta.shared.utility;


import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;

/**
 * Created by crush on 4/19/2016.
 */
public class BoolParam extends TemplateBase<Boolean, Boolean> {

    @Override
    protected TemplateBase<Boolean, Boolean> createNewParam() {
        return new BoolParam();
    }

    @Override
    public void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        final DataTypeId dataType = DataTypeId.from(iff.readByte());

        switch (dataType) {
            case SINGLE:
                setValue(iff.readBoolean());
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
                iff.insertChunkData(dataSingle);
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
