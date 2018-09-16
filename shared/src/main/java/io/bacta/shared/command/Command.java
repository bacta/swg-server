package io.bacta.shared.command;

import io.bacta.shared.foundation.Crc;
import io.bacta.shared.localization.StringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;

/**
 * Created by crush on 5/18/2016.
 */
public class Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);

    private static final int defaultAttackCoolGroup = 0x399ea293; //defaultattack

    private static final String table = "cmd_err";
    private static final StringId[] ids = new StringId[]{
            new StringId(table, "success_prose"),
            new StringId(table, "locomotion_prose"),
            new StringId(table, "ability_prose"),
            new StringId(table, "target_type_prose"),
            new StringId(table, "target_range_prose"),
            new StringId(table, "state_prose"),
            new StringId(table, "state_must_have_prose"),
            new StringId(table, "god_level_prose")
    };


    private String commandName;
    private int commandHash;
    private int defaultPriority;
    private String scriptHook;
    private String failScriptHook;
    //private cppHook
    //private failCppHook
    private float defaultTime;
    private String characterAbility;
    private BitSet locomotionPermissions;
    private BitSet statePermissions;
    private BitSet stateRequired;
    private String tempScript;
    private int target;
    private int targetType;
    private String stringId;
    private int visibleToCleints;
    private boolean callOnTarget;
    private int commandGroup;
    private float maxRangeToTarget;
    private float maxRangeToTargetSquared;
    private int godLevel;
    private int displayGroup;
    private boolean addToCombatQueue;
    private float warmTime;
    private float execTime;
    private float coolTime;
    private int coolGroup;
    private int weaponTypesValid;
    private int weaponTypesInvalid;
    private int coolGroup2;
    private float coolTime2;
    private boolean toolbarOnly;
    private boolean fromServerOnly;

    public Command() {

    }

    public boolean isNull() {
        return commandHash == Crc.NULL;
    }

    public boolean isPrimaryCommand() {
        return coolGroup == defaultAttackCoolGroup;
    }

    public static StringId getStringIdForErrorCode(final int errorCode) {
        if (errorCode < 0 || errorCode >= ErrorCode.MAX) {
            LOGGER.warn("Received invalid error code {}", errorCode);
            return ids[ErrorCode.SUCCESS];
        }

        return ids[errorCode];
    }

    public static final class Priority {
        public static final int IMMEDIATE = 0;
        public static final int FRONT = 1;
        public static final int NORMAL = 2;
        public static final int DEFAULT = 3; //not a real priority, falls through.
        public static final int NUMBER_OF_PRIORITIES = 4;
    }

    public static final class Target {
        public static final int FRIEND = 0;
        public static final int ENEMY = 1;
        public static final int OTHER = 2;
        public static final int NUMBER_OF_TARGETS = 3;
    }

    public static final class TargetType {
        public static final int NONE = 0;
        public static final int REQUIRED = 1;
        public static final int OPTIONAL = 2;
        public static final int LOCATION = 3;
        public static final int ALL = 4;
        public static final int NUMBER_OF_TARGET_TYPES = 5;
    }

    public static final class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int LOCOMOTION = 1;
        public static final int ABILITY = 2;
        public static final int TARGET_TYPE = 3;
        public static final int TARGET_RANGE = 4;
        public static final int STATE_MUST_NOT_HAVE = 5;
        public static final int STATE_MUST_HAVE = 6;
        public static final int GOD_LEVEL = 7;
        public static final int CANCELLED = 8;
        public static final int MAX = 9;
    }

    public static final class WeaponType {
        public static final int RIFLE = 0;
        public static final int CARBINE = 1;
        public static final int PISTOL = 2;
        public static final int HEAVY = 3;
        public static final int ONE_HAND_MELEE = 4;
        public static final int TWO_HAND_MELEE = 5;
        public static final int UNARMED = 6;
        public static final int POLEARM = 7;
        public static final int THROWN = 8;
        public static final int ONE_HAND_LIGHTSABER = 9;
        public static final int TWO_HAND_LIGHTSABER = 10;
        public static final int POLEARM_LIGHTSABER = 11;
        public static final int RANGED = -1;
        public static final int MELEE = -2;
        public static final int ALL = -3;
        public static final int ALL_LIGHTSBAERS = -4;
        public static final int FORCE_POWER = -5;
        public static final int ALL_BUT_HEAVY = -6;
    }
}
