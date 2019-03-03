package io.bacta.game.object.tangible.creature;

import io.bacta.shared.localization.StringId;

public final class Attribute {
    private static final String tableName = "att_n";
    private static final String tableDesc = "att_d";

    private static final String[] attributeNames = new String[]{
            "health",
            "strength",
            "constitution",
            "action",
            "quickness",
            "stamina",
            "mind",
            "focus",
            "willpower"
    };

    private static final StringId[] attributeStringIds;
    private static final StringId[] attributeDescriptionStringIds;

    static {
        attributeStringIds = new StringId[attributeNames.length];
        attributeDescriptionStringIds = new StringId[attributeNames.length];

        for (int i = 0; i < attributeNames.length; ++i) {
            attributeStringIds[i] = new StringId(tableName, attributeNames[i].toLowerCase());
            attributeDescriptionStringIds[i] = new StringId(tableDesc, attributeNames[i].toLowerCase());
        }
    }

    public static final int HEALTH = 0x00;
    public static final int CONSTITUTION = 0x01;
    public static final int ACTION = 0x02;
    public static final int STAMINA = 0x03;
    public static final int MIND = 0x04;
    public static final int WILLPOWER = 0x05;
    public static final int SIZE = 0x06;

    public static boolean isValidAttribute(final int attribute) {
        return attribute >= HEALTH && attribute <= WILLPOWER;
    }

    public static String getAttributeName(final int attribute) {
        return (attribute < 0 || attribute > attributeNames.length) ? "" : attributeNames[attribute];
    }

    public static StringId getAttributeStringId(int attribute) {
        return (attribute < 0 || attribute > attributeNames.length) ? StringId.INVALID : attributeStringIds[attribute];
    }

    public static StringId getAttributeDescriptionStringId(int attribute) {
        return (attribute < 0 || attribute > attributeNames.length) ? StringId.INVALID : attributeDescriptionStringIds[attribute];
    }
}
