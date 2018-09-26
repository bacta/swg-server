package io.bacta.game.crafting;

import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.container.SlottedContainer;
import io.bacta.swg.object.AttributeList;
import io.bacta.swg.object.SharedObjectAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class CraftingService {
    //private final int outputSlotId;

    @Inject
    public CraftingService(SlotIdManager slotIdManager) {
        //outputSlotId = slotIdManager.findSlotId(MANUFACTURING_OUTPUT_SLOT_NAME);
    }

    public void appendAttributsForCraftingTool(final TangibleObject craftingTool, AttributeList attributeList) {
        float craftingEffectiveness = 0.0f;

        //craftingEffectiveness = getObjVars().getItem(OBJVAR_CRAFTING_STATIONMOD);

        attributeList.add(SharedObjectAttributes.craft_tool_effectiveness,
                String.format("%.2f", craftingEffectiveness));

        //int prototypeTimeLeft = getCraftingToolPrototypeTime();
        int prototypeTimeLeft = 0;

        if (prototypeTimeLeft > 0) {
            attributeList.add(SharedObjectAttributes.craft_tool_status, CraftingStrings.WORKING);
            attributeList.add(SharedObjectAttributes.craft_tool_time, Integer.toString(prototypeTimeLeft));
        } else {
            boolean hasPrototype = false;

            final SlottedContainer slottedContainer = craftingTool.getSlottedContainerProperty();

            if (slottedContainer != null) {
                //if (getObjectInSlot(outputSlotId)) {
                    //hasPrototype = true;
                //}
            }

            if (hasPrototype) {
                attributeList.add(SharedObjectAttributes.craft_tool_status, CraftingStrings.FINISHED);
            } else {
                attributeList.add(SharedObjectAttributes.craft_tool_status, CraftingStrings.READY);
            }
        }
    }
}
