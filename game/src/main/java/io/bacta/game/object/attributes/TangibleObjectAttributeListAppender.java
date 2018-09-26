package io.bacta.game.object.attributes;

import io.bacta.game.crafting.CraftingService;
import io.bacta.game.crafting.SerialNumberGenerator;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.shared.object.template.SharedObjectTemplate.GameObjectType;
import io.bacta.swg.container.VolumeContainer;
import io.bacta.swg.object.AttributeList;
import io.bacta.swg.object.SharedObjectAttributes;
import io.bacta.swg.util.NetworkId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Scope("prototype")
@Component
@AppendsAttributesFor(TangibleObject.class)
public final class TangibleObjectAttributeListAppender implements AttributeListAppender<TangibleObject> {
    private final ServerObjectAttributeListAppender serverObjectAppender;
    private final CraftingService craftingService;

    @Inject
    public TangibleObjectAttributeListAppender(ServerObjectAttributeListAppender serverObjectAppender, CraftingService craftingService) {
        this.serverObjectAppender = serverObjectAppender;
        this.craftingService = craftingService;
    }

    @Override
    public void append(TangibleObject object, AttributeList attributeList) {
        this.serverObjectAppender.append(object, attributeList);

        //If it's not a creature object, has hit points, and isn't set to invulnerable, then append the hit points.
        final CreatureObject creatureObject = object.asCreatureObject();

        if (creatureObject == null) {
            final int maxHitPoints = object.getMaxHitPoints();

            if (maxHitPoints > 0 && !object.isInvulnerable()) {
                final int damageTaken = object.getDamageTaken();

                attributeList.add(SharedObjectAttributes.condition,
                        String.format("%d/%d", maxHitPoints - damageTaken, maxHitPoints));
            }

            //Also, need to check if players can move the object or not. If yes, then we append the volume.
            final ServerObjectTemplate serverObjectTemplate = (ServerObjectTemplate) object.getObjectTemplate();

            if (serverObjectTemplate != null) {
                boolean playerCanMoveObject = false;

                //Check the move flags on the objects template to see if players are permitted to move it.
                for (int i = 0; i < serverObjectTemplate.getMoveFlagsCount(); ++i) {
                    if (serverObjectTemplate.getMoveFlags(i) == ServerObjectTemplate.MoveFlags.MF_player) {
                        playerCanMoveObject = true;
                        break;
                    }
                }

                //getCacheVersion()...
                if (playerCanMoveObject) {
                    attributeList.add(SharedObjectAttributes.volume,
                            String.format("%d", Math.max(1, object.getVolume())));
                }
            }
        }

        //Check the game object type and append any required attributes.
        final GameObjectType gameObjectType = GameObjectType.from(object.getGameObjectType());

        switch (gameObjectType) {
            case GOT_misc_drink:
            case GOT_misc_food:
            case GOT_misc_pharmaceutical:
            case GOT_weapon_ranged_thrown:
            case GOT_weapon_heavy_misc:
            case GOT_misc_fishing_bait: {
                final int count = object.getCount();

                if (count != 0) {
                    attributeList.add(SharedObjectAttributes.counter_uses_remaining,
                            String.format("%d", count));
                }

                break;
            }

            case GOT_resource_container_pseudo: {
                //TODO: WTF is this!?
                break;
            }
            default:
                break;
        }

        final VolumeContainer volumeContainer = object.getVolumeContainerProperty();

        //If we are a factory crate that is also a container, then we want to display are contents count.
        if (volumeContainer != null) {
            if (SharedObjectTemplate.GameObjectType.GOT_misc_factory_crate.equals(gameObjectType)) {
                final int currentVolume = volumeContainer.getCurrentVolume();
                final int totalVolume = volumeContainer.getTotalVolume();

                if (currentVolume >= 0 && totalVolume >= 0) {
                    attributeList.add(SharedObjectAttributes.contents,
                            String.format("%d/%d", currentVolume, totalVolume));
                }
            }
        }

        //skill mod bonuses @stat_n

        //attribute bonuses @attr_n

        //skill mod sockets

        //If the object is a crafting tool, then delegate to the crafting service to append attributes.
        if (object.isCraftingTool()) {
            //TODO: AttributeListAppender for crafting tools?
            this.craftingService.appendAttributsForCraftingTool(object, attributeList);
        }

        //If a player created the item, then they should have a creator id attached. Get their name and attach it.
        if (object.getCreatorId() != NetworkId.INVALID) {
            //final String creatorName = playerService.getPlayerName(object.getCreatorId());
            //TODO: Implement a service that can get a player's name by network id.
            final String creatorName = "crush";

            attributeList.add(SharedObjectAttributes.crafter, creatorName);
        }

        //If the object was crafted, it should have the network id of the manufacturing schematic that was used to create it.
        //Generate a serial number from this and append it.
        if (object.isCrafted()) {
            attributeList.add(SharedObjectAttributes.serial_number,
                    SerialNumberGenerator.generate(object.getCraftedId()));
        }

        //Ship components
        //no trade
        //bio link
        //scripts?
    }
}
