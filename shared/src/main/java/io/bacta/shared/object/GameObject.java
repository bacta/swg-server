package io.bacta.shared.object;

import com.google.common.base.Preconditions;
import io.bacta.engine.object.NetworkObject;
import io.bacta.shared.appearance.Appearance;
import io.bacta.shared.collision.CollisionProperty;
import io.bacta.shared.container.ContainedByProperty;
import io.bacta.shared.container.Container;
import io.bacta.shared.container.SlottedContainer;
import io.bacta.shared.container.VolumeContainer;
import io.bacta.shared.math.Transform;
import io.bacta.shared.math.Vector;
import io.bacta.shared.portal.CellProperty;
import io.bacta.shared.portal.PortalProperty;
import io.bacta.shared.property.Property;
import io.bacta.shared.template.ObjectTemplate;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by crush on 4/22/2016.
 */
public class GameObject extends NetworkObject {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GameObject.class);

    protected final static int ROTATIONS_BETWEEN_REORTHONORMALIZE = 255;

    @Getter
    private transient final ObjectTemplate objectTemplate;

    @Getter
    private boolean inWorld;
    @Getter
    private boolean active;
    @Getter
    private boolean kill;
    @Getter
    private boolean childObject;
    @Getter
    @Setter
    private volatile boolean objectToWorldDirty;
    @Getter
    private boolean destroyed;
    @Getter
    private boolean altering;

    private transient Appearance appearance;
    //private Controller controller;
    //private Dynamics dynamics;

    private GameObject attachedToObject;
    private List<GameObject> attachedObjects;
    //private DpvsObjects dpvsObjects;

    private int rotations;

    @Getter
    @Setter
    private Vector scale;
    private Transform objectToParent;
    private Transform objectToWorld;

    //private volatile WatchedByList watchedByList;

    @Getter
    private Container containerProperty;

    @Getter
    private CollisionProperty collisionProperty;

    //private SpatialSubdivisionHandle spatialSubdivisionHandle;

    private boolean useAlterScheduler;
    //private ScheduleData scheduleData;

    private boolean shouldBakeIntoMesh;

    @Getter
    protected ContainedByProperty containedByProperty;
    protected final List<Property> propertyList = new ArrayList<>();

    public GameObject() {
        this(null);
    }

    public GameObject(final ObjectTemplate objectTemplate) {
        this(objectTemplate, NetworkObject.INVALID);
    }

    public GameObject(final ObjectTemplate objectTemplate, final long networkId) {
        this.inWorld = false;
        this.active = true;
        this.kill = false;
        this.childObject = false;
        this.objectToWorldDirty = true;
        this.destroyed = false;
        this.altering = false;
        this.objectTemplate = objectTemplate;
        //this.notificationList(NotificationListManager.getEmptyNotificationList());
        this.networkId = networkId;
        this.rotations = 0;
        this.scale = Vector.XYZ111;
        this.objectToParent = new Transform();
        this.objectToWorld = null;
        //this.watchedByList = new WatchedByList();
        this.useAlterScheduler = true;
        this.shouldBakeIntoMesh = true;
        this.attachedObjects = new ArrayList<>();

        if (objectTemplate != null)
            objectTemplate.addReference();
        //NetworkIdManager::addObject(this);
    }

    public String getDebugInformation() {
        return getDebugInformation(false);
    }

    public String getDebugInformation(final boolean includeParent) {
        if (includeParent && attachedToObject != null) {
            return String.format("[id=%d, ot=%s, at=%s, child=%d, pid=%d, pot=%s, pat=%s]",
                    getNetworkId(),
                    getObjectTemplateName(),
                    getAppearanceTemplateName(),
                    isChildObject(),
                    attachedToObject.getNetworkId(),
                    attachedToObject.getObjectTemplateName(),
                    attachedToObject.getAppearanceTemplateName());
        } else {
            return String.format("[id=%d, ot=%s, at=%s]",
                    getNetworkId(),
                    getObjectTemplateName(),
                    getAppearanceTemplateName());
        }
    }

    public int getObjectType() {
        return objectTemplate != null ? objectTemplate.getId() : 0;
    }

    public String getObjectTemplateName() {
        return objectTemplate != null ? objectTemplate.getResourceName() : null;
    }

    public Transform getObjectToParent() {
        return objectToParent;
    }

    /**
     * Gets the object-to-world transformation for this object.
     *
     * @return The transform for this object.
     */
    public Transform getObjectToWorld() {
        if (attachedToObject != null) {
            if (objectToWorldDirty) {
                objectToWorld.multiply(attachedToObject.getObjectToWorld(), objectToParent);
                setObjectToWorldDirty(false);
            }

            return objectToWorld;
        }

        return objectToParent;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public String getAppearanceTemplateName() {
        return appearance != null ? getAppearance().getAppearanceTemplateName() : null;
    }

    /**
     * Get the parent object for this object.
     *
     * @return Returns null if the object is not a child object.
     */
    public GameObject getParent() {
        return childObject ? attachedToObject : null;
    }

    /**
     * Get the object this object is attached to.
     * <p>
     * The object may be a child of the attached object.
     */
    public GameObject getAttachedTo() {
        return attachedToObject;
    }

    public void attachToObjectInWorld(final GameObject object, final boolean asChildObject) {
        // calculate frame and position in the master's space
        final Vector i = object.rotateWorldToObject(getObjectFrameIInWorld());
        final Vector j = object.rotateWorldToObject(getObjectFrameJInWorld());
        final Vector k = object.rotateWorldToObject(getObjectFrameKInWorld());
        final Vector p = object.rotateTranslateWorldToObject(getPositionInWorld());

        // set the transform of the child object
        objectToParent.setLocalFrameIJKInParentSpace(i, j, k);
        objectToParent.setPositionInParentSpace(p);
        reorthonormalize();

        // now do the attach in master space
        attachToObjectInParent(object, asChildObject);
    }

    public void attachToObjectInParent(final GameObject object, final boolean asChildObject) {
        Preconditions.checkNotNull(object);

        assert !childObject : String.format("object %s already is a child object", getDebugInformation(true));
        assert attachedToObject != null : String.format("object %s is relative to another object", getDebugInformation(true));
        assert this != object : "attempting to attach to self";

        if (attachedToObject != null)
            detachFromObject(DetachFlags.NONE);

        if (object.attachedObjects == null)
            object.attachedObjects = new ArrayList<>();

        object.attachedObjects.add(this);

        // remove the object from the world if its being added as a child
        // and is currently in the world.  This must happen before the m_childObject
        // flag is set so that the worlds don't erroneously optimize out necessary
        // operations by looking at the isChildObject() method.  This issue happened
        // with removing objects from the ClientWorld sphere trees.
        if (asChildObject && isInWorld())
            removeFromWorld();

        attachedToObject = object;
        childObject = asChildObject;

        objectToWorld = new Transform();
        setObjectToWorldDirty(true);

        if (asChildObject) {        // add the child to the world if the parent is in the world.
            if (object.isInWorld())
                addToWorld();

            // if parent objects alter their children, ensure the new child
            // object is not in the alter scheduler.
            //if (ms_objectsAlterChildrenAndContents)
            //IGNORE_RETURN(AlterScheduler::removeObject ( * this));
        }

        //-- always schedule an object for alter next frame if it has received a new child object
        //if (World::isInstalled () && World::existsInWorld (getRootParent()))
        //scheduleForAlter();
    }

    /**
     * Detach an object from its master.
     * <p>
     * Removal of an attached object causes the last attached object to move
     * into its place in the vector.  Therefore, attached object removal causes
     * the order of attached objects to change.
     */
    public void detachFromObject(final DetachFlags detachFlags) {
        Preconditions.checkNotNull(attachedToObject);

        // leave the child object where it was in the same cell
        final boolean toParentCell = (detachFlags == DetachFlags.PARENT);
        final boolean noCell = (detachFlags == DetachFlags.NONE);

        final CellProperty cellProperty = noCell ? null : (toParentCell ? getParentCell() : CellProperty.getWorldCellProperty());
        final boolean shouldAttach = !(!toParentCell || noCell) && attachedToObject != cellProperty.getOwner();
        objectToParent = shouldAttach ? getTransformObjectToCell() : getTransformObjectToWorld();
        objectToWorld = null;
        setObjectToWorldDirty(true);

        // remove from the attached objects list
        final List<GameObject> attachedObjects = attachedToObject.attachedObjects;
        final boolean removed = attachedObjects.remove(this);

        assert removed : "Attached object was not found in parent.";

        // set as unattached
        attachedToObject = null;

        final boolean wasChildObject = isChildObject();
        final boolean wasInWorld = isInWorld();

        if (wasChildObject && wasInWorld)
            removeFromWorld();

        childObject = false;

        if (wasChildObject && wasInWorld)
            addToWorld();

        if (shouldAttach)
            setParentCell(cellProperty);
    }

    public void setParentCell(final CellProperty cellProperty) {
        Preconditions.checkNotNull(cellProperty);

        // if we are already in that cell, don't do anything.
        if (getParentCell() == cellProperty)
            return;

        assert !isChildObject() : String.format("called on child object [id=%d template=%s] with parent object [id=%d template=%s]",
                getNetworkId(), getObjectTemplateName(),
                attachedToObject.getNetworkId(), attachedToObject.getObjectTemplateName());

        // if we were in another cell, detach us.  This will leave out object in world space.
        if (!isInWorldCell())
            detachFromObject(DetachFlags.WORLD);

        // we are now relative to the new cell
        if (cellProperty != CellProperty.getWorldCellProperty())
            attachToObjectInWorld(cellProperty.getOwner(), false);

        // issue a cell notification change
        cellChanged(false);
    }

    public CellProperty getParentCell() {
        Property cell = null;

        for (GameObject o = getAttachedTo(); o != null && cell == null; o = o.getAttachedTo())
            cell = o.getCellProperty();

        if (cell == null)
            return CellProperty.getWorldCellProperty();

        return (CellProperty) cell;
    }

    /**
     * Add a new child object to the end of the child object list.
     * <p>
     * This routine will append the new child object to the list of child
     * objects.
     *
     * @param childObject New child object
     */
    public void addChildObjectToObject(final GameObject childObject) {
        childObject.attachToObjectInParent(this, true);
    }

    /**
     * Add a new child object to the end of the child object list.
     * <p>
     * This routine will append the new child object to the list of child
     * objects.
     *
     * @param childObject New child object
     */
    public void addChildObjectToParent(final GameObject childObject) {
        //calculate frame and position in this object's space
        final Vector p = rotateTranslateParentToObject(childObject.getPositionInParent());
        final Vector i = rotateParentToObject(childObject.getObjectFrameIInParent());
        final Vector j = rotateParentToObject(childObject.getObjectFrameJInParent());
        final Vector k = rotateParentToObject(childObject.getObjectFrameKInParent());

        //set the transform of the child object
        childObject.setTransformIJKObjectToParent(i, j, k);
        childObject.setPositionInParent(p);
        childObject.reorthonormalize();

        addChildObjectToObject(childObject);
    }

    /**
     * Get the object at the top level of the object hierarchy
     *
     * @return the object at the top of the hierarchy or the current object if the object is at the top of the hierarchy
     */
    public GameObject getRootParent() {
        if (childObject && attachedToObject == null)
            throw new IllegalStateException("Child but attachedTo is null");

        return childObject ? attachedToObject.getRootParent() : this;
    }

    public int getNumberOfAttachedObjects() {
        return attachedObjects.size();
    }

    public GameObject getAttachedObject(int attachedObjectIndex) {
        return attachedObjects.get(attachedObjectIndex);
    }

    public int getNumberOfChildObjects() {
        return attachedObjects != null ? attachedObjects.size() : 0;
    }

    /**
     * Get the specified child object.
     * <p>
     * If the childObjectIndex is out of range, this routine will call Fatal
     * in debug compiles.
     *
     * @param childObjectIndex Index of the child object to retrieve
     * @return Pointer to the child object
     */
    public GameObject getChildObject(int childObjectIndex) {
        Preconditions.checkNotNull(attachedObjects);

        assert childObjectIndex >= 0 || childObjectIndex < getNumberOfChildObjects()
                : String.format("child object index out of range %d/%d", childObjectIndex, getNumberOfChildObjects());

        final GameObject child = attachedObjects.get(childObjectIndex);
        Preconditions.checkNotNull(child);

        return child;
    }

    /**
     * Get the object to world transformation for this object.
     *
     * @return The object-to-world transformation for this object.
     */
    public Transform getTransformObjectToWorld() {
        if (attachedToObject != null) {
            if (objectToWorldDirty) {
                objectToWorld.multiply(attachedToObject.getTransformObjectToWorld(), objectToParent);
                setObjectToWorldDirty(false);
            }
            return objectToWorld;
        }

        return objectToParent;
    }

    public void setTransformObjectToWorld(final Transform newOjectToWorld) {
        final CellProperty cell = getParentCell();

        if (cell == CellProperty.getWorldCellProperty()) {
            setTransformObjectToParent(newOjectToWorld);
            return;
        }

        final Transform cellToWorld = cell.getOwner().getTransformObjectToWorld();
        final Transform worldToCell = new Transform();
        worldToCell.invert(cellToWorld);

        final Transform objectToCell = new Transform();
        objectToCell.multiply(worldToCell, newOjectToWorld);

        setTransformObjectToParent(objectToCell);
    }

    public Transform getTransformObjectToParent() {
        return objectToParent;
    }

    public Transform getTransformObjectToCell() {
        final Transform result = getTransformObjectToParent();

        for (GameObject obj = getAttachedTo(); obj != null && obj.getCellProperty() == null; obj = obj.getAttachedTo())
            result.multiply(obj.getTransformObjectToParent(), result);

        return result;
    }

    /**
     * Gets the position in world space.
     *
     * @return
     */
    public Vector getPositionInWorld() {
        return getTransformObjectToWorld().getPositionInParent();
    }

    /**
     * Gets the position in parent space.
     *
     * @return
     */
    public Vector getPositionInParent() {
        return objectToParent.getPositionInParent();
    }

    /**
     * Gets the position cell space.
     *
     * @return
     */
    public Vector getPositionInCell() {
        return getTransformObjectToCell().getPositionInParent();
    }

    /**
     * Set the position of this object in its parent space.
     *
     * @param newPositionInParentSpace New position in the parent's space
     */
    public void setPositionInParent(final Vector newPositionInParentSpace) {
        final Vector oldPosition = getPositionInParent();
        objectToParent.setPositionInParentSpace(newPositionInParentSpace);
        positionChanged(false, oldPosition);
    }

    /**
     * Set the position of this object in world space.
     *
     * @param newPositionInWorldSpace New position in the world space
     */
    public void setPositionInWorld(final Vector newPositionInWorldSpace) {
        if (attachedToObject != null)
            setPositionInParent(attachedToObject.rotateTranslateWorldToObject(newPositionInWorldSpace));
        else
            setPositionInParent(newPositionInWorldSpace);
    }

    /**
     * Set the object-to-parent transform for this object.
     *
     * @param newObjectToParentTransform New object-to-parent transform
     */
    public void setTransformObjectToParent(final Transform newObjectToParentTransform) {
        final Vector oldPosition = getPositionInParent();
        objectToParent = newObjectToParentTransform;
        positionAndRotationChanged(false, oldPosition);
    }

    /**
     * Reorthonormalize the object-to-world transform.
     * <p>
     * Repeated rotations will introduce numerical error into the transform,
     * which will cause the upper 3x3 matrix to become non-orthonormal.  If
     * enough error is introduced, weird errors will begin to occur when using
     * the transform.
     * <p>
     * This routine attempts to reduce the numerical error by reorthonormalizing
     * the upper 3x3 matrix.
     */
    public void reorthonormalize() {
        objectToParent.reorthonormalize();
        rotations = 0;
    }

    /**
     * Yaw the object around its Y axis.
     * <p>
     * This routine will rotate the object around its Y axis by the specified
     * number of radians.
     * <p>
     * Positive rotations are clockwise when viewed from the positive side of
     * the axis being rotated about looking towards the origin.
     *
     * @param radians Radians to yaw the object.
     * @see GameObject#roll(float)
     * @see GameObject#pitch(float)
     */
    public void yaw(float radians) {
        if (radians != 0.0f) {
            objectToParent.yaw(radians);

            if (++rotations >= ROTATIONS_BETWEEN_REORTHONORMALIZE)
                reorthonormalize();

            rotationChanged(false);
        }
    }

    /**
     * Pitch the object around its X axis.
     * <p>
     * This routine will rotate the object around its X axis by the specified
     * number of radians.
     * <p>
     * Positive rotations are clockwise when viewed from the positive side of
     * the axis being rotated about looking towards the origin.
     *
     * @param radians Radians to pitch the object.
     * @see GameObject#yaw(float)
     * @see GameObject#roll(float)
     */
    public void pitch(float radians) {
        if (radians != 0.0f) {
            objectToParent.pitch(radians);

            if (++rotations >= ROTATIONS_BETWEEN_REORTHONORMALIZE)
                reorthonormalize();

            rotationChanged(false);
        }
    }

    /**
     * Roll the object around its Z axis.
     * <p>
     * This routine will rotate the object around its Z axis by the specified
     * number of radians.
     * <p>
     * Positive rotations are clockwise when viewed from the positive side of
     * the axis being rotated about looking towards the origin.
     *
     * @param radians Radians to roll the object.
     * @see GameObject#yaw(float)
     * @see GameObject#pitch(float)
     */
    public void roll(float radians) {
        if (radians != 0.0f) {
            objectToParent.roll(radians);

            if (++rotations >= ROTATIONS_BETWEEN_REORTHONORMALIZE)
                reorthonormalize();

            rotationChanged(false);
        }
    }

    /**
     * Reset the objectToParent transform's rotations.
     * <p>
     * This does NOT affect the object's position.  This will make the object
     * have the same orientation as its parent.
     */
    public void resetRotateObjectToParent() {
        objectToParent.resetRotateLocalSpaceToParentSpace();
        rotations = 0;
        rotationChanged(false);
    }

    /**
     * Reset the objectToParent transform.
     * <p>
     * This will make the object have the same orientation and position as its parent.
     */
    public void resetRotateTranslateObjectToParent() {
        final Vector oldPosition = getPositionInParent();
        objectToParent.resetRotateTranslateLocalSpaceToParentSpace();
        rotations = 0;
        positionAndRotationChanged(false, oldPosition);
    }

    /**
     * Rotate vector from the object's frame to the parent frame.
     *
     * @param vector Vector to rotate from object space into parent space
     * @return The source object-space vector rotated into parent space
     */
    public Vector rotateObjectToParent(final Vector vector) {
        return objectToParent.rotateLocalToParent(vector);
    }

    /**
     * Calculate vector from the object's frame to the parent frame.
     *
     * @param vector Vector to rotate and translate from object space into parent space
     * @return The source object-space vector rotated and translated into parent space
     */
    public Vector rotateTranslateObjectToParent(final Vector vector) {
        return objectToParent.rotateTranslateLocalToParent(vector);
    }

    /**
     * Rotate vector from the parent frame to the object's frame.
     *
     * @param vector Vector to rotate from parent space into object space
     * @return The source parent-space vector rotated into object space
     */
    public Vector rotateParentToObject(final Vector vector) {
        return objectToParent.rotateParentToLocal(vector);
    }

    /**
     * Calculate vector from the parent frame to the object's frame.
     *
     * @param vector Vector to rotate and translate from parent space into object space
     * @return The source parent-space vector rotated into object space
     */
    public Vector rotateTranslateParentToObject(final Vector vector) {
        return objectToParent.rotateTranslateParentToLocal(vector);
    }

    /**
     * Rotate vector from the object's frame to the world frame.
     *
     * @param vector Vector to rotate from object space into world space
     * @return The source object-space vector rotated into world space
     */
    public Vector rotateObjectToWorld(final Vector vector) {
        return getTransformObjectToWorld().rotateLocalToParent(vector);
    }

    /**
     * Calculate vector from the object's frame to the world frame.
     *
     * @param vector Vector to rotate and translate from object space into world space
     * @return The source object-space vector rotated and translated into world space
     */
    public Vector rotateTranslateObjectToWorld(final Vector vector) {
        return getTransformObjectToWorld().rotateTranslateLocalToParent(vector);
    }

    /**
     * Rotate vector from the world frame to the object's frame.
     *
     * @param vector Vector to rotate from world space into object space
     * @return The source world-space vector rotated into object space
     */
    public Vector rotateWorldToObject(final Vector vector) {
        return getTransformObjectToWorld().rotateParentToLocal(vector);
    }

    /**
     * Calculate vector from the world frame to the object's frame.
     *
     * @param vector Vector to rotate and translate from world space into object space
     * @return The source world-space vector rotated into object space
     */
    public Vector rotateTranslateWorldToObject(final Vector vector) {
        return getTransformObjectToWorld().rotateTranslateParentToLocal(vector);
    }

    /**
     * Calculate vector from the parent frame to the world frame.
     *
     * @param vector Vector to rotate and translate from parent space into world space
     * @return The source parent-space vector rotated into world space
     */
    public Vector rotateTranslateParentToWorld(final Vector vector) {
        if (attachedToObject != null)
            return attachedToObject.rotateTranslateObjectToWorld(vector);

        return vector;
    }

    /**
     * Calculate vector from the parent frame to the world frame.
     *
     * @param vector Vector to rotate from parent space into world space
     * @return The source parent-space vector rotated into world space
     */
    public Vector rotateParentToWorld(final Vector vector) {
        if (attachedToObject != null)
            return attachedToObject.rotateObjectToWorld(vector);

        return vector;
    }

    /**
     * Calculate vector from the world frame to the parent frame.
     *
     * @param vector Vector to rotate from world space into parent space
     * @return The source world-space vector rotated into parent space
     */
    public Vector rotateWorldToParent(final Vector vector) {
        if (attachedToObject != null)
            return attachedToObject.rotateWorldToObject(vector);

        return vector;
    }

    /**
     * Calculate vector from the world frame to the parent frame.
     *
     * @param vector Vector to rotate from world space into parent space
     * @return The source world-space vector rotated into parent space
     */
    public Vector rotateTranslateWorldToParent(final Vector vector) {
        if (attachedToObject != null)
            return attachedToObject.rotateTranslateWorldToObject(vector);

        return vector;
    }

    /**
     * Move the object in it's own local space.
     * <p>
     * This routine moves the object according to its current frame of reference.
     * Therefore, moving along the Z axis will move the object forward in the direction
     * in which it is pointed.
     *
     * @param vectorInObjectSpace Offset to move in local space
     * @see GameObject#moveInParent(Vector)
     */
    public void moveInLocal(final Vector vectorInObjectSpace) {
        moveInParent(rotateObjectToParent(vectorInObjectSpace));
    }

    /**
     * Move the object in it's parent space.
     * <p>
     * This routine moves the object in it's parent space, or the world space if
     * the object has no parent. Therefore, moving along the Z axis will move the
     * object forward along the Z-axis of it's parent space, not forward in the
     * direction in which it is pointed.
     *
     * @param vector Offset to move in parent space
     * @see GameObject#moveInLocal(Vector)
     */
    public void moveInParent(final Vector vector) {
        if (vector != Vector.ZERO) {
            final Vector oldPosition = getPositionInParent();
            objectToParent.moveInParentSpace(vector);
            positionChanged(false, oldPosition);
        }
    }

    /**
     * Get the parent-space vector pointing along the X axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     *
     * @return The vector pointing along the X axis of the frame in parent space
     */
    public Vector getObjectFrameIInParent() {
        return objectToParent.getLocalFrameIInParentSpace();
    }

    public Vector getObjectFrameJInParent() {
        return objectToParent.getLocalFrameJInParentSpace();
    }

    public Vector getObjectFrameKInParent() {
        return objectToParent.getLocalFrameKInParentSpace();
    }

    /**
     * Get the parent-space vector pointing along the X axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     *
     * @return The vector pointing along the X axis of the frame in parent space
     */
    public Vector getObjectFrameIInWorld() {
        return getTransformObjectToWorld().getLocalFrameIInParentSpace();
    }

    /**
     * Get the parent-space vector pointing along the Y axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     *
     * @return The vector pointing along the Y axis of the frame in parent space
     */
    public Vector getObjectFrameJInWorld() {
        return getTransformObjectToWorld().getLocalFrameJInParentSpace();
    }

    /**
     * Get the parent-space vector pointing along the Z axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     *
     * @return The vector pointing along the Z axis of the frame in parent space
     */
    public Vector getObjectFrameKInWorld() {
        return getTransformObjectToWorld().getLocalFrameKInParentSpace();
    }

    /**
     * Set the object-to-parent transform for this object.
     *
     * @param i Unit vector along the X axis
     * @param j Unit vector along the Y axis
     * @param k Unit vector along the Z axis
     */
    public void setTransformIJKObjectToParent(final Vector i, final Vector j, final Vector k) {
        objectToParent.setLocalFrameIJKInParentSpace(i, j, k);
        rotationChanged(false);
    }

    /**
     * Set the object-to-parent transform for this object.
     *
     * @param k Unit vector along the Z axis
     * @param j Unit vector along the Y axis
     */
    public void setTransformKJObjectToParent(final Vector k, final Vector j) {
        objectToParent.setLocalFrameKJInParentSpace(k, j);
        rotationChanged(false);
    }

    public void addToWorld() {
        if (inWorld) {
            LOGGER.warn("Object ({} : {}) is already in the world.",
                    getObjectTemplateName(), getNetworkId());
            return;
        }

        setObjectToWorldDirty(true);

        inWorld = true;

        //if (appearance != null)
        //    appearance.addToWorld();

        //notificationList.addToWorld(this);

        if (attachedObjects != null && !attachedObjects.isEmpty()) {
            attachedObjects.stream()
                    .filter(attachedObject -> attachedObject.isChildObject() && !attachedObject.isInWorld())
                    .forEach(GameObject::addToWorld);
        }
    }

    public void addProperty(final Property property) {
        addProperty(property, false);
    }

    public void addProperty(final Property property, final boolean allowWhileInWorld) {
        final int propertyId = property.getPropertyId();
        final boolean inWorldNow = isInWorld();

        assert getProperty(propertyId) == null : "Cannot add existing property.";
        assert inWorldNow && allowWhileInWorld : "Cannot add property to object already in world.";

        propertyList.add(property);

        if (propertyId == CellProperty.getClassPropertyId()
                || propertyId == PortalProperty.getClassPropertyId()
                || propertyId == SlottedContainer.getClassPropertyId()
                || propertyId == VolumeContainer.getClassPropertyId()) {
            assert containerProperty == null : "Object already has a container.";
            containerProperty = (Container) property;
        } else if (propertyId == ContainedByProperty.getClassPropertyId()) {
            assert containedByProperty == null : "Object already has a contained by property.";
            containedByProperty = (ContainedByProperty) property;
        } else if (propertyId == CollisionProperty.getClassPropertyId()) {
            assert collisionProperty == null : "Object already has a collision property.";
            collisionProperty = (CollisionProperty) property;
        }

        if (inWorldNow) //Only always true when asserts are turned on.
            property.addToWorld();
    }

    @SuppressWarnings("unchecked")
    public <PropertyType extends Property> PropertyType getProperty(final int propertyId) {
        for (final Property property : propertyList) {
            if (property.getPropertyId() == propertyId)
                return (PropertyType) property;
        }

        return null;
    }

    public void removeProperty(final int propertyId) {
        final Iterator<Property> iterator = propertyList.iterator();

        while (iterator.hasNext()) {
            final Property property = iterator.next();

            if (property.getPropertyId() == propertyId)
                iterator.remove();
        }

        if (propertyId == CellProperty.getClassPropertyId()
                || propertyId == PortalProperty.getClassPropertyId()
                || propertyId == SlottedContainer.getClassPropertyId()
                || propertyId == VolumeContainer.getClassPropertyId()) {
            containerProperty = null;
        } else if (propertyId == ContainedByProperty.getClassPropertyId()) {
            containedByProperty = null;
        } else if (propertyId == CollisionProperty.getClassPropertyId())
            collisionProperty = null;
    }

    public void removeFromWorld() {
    }

    public SlottedContainer getSlottedContainerProperty() {
        final Container container = getContainerProperty();

        if (container != null && container.getPropertyId() == SlottedContainer.getClassPropertyId())
            return (SlottedContainer) container;

        return null;
    }

    public VolumeContainer getVolumeContainerProperty() {
        final Container container = getContainerProperty();

        if (container != null && container.getPropertyId() == VolumeContainer.getClassPropertyId())
            return (VolumeContainer) container;

        return null;
    }

    public CellProperty getCellProperty() {
        final Container container = getContainerProperty();

        if (container != null && container.getPropertyId() == CellProperty.getClassPropertyId())
            return (CellProperty) container;

        return null;
    }

    public PortalProperty getPortalProperty() {
        final Container container = getContainerProperty();

        if (container != null && container.getPropertyId() == PortalProperty.getClassPropertyId())
            return (PortalProperty) container;

        return null;
    }

    public boolean isInWorldCell() {
        return getParentCell() == CellProperty.getWorldCellProperty();
    }

    /**
     * Called after the specified object has its containedByProperty changed
     * <p>
     * This default implementation does nothing.
     * <p>
     * Each object is assumed to have no more than one container associated
     * with it; therefore, it is unambiguous which container is under
     * consideration for this object.
     *
     * @param oldValue The container that used to hold this object
     * @param newValue The container that now holds this object.
     */
    public void containedByModified(final GameObject oldValue, final GameObject newValue, boolean isLocal) {

    }

    /**
     * Called after the specified object has its slotted arrangement changed
     * <p>
     * This default implementation does nothing.
     * <p>
     * Each object is assumed to have no more than one container associated
     * with it; therefore, it is unambiguous which container is under
     * consideration for this object.
     *
     * @param oldValue The old arrangement
     * @param newValue The new arrangement
     */
    public void arrangementModified(final int oldValue, final int newValue, boolean isLocal) {

    }

    public void positionChanged(final boolean dueToParentChange, final Vector oldPosition) {
        setObjectToWorldDirty(true);

        if (inWorld) {
            validatePosition(this, getPositionInParent());

            //notificationsList.positionChanged(this, dueToParentChange, oldPosition);

            if (attachedObjects != null && !attachedObjects.isEmpty()) {
                for (final GameObject attachedObject : attachedObjects) {
                    attachedObject.positionChanged(true, attachedObject.getPositionInParent());
                }
            }
        }
    }

    public void rotationChanged(final boolean dueToParentChange) {
        setObjectToWorldDirty(true);

        if (inWorld) {
            //notificationList.rotationChanged(this, dueToParentChange);

            if (attachedObjects != null && !attachedObjects.isEmpty()) {
                for (final GameObject attachedObject : attachedObjects) {
                    attachedObject.rotationChanged(true);
                }
            }
        }
    }

    public void positionAndRotationChanged(final boolean dueToParentChange, final Vector oldPosition) {
        setObjectToWorldDirty(true);

        if (inWorld) {
            validatePosition(this, getPositionInParent());

            //notificationsList.positionAndRotationChanged(this, dueToParentChange, oldPosition);

            if (attachedObjects != null && !attachedObjects.isEmpty()) {
                for (final GameObject attachedObject : attachedObjects) {
                    attachedObject.positionAndRotationChanged(true, attachedObject.getPositionInParent());
                }
            }
        }
    }

    public void cellChanged(final boolean dueToParentChange) {
        setObjectToWorldDirty(true);

        if (inWorld) {
            //notificationList.cellChanged(this, dueToParentChange);

            if (attachedObjects != null && !attachedObjects.isEmpty()) {
                for (final GameObject object : attachedObjects)
                    object.cellChanged(true);
            }
        }
    }

    public static void validatePosition(final GameObject object, final Vector position) {
        if (Math.abs(position.x) > 16000.f || Math.abs(position.y) > 16000.f || Math.abs(position.z) > 16000.f) {
            final GameObject parent = object.getAttachedTo();

            final String errorMssage = String.format("Position (%f,%f,%f) is out-of-world and is invalid for object id=[%d], template=[%s], appearance=[%s], parent id=[%d], template=[%s], appearance=[%s]",
                    position.x,
                    position.y,
                    position.z,
                    object.getNetworkId(),
                    object.getObjectTemplateName(),
                    object.getAppearance() != null ? object.getAppearance().getAppearanceTemplateName() : null,
                    parent != null ? parent.getNetworkId() : null,
                    parent != null ? parent.getObjectTemplateName() : null,
                    parent != null && parent.getAppearance() != null ? parent.getAppearance().getAppearanceTemplateName() : null);

            LOGGER.error(errorMssage);
            throw new IllegalStateException(errorMssage);
        }
    }

    public enum DetachFlags {
        NONE,
        PARENT,
        WORLD
    }
}
