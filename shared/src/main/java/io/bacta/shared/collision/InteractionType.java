package io.bacta.shared.collision;

/**
 * Created by crush on 5/13/2016.
 * <p>
 * The values for these enums comes from SharedObjectTemplate.h
 */
public final class InteractionType {
    public static final int SEE = 0x0001;
    public static final int TARGET = 0x0002;
    public static final int COMBAT_RANGED = 0x0004;
    public static final int COMBAT_MELEE = 0x0008;
    public static final int MANIPULATE = 0x0010;
    public static final int TALK = 0x0020; //Sound doesn't trave in straight lines, but...
    public static final int RADIAL_DAMAGE = 0x0040; //Explosions cause radial damage
    public static final int CAMERA = 0x0080; //This is a bit different from the other actions - if an object blocks CAMERA it means the object blocks the camera's view of the target.

    public static final int ALL = SEE | TARGET | COMBAT_RANGED | COMBAT_MELEE | MANIPULATE | TALK | RADIAL_DAMAGE | CAMERA;
    public static final int ANY = 0xFFFFFFFF;
}
