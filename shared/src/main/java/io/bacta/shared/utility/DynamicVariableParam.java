package io.bacta.shared.utility;


import io.bacta.shared.iff.Iff;
import io.bacta.shared.tre.foundation.DataResourceList;
import io.bacta.shared.tre.template.ObjectTemplate;

/**
 * Created by crush on 4/19/2016.
 */
public class DynamicVariableParam extends TemplateBase<DynamicVariableParamData, DynamicVariableParamData> {
    private boolean extendingBaseList;

    @Override
    protected TemplateBase<DynamicVariableParamData, DynamicVariableParamData> createNewParam() {
        return new DynamicVariableParam();
    }

    @Override
    protected void cleanSingleParam() {

    }

    public void setExtendingBaseList(boolean flag) {
        extendingBaseList = flag;
    }

    public boolean isExtendingBaseList() {
        return extendingBaseList;
    }

    public void setIsLoaded() {
        loaded = true;
    }

    @Override
    public void loadFromIff(final DataResourceList<ObjectTemplate> resourceList, Iff iff) {

    }

    @Override
    public void saveToIff(final DataResourceList<ObjectTemplate> resourceList, Iff iff) {

    }
}
