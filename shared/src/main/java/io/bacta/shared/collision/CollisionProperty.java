package io.bacta.shared.collision;

import io.bacta.shared.collision.extent.BaseExtent;
import io.bacta.shared.math.Sphere;
import io.bacta.shared.math.Transform;
import io.bacta.shared.math.Vector;
import io.bacta.shared.object.GameObject;
import io.bacta.shared.object.template.SharedCreatureObjectTemplate;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.shared.portal.CellProperty;
import io.bacta.shared.property.Property;

public class CollisionProperty extends Property {
    public final static int MOBILE = 0x0001;
    public final static int FLORA = 0x0002;
    public final static int PLAYER = 0x0004;
    public final static int MOUNT = 0x0008;
    public final static int SHIP = 0x0010;
    public final static int PLAYER_CONTROLLED = 0x0020;
    public final static int COLLIDABLE = 0x0040;
    public final static int SERVER_SIDE = 0x0080;
    public final static int IN_COMBAT = 0x0100;
    public final static int IN_COLLISION_WORLD = 0x0200;
    public final static int IDLE = 0x0400;
    public final static int DISABLE_COLLISION_WORLD_ADD_REMOVE = 0x0800;


    public static int getClassPropertyId() {
        return 0x01D26659;
    }

    protected Transform lastTransformInWorld;
    protected Transform lastTransformInParent;
    protected GameObject lastCellObject; //Watcher<T>

    protected float stepHeight;
    protected float defaultRadius;
    protected float offsetX;
    protected float offsetZ;

    //Make this stuff thread-safe without volatile.
    protected volatile boolean extentsDirty;

    protected volatile BaseExtent extentInLocal;
    protected volatile BaseExtent extentInParent;

    protected volatile Sphere sphereInLocal;
    protected volatile Sphere sphereInWorld;

    protected volatile float scale;

    protected Floor floor;
    protected Footprint footprint;
    protected int idleCounter;
    protected CollisionProperty next;
    protected CollisionProperty prev;

    protected boolean disableCollisionWorldAddRemove;
    protected short flags;

    public CollisionProperty(final GameObject owner) {
        this(owner, owner.getObjectTemplate() != null ? owner.getObjectTemplate().asSharedObjectTemplate() : null);
    }

    public CollisionProperty(final GameObject owner, final SharedObjectTemplate objectTemplate) {
        super(getClassPropertyId(), owner);

        lastTransformInParent = Transform.IDENTITY;
        lastTransformInWorld = Transform.IDENTITY;
        extentsDirty = true;
        scale = owner.getScale().x;
        idleCounter = 3;
        flags = COLLIDABLE;

        initFromTemplate(objectTemplate);

        initFloor();

        if (objectTemplate != null) {
            final String templateName = objectTemplate.getResourceName();

            //TODO: This seems pretty hackish...
            if (templateName != null) {
                final boolean lairTemplate = templateName.contains("lair");

                if (lairTemplate)
                    setCollidable(false);
            }
        }
    }

    public void detachList() {
        if (prev != null)
            prev.next = next;

        if (next != null)
            next.prev = prev;

//        if (this == activeListHead) {
//            activeListHead = next;
//        }

        prev = null;
        next = null;
    }

    public CollisionProperty attachList(final CollisionProperty head) {
        detachList();

        if (head != null)
            head.prev = this;

        prev = null;
        next = head;

        return this;
    }

    public Transform getTransformObjectToCell(final GameObject object) {
        if (object == null)
            return Transform.IDENTITY;

        //If this object is a cell, its o2c transform is the identity transform

        if (object.getCellProperty() != null)
            return Transform.IDENTITY;

        //Otherwise this object's o2c transform is the product of its o2p transform and its parent's o2c transform.
        final Transform objectToParent = object.getTransformObjectToParent();
        final Transform parentToCell = getTransformObjectToCell(object.getAttachedTo());

        final Transform objectToCell = new Transform();
        objectToCell.multiply(parentToCell, objectToParent);

        return objectToCell;
    }

    public Transform getTransformObjectToParent() {
        return getOwner().getTransformObjectToParent();
    }

    public Transform getTransformObjectToWorld() {
        return getOwner().getTransformObjectToWorld();
    }

    public Transform getTransformObjectToCell() {
        return this.getTransformObjectToCell(getOwner());
    }

    protected float getDistance(final Vector pointInWorld, float maxDistance) {
        return 0.0f;
    }

    public GameObject getStandingOn() {
        if (footprint != null)
            return footprint.getStandingOn();
        else
            return null;
    }

    public void hit(final CollisionProperty staticCollider) {
        if (isMobile())
            setIdle(false);
    }

    public void hitBy(final CollisionProperty dynamicCollider) {
        if (isMobile())
            setIdle(false);
    }

    public CellProperty getCell() {
        return getOwner().getParentCell();
    }

    public Vector getLastPosInWorld() {
        return getLastTransformInWorld().getPositionInParent();
    }

    public Vector getLastPosInParent() {
        return getLastTransformInParent().getPositionInParent();
    }

    public Transform getLastTransformInWorld() {
        return lastTransformInWorld;
    }

    public Transform getLastTransformInParent() {
        if (lastCellObject != null) {
            return lastTransformInParent;
        } else {
            return lastTransformInWorld;
        }
    }

    public CellProperty getLastCell() {
        if (lastCellObject != null) {
            return lastCellObject.getCellProperty();
        } else {
            return CellProperty.getWorldCellProperty();
        }
    }

    public void setLastPos(final CellProperty cell, final Transform transformInParent) {
        if (cell == null) {
            lastCellObject = null;
            lastTransformInParent = transformInParent;
            lastTransformInWorld = transformInParent;
        } else {
            lastCellObject = cell.getOwner();
            lastTransformInParent = transformInParent;
            //lastTransformInWorld = CollisionUtils.transformToCell(cell, transformInParent, CellProperty.getWorldCellProperty());
        }
    }

    public int elevatorMove(int nFloors, final Transform outTransform) {
//        if (footprint != null)
//            return footprint.elevatorMove(nFloors, outTransform);
//        else
        return 0;
    }

    public void updateExtents() {
        //extentUpdateCount++;

        //We can't get an onScaleChanged callback, so detect if it changed ourselves
        //and if so rebuild the extents.

//        float newScale = getOwner().getScale().x;
//
//        if (scale != newScale) {
//            extentInLocal = null;
//            extentInParent = null;
//            scale = newScale;
//        }
//
//        if (extentInLocal == null) {
//            final Appearance appearance = getOwner().getAppearance();
//
//            if (appearance != null) {
//                final AppearanceTemplate appearanceTemplate = appearance.getAppearanceTemplate();
//
//                if (appearanceTemplate != null) {
//                    final BaseExtent source = convertToSimpleExtent(appearanceTemplate.getCollisionExtent());
//
//                    if (source != null) {
//                        final BaseExtent scaled = source.clone();
//
//                        scaled.transform(source, Transform.IDENTITY, scale);
//
//                        attachSourceExtent(scaled);
//                    }
//                }
//            }
//
//            if (extentInLocal == null) {
//                final PortalProperty portalProperty = getOwner().getPortalProperty();
//
//                if (portalProperty != null) {
//                    final BaseExtent source = convertToSimpleExtent(portalProperty.getPortalPropertyTemplate().getCell(0).getCollisionExtent());
//
//                    if (source != null) {
//                        final BaseExtent scaled = source.clone();
//
//                        scaled.transform(source, Transform.IDENTITY, scale);
//
//                        attachSourceExtent(scaled);
//                    }
//                }
//            }
//
//            if (extentInLocal == null) {
//                if (isMobile()) {
//                    final Vector center = new Vector(offsetX, getCollisionHeight() + getCollisionRadius(), offsetZ).multiply(scale);
//                    final float radius = getCollisionRadius() * scale;
//
//                    final Sphere defaultSphere = new Sphere(center, radius);
//
//                    attachSourceExtent(new SimpleExtent(new MultiShape(defaultSphere)));
//
//                    if (footprint != null)
//                        footprint.setRadius(radius);
//                }
//            }
//
//            //minor hack to make sure ships have a default radius even though it's not specified in the template
//
//            if (isShip() && extentInLocal != null)
//                this.defaultRadius = extentInLocal.getRadius();
//        }
//
//        if (extentInParent == null) {
//            if (extentInLocal != null)
//                extentInParent = extentInLocal.clone();
//        }
//
//        if (extentInParent != null && extentInLocal != null) {
//            Transform t = getTransformObjectToCell();
//
//            if (isMobile() && !isShip()) {
//                final Vector pos = t.getPositionInParent();
//                t = Transform.IDENTITY;
//                t.moveInParentSpace(pos);
//            }
//
//            extentInParent.transform(extentInLocal, t, 1.0f);
//        }
//
//        if (floor != null)
//            floor.updateExtent();
//
//        if (extentInLocal != null && floor != null) {
//            sphereInLocal = Containment3d.EncloseSphere(extentInLocal.getBoundingSphere(), floor.getBoundingSphereInLocal());
//        } else if (extentInLocal != null) {
//            sphereInLocal = extentInLocal.getBoundingSphere();
//        } else if (floor != null) {
//            sphereInLocal = floor.getBoundingSphereInLocal();
//        } else {
//            final Appearance appearance = getOwner().getAppearance();
//
//            if (appearance != null)
//                sphereInLocal = appearance.getSphere();
//            else
//                sphereInLocal = Sphere.ZERO;
//        }
//
//        if (extentInParent != null && floor != null) {
//            final Sphere extentSphereInWorld = CollisionUtils.transformToWorld(getCell(), extentInParent.getBoundingSphere());
//            sphereInWorld = Containment3d.EncloseSphere(extentSphereInWorld, floor.getBoundingSphereInWorld());
//        } else if (floor != null) {
//            sphereInWorld = floor.getBoundingSphereInWorld();
//        } else if (extentInParent != null) {
//            final Sphere sphereInParent = extentInParent.getBoundingSphere();
//            sphereInWorld = CollisionUtils.transformToWorld(getCell(), sphereInParent);
//        } else {
//            sphereInWorld = getOwner().rotateTranslateObjectToWorld(sphereInLocal);
//        }
//
//        extentsDirty = false;
    }

    public Sphere getBoundingSphereInLocal() {
        if (extentsDirty)
            updateExtents();

        //omitted a bunch of debug code...

        return sphereInLocal;
    }

    public Sphere getBoundingSphereInWorld() {
        if (extentsDirty)
            updateExtents();

        //omitted a bunch of debug code...

        return sphereInWorld;
    }

    public void cellChanged() {
//        setExtentsDirty(true);
//
//        if (footprint != null)
//            footprint.cellChanged();
    }

    public void objectWarped() {
//        setIdle(false);
//        storePosition();
//        final Footprint foot = getFootprint();
//
//        if (foot != null)
//            foot.objectWarped();
    }

    public boolean blocksMovement() {
        //HACK - force mobiles to block movment so we can collide with them
        if (isMobile())
            return true;
        else
            return isCollidable();
    }
//
//    public boolean blocksInteraction (InterationType interaction) {
    // return isCollidable();
//    }

    public boolean canCollidWith(final CollisionProperty otherCollision) {
//        if (otherCollision == null || !otherCollision.isCollidable())
//            return false;
//
//        if (!isCollidable())
//            return false;
//
//        if (!otherCollision.isMobile()) {
//            final GameObject otherOwner = otherCollision.getOwner();
//
//            if (otherOwner instanceof DoorObject)
//                return true; //everyone always triggers doors.
//
//            final boolean isBarrier = otherOwner instanceof BarrierObject;
//
//            final CellProperty thisCell = getOwner().getParentCell();
//            final CellProperty otherCell = otherCollision.getOwner().getParentCell();
//
//            if (thisCell && !thisCell.isAdjacentTo(otherCell))
//                return false;
//
//            if (otherCell != null && (otherCell != CellProperty.getWorldCellProperty())) {
//                final PortalProperty otherPortalProperty = otherCell.getPortalProperty();
//
//                if (otherPortalProperty != null) {
//                    final GameObject otherBuilding = otherPortalProperty.getOwner();
//
//                    //IsPlayerHouseHook hook = ConfigSharedCollision::getIsPlayerHouseHook();
//
//                    //if (hook && hook(otherBuilding))
//                    //{
//                    ////Target is inside a player house and thus is non-collidable
//                    //return isBarrier;
//                    //}
//                }
//            }
//
//            if (isBarrier)
//                return true;
        //}

        //Players collide with each other if they're both in combat and always collide with monsters.
//Monsters don't collide with anything.
//        return !(isMobile() && otherCollision.isMobile())
//                || !isPlayer()
//                || !otherCollision.isPlayerControlled()
//                || isInCombat() && otherCollision.isInCombat();
        return false;
    }

    public void updateIdle() {
        idleCounter++;

        if (idleCounter >= 3) {
            if (footprint != null)
                storePosition();

            setIdle(true);
        }
    }

    public void setIdle(final boolean idle) {
        if (isMobile()) {
            if (idle) {
                detachList();
            } else {
                idleCounter = Math.min(idleCounter, 0);
                //attachList(activeListHead);
            }
        }

        modifyFlags(IDLE, idle);
    }

    public void storePosition() {
        setLastPos(getCell(), getTransformObjectToParent());
    }

    public void attachSourceExtent(final BaseExtent newSourceExtent) {
        if (extentInLocal != newSourceExtent) {
            extentInLocal = newSourceExtent;
            extentInParent = null;
            extentsDirty = true;
        }
    }

    public float getCollisionRadius() {
        return 0.f;
    }

    public float getCollisionHeight() {
        return 0.f;
    }

    public void initFloor() {
        if (isMobile())
            return;
//
//        if (floor != null)
//            return;
//
//        String floorName = null;
//
//        final Appearance appearance = getOwner().getAppearance();
//
//        if (appearance != null)
//            floorName = appearance.getFloorName();
//
//        // HACK - Server-side POBs don't currently load appearances, which means
//        // that they don't load their building skirts. If we couldn't get a floor from
//        // the appearance, try to get one from the portal property instead.
//        if (floorName == null) {
//            final PortalProperty portalProperty = getOwner().getPortalProperty();
//
//            if (portalProperty != null)
//                floorName = portalProperty.getExteriorFloorName();
//        }

//        TODO: FloorManager. We will have to move this whole method...
//        if (floorName != null) {
//            floor = FloorManager.createFloor(floorName, getOwner(), appearance, true);
//
//            if (appearance != null)
//                appearance.getShadowBlobAllowed();
//        }
    }

    public void initFromTemplate(final SharedObjectTemplate objectTemplate) {
        if (objectTemplate == null) {
            //TODO: Put in static spatial database.
            return;
        }

        if (objectTemplate instanceof SharedCreatureObjectTemplate) {
            final SharedCreatureObjectTemplate creatureTemplate = (SharedCreatureObjectTemplate) objectTemplate;

            setMobile(true);

            //TODO: put in dynamic spatial database.

            //More SOE HACKS
            if (objectTemplate.getCrcName().getString().contains("player"))
                setPlayerControlled(true);

            stepHeight = creatureTemplate.getStepHeight();
            defaultRadius = creatureTemplate.getCollisionRadius();
            offsetX = creatureTemplate.getCollisionOffsetX();
            offsetZ = creatureTemplate.getCollisionOffsetZ();

            final float waterHeight = creatureTemplate.getSwimHeight();

            footprint = new Footprint(getOwner().getPositionInParent(), getCollisionRadius(), this, waterHeight);
        } else {
            if (objectTemplate.getForceNoCollision())
                setCollidable(false);

//            final int gameObjectType = objectTemplate.getGameObjectType().value;

            //TODO: Implement GameObjectTypes::isTypeOf(GOT, baseGOT)
            final boolean isShip = false;

            if (isShip) {
//                setShip(true);
//
//                if (objectTemplate instanceof SharedShipObjectTemplate) {
//                    final SharedShipObjectTemplate shipObjectTemplate = (SharedShipObjectTemplate) objectTemplate;
//                    setMobile(true);
//
//                    if (shipObjectTemplate.getPlayerControlled())
//                        setPlayerControlled(true);
//                }

                //Ships don't have a radius specified in the template, but we'll give them one once we've updated
                //the extents. To start, just give them a 1 meter default radius.

                stepHeight = 0.0f;
                defaultRadius = 1.0f;
                offsetX = 0.0f;
                offsetZ = 0.0f;

                //TODO: If GameObjectType is ship_station, ship_capital, ship_transport, ship_mining_asteroid_static,
                //then put in static spatial database.
                //Otherwise, put in dyanmic spatial database.
            } else if (false /* is ship component */) {
                //If a ship component, put in dynamic spatial database.
            } else {
                //else put in static spatial database.
            }
        }
    }

    public void addToCollisionWorld() {
        modifyFlags(IN_COLLISION_WORLD, true);

        setIdle(false);
        idleCounter = -10; //Make AI collision not be flagged as idle for a short while after being added to the world.

        storePosition();

        setExtentsDirty(true);

        if (footprint != null) {
            //-- cached objects don't get footprints because we don't want to drop them to the floors/ground etc...
//            if (getOwner().getNetworkId() < NetworkObject.INVALID)
//                footprint = null;
//
//            if (footprint != null)
//                footprint.addToWorld();
        }
    }

    public void removeFromCollisionWorld() {
        detachList();

//        if (footprint != null)
//            footprint.detach();

        modifyFlags(IN_COLLISION_WORLD, false);
    }

    public boolean isInCollisionWorld() {
        return hasFlags(IN_COLLISION_WORLD);
    }


//
//    public void drawDebugShapes(final DebugShapeRenderer renderer) {
//
//    }


    public Floor getFloor() {
        return floor;
    }

    public Footprint getFootprint() {
        return footprint;
    }

    public boolean getExtentsDirty() {
        return extentsDirty;
    }

    public void setExtentsDirty(boolean dirty) {
        extentsDirty = dirty;
    }

    public BaseExtent getExtentInLocal() {
        if (getExtentsDirty())
            updateExtents();

        return extentInLocal;
    }

    public BaseExtent getExtentInParent() {
        if (getExtentsDirty())
            updateExtents();

        return extentInParent;
    }

    public float getScale() {
        return scale;
    }

    public void setMount(boolean mount) {
        if (isMount() == mount) {
            modifyFlags(MOUNT, mount);
//
//            if (footprint != null)
//                footprint.updateContactRadii();
        }
    }

    public void setMobile(boolean mobile) {
        modifyFlags(MOBILE, mobile);
    }

    public void setFlora(boolean flora) {
        modifyFlags(FLORA, flora);
    }

    public void setPlayer(boolean player) {
        modifyFlags(PLAYER, player);
    }

    public void setPlayerControlled(boolean playerControlled) {
        modifyFlags(PLAYER_CONTROLLED, playerControlled);
    }

    public void setCollidable(boolean collidable) {
        modifyFlags(COLLIDABLE, collidable);
    }

    public void setInCombat(boolean inCombat) {
        modifyFlags(IN_COMBAT, inCombat);
    }

    public void setShip(boolean ship) {
        modifyFlags(SHIP, ship);
    }

    public void setServerSide(boolean serverSide) {
        modifyFlags(SERVER_SIDE, serverSide);
    }


    public boolean isMobile() {
        return hasFlags(MOBILE);
    }

    public boolean isFlora() {
        return hasFlags(FLORA);
    }

    public boolean isPlayer() {
        return hasFlags(PLAYER);
    }

    public boolean isMount() {
        return hasFlags(MOUNT);
    }

    public boolean isShip() {
        return hasFlags(SHIP);
    }

    public boolean isPlayerControlled() {
        return hasFlags(PLAYER_CONTROLLED);
    }

    public boolean isCollidable() {
        return hasFlags(COLLIDABLE);
    }

    public boolean isInCombat() {
        return hasFlags(IN_COMBAT);
    }

    public boolean isIdle() {
        return hasFlags(IDLE);
    }

    public boolean isServerSide() {
        return hasFlags(SERVER_SIDE);
    }

    public CollisionProperty getPrev() {
        return prev;
    }

    public CollisionProperty getNext() {
        return next;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = (short) flags;
    }

    public boolean hasFlags(int flags) {
        return (this.flags & flags) == flags;
    }

    public void modifyFlags(int flags, boolean onOff) {
        if (onOff)
            this.flags |= flags;
        else
            this.flags &= ~flags;
    }


    private static BaseExtent convertToSimpleExtent(final BaseExtent sourceExtent) {
        if (sourceExtent == null)
            return null;
//
//        switch (sourceExtent.getType()) {
//            case
//        }

        return null;
    }
}
