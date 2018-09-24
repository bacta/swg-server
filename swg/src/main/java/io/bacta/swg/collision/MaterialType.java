package io.bacta.swg.collision;

/**
 * Created by crush on 5/13/2016.
 */
public final class MaterialType {
    public static final int SOLID = 0x0001; //Generic solid stuff
    public static final int MEAT = 0x0002; //Living things are made of meat
    public static final int METAL = 0x0004; //Droids and machines are made of metal
    public static final int ENERGY = 0x0008; //Ghosts are made of energy
    public static final int BRUSH = 0x0010; //Bushes are made of brush
    public static final int DOOR = 0x0020; //Doors are made of door
    public static final int MONSTER = 0x0040; //Monsters are made of monster
    public static final int PLAYER = 0x0080; //Players are made of player
    public static final int NPC = 0x0100; //NPCs are made of NPC

    public static final int ANY = 0xFFFFFFFF;
}
