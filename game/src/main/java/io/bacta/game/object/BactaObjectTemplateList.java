package io.bacta.game.object;

import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.swg.container.ArrangementDescriptorList;
import io.bacta.swg.container.SlotDescriptorList;
import io.bacta.swg.template.ObjectTemplate;
import io.bacta.swg.template.ObjectTemplateList;
import io.bacta.swg.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class BactaObjectTemplateList extends ObjectTemplateList {
    private final ArrangementDescriptorList arrangementDescriptorList;
    private final SlotDescriptorList slotDescriptorList;

    @Inject
    public BactaObjectTemplateList(final TreeFile treeFile,
                                   final ArrangementDescriptorList arrangementDescriptorList,
                                   final SlotDescriptorList slotDescriptorList) {
        super(treeFile);
        this.arrangementDescriptorList = arrangementDescriptorList;
        this.slotDescriptorList = slotDescriptorList;
    }

    @Override
    protected void postFetch(final ObjectTemplate objectTemplate) {
        if (objectTemplate instanceof SharedObjectTemplate) {
            final SharedObjectTemplate sharedObjectTemplate = (SharedObjectTemplate) objectTemplate;

            final String slotFilename = sharedObjectTemplate.getSlotDescriptorFilename();
            final String arrangementFilename = sharedObjectTemplate.getArrangementDescriptorFilename();

            if (slotFilename != null && !slotFilename.isEmpty())
                sharedObjectTemplate.setSlotDescriptor(slotDescriptorList.fetch(slotFilename));

            if (arrangementFilename != null && !arrangementFilename.isEmpty())
                sharedObjectTemplate.setArrangementDescriptor(arrangementDescriptorList.fetch(arrangementFilename));
        }
    }
}
