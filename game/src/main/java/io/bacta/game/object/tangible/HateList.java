package io.bacta.game.object.tangible;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.swg.object.GameObject;
import io.bacta.swg.util.NetworkId;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by crush on 5/8/2016.
 *
 * @TODO NEED MASSIVE WORK!!!!
 */
public class HateList {
    private static final Logger LOGGER = LoggerFactory.getLogger(HateList.class);

    private static long defaultAutoExpireTargetDuration = 6;
    private static float maxCombatRange = 96.0F;
    private static float interiorTargetDurationFactor = 1.0F;

    @Getter
    private TangibleObject owner;
    @Getter
    private PlayerObject playerObject;

    @Getter
    private final TObjectFloatMap<GameObject> hateList;
    @Getter
    private final TLongSet recentHateList;
    @Getter
    private GameObject target;
    @Getter
    private float maxHate;
    @Getter
    private long lastUpdateTime;
    private long autoExpireTargetDuration;

    public HateList() {

        this.hateList = new TObjectFloatHashMap<>();
        this.recentHateList = new TLongHashSet();
    }


    public boolean addHate(final GameObject target, final float hate) {
//        boolean result = false;
//
//        if (target == owner) {
//            LOGGER.warn("Owner ({}) trying to add hate to itself.", owner.getDebugInformation());
//        } else if (!isOwnerValid()) {
//            LOGGER.warn("Owner ({}} is invalid.", owner.getDebugInformation());
//        } else {
//            if (!isValidTarget(target)) {
//                LOGGER.warn("Owner ({}) has invalid target ({}).", owner.getDebugInformation(), target.getNetworkId());
//            } else {
//                //If a target AI has a master, the target and the master needs to be added to the hate list (ie. pets should cause their master to gain hate)
//                final CreatureObject targetCreatureObject = CreatureObject.asCreatureObject(target);
//                final long masterId = (targetCreatureObject != null) ? targetCreatureObject.getMaster() : NetworkObject.INVALID;
//
//                if (masterId != NetworkObject.INVALID)
//                    addHate(masterId, 0.0f); //wtf 0?????
//
//                // A > 0 hate causes the owner to send out an OnSawAttack() trigger about the target
//                if (hate > 0.0f)
//                    recentHateList.add(target);
//
//                resetHateTimer();
//
//                float totalTargetHate = hate;
//
//                if (hateList.containsKey(target)) {
//                    totalTargetHate = hateList.get(targetId) + hate;
//                    hateList.set(targetId, totalTargetHate);
//                } else {
//                    hateList.insert(targetId, hate);
//
//                    if (playerObject != null)
//                        playerObject.addToPlayerHateList(targetId);
//
//                    triggerTargetAdded(targetId);
//                }
//
//                if ((target.get() == NetworkObject.INVALID)
//                        || (totalTargetHate > this.maxHate.get())) {
//
//                    setTarget(targetId, totalTargetHate);
//                }
//
//                result = true;
//            }
//        }

        return false;
    }

    public boolean setHate(final long targetId, final float hate) {
        return false;
    }

    public float getHate(final GameObject target) {
        float result = 0.0f;

        if (hateList.containsKey(target))
            result = hateList.get(target);

        return result;
    }

    public boolean removeTarget(final long targetId) {
        return false;
    }

    public boolean isValidTarget(final GameObject targetObject) {
        return false;
    }

    public List<ImmutableHateListEntry> getSortedList() {
        //TODO: Why don't we just keep a sorted list in the first place?!
        final List<ImmutableHateListEntry> sortedList = new ArrayList<>(hateList.size());

        if (!hateList.isEmpty()) {
            final TObjectFloatIterator<GameObject> iterator = hateList.iterator();

            while (iterator.hasNext()) {
                iterator.advance();
                sortedList.add(new ImmutableHateListEntry(iterator.key().getNetworkId(), iterator.value()));
            }

            //Sort the targets based on the most hate.
            Collections.sort(sortedList, (a, b) -> Float.compare(a.hate, b.hate) * -1);
        }

        return sortedList;
    }

    public boolean isEmpty() {
        return hateList.isEmpty();
    }

    public void findNewTarget() {
        //This method should be moved to a service somewhere as it needs access to a list of all objects.
        long targetId = NetworkId.INVALID;
        float maxHate = 0.0f;

        final TObjectFloatIterator<GameObject> iterator = hateList.iterator();

        while (iterator.hasNext()) {
            iterator.advance();

            if (iterator.key() == null) {
                continue; //This can't actually happen. SOE does a lookup to try and get the object.
            }

            if (iterator.value() == maxHate || targetId == NetworkId.INVALID) {
                targetId = iterator.key().getNetworkId();
                maxHate = iterator.value();
            }
        }

        setTarget(targetId, maxHate);
    }

    public void clear() {
//        hateList.clear();
//
//        if (playerObject != null)
//            playerObject.clearPlayerHateList();
//
//        target = NetworkObject.INVALID;
//        maxHate = 0;
//        recentHateList.clear();
    }

    public boolean isOnList(final GameObject object) {
        return hateList.containsKey(object);
    }

    public void setTarget(final long targetId, final float hate) {
//        maxHate = hate;
//
//        if (target != targetId) {
//            target = targetId;
//
//            if (target != NetworkObject.INVALID)
//                triggerTargetChanged(target);
//        }
    }

    public void triggerTargetChanged(final GameObject target) {
        //TODO: script hook
    }

    public void triggerTargetAdded(final GameObject target) {
        //TODO: script hook
    }

    public void triggerTargetRemoved(final GameObject target) {
        //TODO: scripp hook
    }

    public long getAutoExpireTargetDuration() {
        long result = autoExpireTargetDuration;

        if (!owner.getParentCell().isWorldCell())
            result *= interiorTargetDurationFactor;

        return result;
    }

    public boolean isOwnerValid() {
        final CreatureObject ownerCreature = CreatureObject.asCreatureObject(owner);
        final boolean ownerIncapacitated = (ownerCreature != null) && ownerCreature.isIncapacitated();

        if (ownerIncapacitated)
            return false;

        final boolean ownerDead = (ownerCreature != null) && ownerCreature.isDead();

        if (ownerDead)
            return false;

        final boolean ownerDisabled = (ownerCreature != null) && ownerCreature.isDisabled();

        if (ownerDisabled)
            return false;

        //final AICreatureController ownerAiCreatureController = AICreatureController.asAiCreatureController(owner.getController());
        //final boolean ownerRetreating = (ownerAiCreatureController != null) && ownerAiCreatureController.isRetreating();

        //if (ownerRetreating)
        //    return false;

        return true;
    }

    public void clearRecentHateList() {
        recentHateList.clear();
    }

    public void resetHateTimer() {
//        lastUpdateTime = System.currentTimeMillis();
//
//        //For NPC, reset the hate timer of every player on the NPC's hate list.
//        if (playerObject == null && !hateList.isEmpty()) {
//            final TObjectFloatIterator<GameObject> iterator = hateList.iterator();
//
//            while (iterator.hasNext()) {
//                iterator.advance();
//                final TangibleObject tangibleObject = TangibleObject.asTangibleObject(iterator.key());
//
//                if (tangibleObject != null && tangibleObject.isHateListOwnerPlayer())
//                    tangibleObject.resetHateTimer();
//            }
//        }
    }

    public float getMaxDistanceToTarget() {
        return maxCombatRange * 2.0F;
    }

    public void setAutoExpireTargetEnabled(final boolean enabled) {
        if (enabled) {
            autoExpireTargetDuration = defaultAutoExpireTargetDuration;
        } else {
            autoExpireTargetDuration = 0;
        }
    }

    public boolean isAutoExpireTargetEnabled() {
        return getAutoExpireTargetDuration() > 0;
    }

    public float getAiLeashTime() {
        return autoExpireTargetDuration;
    }

    public void setAiLeastTime(final float time) {
        autoExpireTargetDuration = (long) time;
    }

    public void forceHateTarget(final GameObject target) {
        if (!isOnList(target))
            return;

        maxHate = getHate(target);

        if (this.target != target) {
            this.target = target;

            if (this.target != null)
                triggerTargetChanged(target);
        }
    }


    public final class ImmutableHateListEntry {
        public final long targetId;
        public final float hate;

        public ImmutableHateListEntry(final long targetId, final float hate) {
            this.targetId = targetId;
            this.hate = hate;
        }
    }
}
