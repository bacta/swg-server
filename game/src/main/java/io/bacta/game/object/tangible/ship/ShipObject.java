package io.bacta.game.object.tangible.ship;

import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.template.ObjectTemplateList;

import javax.inject.Inject;

/**
 * Created by crush on 9/4/2014.
 */
public class ShipObject extends TangibleObject {
    //private final AutoDeltaShort shipId;
    //private final AutoDeltaFloat slideDampener;
    //private final AutoDeltaFloat currentChassisHitPoints;
    //private final AutoDeltaFloat maximumChassisHitPoints;
    //private final AutoDeltaInt chassisType;
    //private final AutoDeltaFloat chassisComponentMassMaximum;
    //private final AutoDeltaFloat chassisComponentMassCurrent;
    //private final AutoDeltaFloat chassisSpeedMaximumModifier;
    //private final AutoDeltaFloat shipActualAccelerationRate;
    //private final AutoDeltaFloat shipActualDecelerationRate;
    //private final AutoDeltaFloat shipActualPitchAccelerationRate;
    //private final AutoDeltaFloat shipActualYawAccelerationRate;
    //private final AutoDeltaFloat shipActualRollAccelerationRate;
    //private final AutoDeltaFloat shipActualPitchRateMaximum;
    //private final AutoDeltaFloat shipActualYawRateMaximum;
    //private final AutoDeltaFloat shipActualRollRateMaximum;
    //private final AutoDeltaFloat shipActualSpeedMaximum;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentArmorHitpointsMaximum;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentArmorHitpointsCurrent;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentEfficiencyGeneral;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentEfficiencyEnergy;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentEnergyMaintenanceRequirement;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentMass;
    //private final AutoDeltaMap<Integer, Long> componentCrc;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentHitpointsCurrent;
    //private final AutoDeltaPackedMap<Integer, FLOAT> componentHitpointsMaximum;
    //private final AutoDeltaPackedMap<Integer, Integer> componentFlags;
    //private final AutoDeltaPackedMap<Integer, UnicodeString> componentNames;
    //private final AutoDeltaPackedMap<Integer, Long> componentCreators;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponDamageMaximum;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponDamageMinimum;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponEffectivenessShields;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponEffectivenessArmor;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponEnergyPerShot;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponRefireRate;
    //private final AutoDeltaPackedMap<Integer, FLOAT> weaponEfficiencyRefireRate;
    //private final AutoDeltaPackedMap<Integer, Integer> weaponAmmoCurrent;
    //private final AutoDeltaPackedMap<Integer, Integer> weaponAmmoMaximum;
    //private final AutoDeltaPackedMap<Integer, Integer> weaponAmmoType;
    //private final AutoDeltaFloat shieldHitpointsFrontCurrent;
    //private final AutoDeltaFloat shieldHitpointsFrontMaximum;
    //private final AutoDeltaFloat shieldHitpointsBackCurrent;
    //private final AutoDeltaFloat shieldHitpointsBackMaximum;
    //private final AutoDeltaFloat shieldRechargeRate;
    //private final AutoDeltaFloat capacitorEnergyCurrent;
    //private final AutoDeltaFloat capacitorEnergyMaximum;
    //private final AutoDeltaFloat capacitorEnergyRechargeRate;
    //private final AutoDeltaFloat engineAccelerationRate;
    //private final AutoDeltaFloat engineDecelerationRate;
    //private final AutoDeltaFloat enginePitchAccelerationRate;
    //private final AutoDeltaFloat engineYawAccelerationRate;
    //private final AutoDeltaFloat engineRollAccelerationRate;
    //private final AutoDeltaFloat enginePitchRateMaximum;
    //private final AutoDeltaFloat engineYawRateMaximum;
    //private final AutoDeltaFloat engineRollRateMaximum;
    //private final AutoDeltaFloat engineSpeedMaximum;
    //private final AutoDeltaFloat reactorEnergyGenerationRate;
    //private final AutoDeltaFloat boosterEnergyCurrent;
    //private final AutoDeltaFloat boosterEnergyMaximum;
    //private final AutoDeltaFloat boosterEnergyRechargeRate;
    //private final AutoDeltaFloat boosterEnergyConsumptionRate;
    //private final AutoDeltaFloat boosterAcceleration;
    //private final AutoDeltaFloat boosterSpeedMaximum;
    //private final AutoDeltaFloat droidInterfaceCommandSpeed;
    //private final AutoDeltaLong installedDroidControlDevice;
    //private final AutoDeltaInt cargoHoldContentsMaximum;
    //private final AutoDeltaInt cargoHoldContentsCurrent;
    //private final AutoDeltaPackedMap<Long, Integer> cargoHoldContents;
    //private final AutoDeltaMap<Long, Pair<UnicodeString, STRING>> cargoHoldContentsResourceTypeInfo;
    //private final AutoDeltaLong pilotLookAtTarget;
    //private final AutoDeltaInt pilotLookAtTargetSlot;
    //Archive::AutoDeltaVariableCallback<BitArray,ShipObject::Callbacks::DefaultCallback<ShipObject::Messages::TargetableSlotBitfieldChanged,BitArray>,ShipObject> m_targetableSlotBitfield;
    //private final AutoDeltaString wingName;
    //private final AutoDeltaString typeName;
    //private final AutoDeltaString difficulty;
    //private final AutoDeltaString FACTION;
    //private final AutoDeltaInt guildId;

    @Inject
    public ShipObject(final ObjectTemplateList objectTemplateList,
                      final SlotIdManager slotIdManager,
                      final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
