package io.bacta.shared.utility;

import io.bacta.shared.iff.Iff;
import io.bacta.shared.tre.foundation.DataResourceList;
import io.bacta.shared.tre.template.ObjectTemplate;

/**
 * Created by crush on 4/19/2016.
 */
public class VectorParam extends TemplateBase<VectorParamData, VectorParamData> {
    @Override
    protected TemplateBase<VectorParamData, VectorParamData> createNewParam() {
        return new VectorParam();
    }

    @Override
    protected void cleanSingleParam() {
        dataSingle.x.cleanData();
        dataSingle.y.cleanData();
        dataSingle.z.cleanData();
        dataSingle.radius.cleanData();
    }

    @Override
    public void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        final DataTypeId dataType = DataTypeId.from(iff.readByte());

        switch (dataType) {
            case SINGLE:
                this.dataType = DataTypeId.SINGLE;
                dataSingle.ignoreY = iff.readBoolean();
                dataSingle.x.loadFromIff(resourceList, iff);
                if (!dataSingle.ignoreY)
                    dataSingle.y.loadFromIff(resourceList, iff);
                dataSingle.z.loadFromIff(resourceList, iff);
                dataSingle.radius.loadFromIff(resourceList, iff);
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
                iff.insertChunkData(dataSingle.ignoreY);
                dataSingle.x.saveToIff(resourceList, iff);
                if (dataSingle.ignoreY)
                    dataSingle.y.saveToIff(resourceList, iff);
                dataSingle.z.saveToIff(resourceList, iff);
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
