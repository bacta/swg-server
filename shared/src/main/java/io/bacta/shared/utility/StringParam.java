package io.bacta.shared.utility;


import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;

/**
 * Created by crush on 4/19/2016.
 */
public class StringParam extends TemplateBase<String, String> {
    @Override
    protected TemplateBase<String, String> createNewParam() {
        return new StringParam();
    }

    @Override
    protected void cleanSingleParam() {
        dataSingle = "";
    }

    @Override
    public void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, final Iff iff) {
        final DataTypeId dataType = DataTypeId.from(iff.readByte());

        switch (dataType) {
            case SINGLE:
                final String value = iff.readString();
                setValue(fixFileSeparators(value));
                loaded = true;
                break;
            case WEIGHTED_LIST:
                setValue(new WeightedList());
                loadWeightedListFromIff(resourceList, iff);
                break;
            case NONE:
                cleanData();
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

    private String fixFileSeparators(final String filePath) {
        final StringBuilder sb = new StringBuilder(filePath.length());

        for (char c : filePath.toCharArray())
            sb.append(c == '\\' ? '/' : c);

        return sb.toString();
    }
}
