package io.bacta.game.container;

import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.engine.utils.ReflectionUtil;
import io.bacta.shared.container.ContainedByProperty;
import io.bacta.shared.container.Container;
import io.bacta.shared.container.ContainerErrorCode;
import io.bacta.shared.container.ContainerResult;
import io.bacta.shared.object.GameObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 5/3/2016.
 * <p>
 * Used for making transfers with a Container object directly. Please go through the
 * {@link ContainerTransferService} if making transfers.
 */
@Slf4j
@Service
public class ContainerService {
    static final Field changedField = ReflectionUtil.getFieldOrNull(Container.class, "changed");
    static final Field contentsField = ReflectionUtil.getFieldOrNull(Container.class, "contents");

    private final boolean loopChecking;
    private final int maxDepth;

    @Inject
    public ContainerService(final BactaConfiguration configuration) {
        loopChecking = configuration.getBooleanWithDefault("SharedObject", "containerLoopChecking", true);
        maxDepth = configuration.getIntWithDefault("SharedObject", "containerMaxDepth", 9);
    }

    public boolean isContentItemObservedWith(final Container container, final GameObject item) {
        return false;
    }

    public boolean isContentItemExposedWith(final Container container, final GameObject item) {
        return false;
    }

    public boolean canContentsBeObservedWith(final Container container) {
        return false;
    }

    public boolean mayAdd(final Container container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (item == container.getOwner()) {
            containerResult.setError(ContainerErrorCode.ADD_SELF);
            return false;
        }

        if (loopChecking) {
            final Container containerToCheck = item.getContainerProperty();

            if (containerToCheck != null) {
                ContainedByProperty cbIter = container.getOwner().getContainedByProperty();

                if (cbIter != null) {
                    GameObject iterObject = cbIter.getContainedBy();

                    if (iterObject != null) {
                        final List<GameObject> checkList = new ArrayList<>(10);
                        checkList.add(container.getOwner());

                        for (int count = 0; iterObject != null; ++count) {
                            if (count > maxDepth) {
                                LOGGER.warn("Too deep a container hierarchy.");
                                containerResult.setError(ContainerErrorCode.TOO_DEEP);
                                return false;
                            }

                            checkList.add(iterObject);
                            final GameObject obj = cbIter.getContainedBy();
                            iterObject = null;

                            if (obj != null) {
                                cbIter = obj.getContainedByProperty();

                                if (cbIter != null)
                                    iterObject = cbIter.getContainedBy();
                            }
                        }

                        if (checkList.contains(item)) {
                            LOGGER.warn("Adding item {} to {} would have introduced a container loop.",
                                    item.getNetworkId(),
                                    container.getOwner().getNetworkId());

                            containerResult.setError(ContainerErrorCode.ALREADY_IN);
                            return false;
                        }
                    }
                }
            }
        }

        if (checkDepth(container) > maxDepth) {
            containerResult.setError(ContainerErrorCode.TOO_DEEP);
            return false;
        }

        return true;
    }

    public boolean remove(final Container container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.UNKNOWN);

        boolean returnValue = false;

        ContainedByProperty property = item.getContainedByProperty();

        if (property != null) {
            if (property.getContainedBy() != container.getOwner()) {
                LOGGER.warn("Cannot remove an item [{}] from container [{}] whose containedBy says it isn't in this container.",
                        item.getNetworkId(),
                        container.getOwner().getNetworkId());
                containerResult.setError(ContainerErrorCode.NOT_FOUND);
                return false;
            }

            final List<GameObject> contents = ReflectionUtil.getFieldValue(contentsField, container);
            returnValue = contents.remove(item);

            if (returnValue) {
                containerResult.setError(ContainerErrorCode.SUCCESS);
                property.setContainedBy(null);
            }
        }

        return returnValue;
    }

    public boolean remove(final Container container, final int position, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final List<GameObject> contents = ReflectionUtil.getFieldValue(contentsField, container);
        final GameObject obj = contents.get(position);

        if (obj != null) {
            ContainedByProperty property = obj.getContainedByProperty();

            if (property != null) {
                if (property.getContainedBy() != container.getOwner()) {
                    LOGGER.warn("Cannot remove an item [{}] from container [{}] whose containedBy says it isn't in this container.",
                            obj.getNetworkId(),
                            container.getOwner().getNetworkId());
                    containerResult.setError(ContainerErrorCode.NOT_FOUND);
                    return false;
                }
                property.setContainedBy(null);
            }

            contents.remove(position);
            return true;
        }

        containerResult.setError(ContainerErrorCode.UNKNOWN);
        return false;
    }

    protected int addToContents(final Container container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (!mayAdd(container, item, containerResult))
            return -1;

        ContainedByProperty containedItem = item.getContainedByProperty();

        assert containedItem != null : "Cannot add an item with no containedByProperty to a container.";

        if (containedItem.getContainedBy() != null && containedItem.getContainedBy() != container.getOwner()) {
            LOGGER.warn("Cannot add an item [{}] to a container [{}] when it is already contained!",
                    item.getNetworkId(),
                    container.getOwner().getNetworkId());
            containerResult.setError(ContainerErrorCode.ALREADY_IN);
            return -1;
        }

        if (loopChecking) {
            final Container containerToCheck = item.getContainerProperty();

            if (containerToCheck != null) {
                ContainedByProperty cbIter = container.getOwner().getContainedByProperty();

                if (cbIter != null) {
                    GameObject iterObject = cbIter.getContainedBy();

                    if (iterObject != null) {
                        final List<GameObject> checkList = new ArrayList<>(10);
                        checkList.add(container.getOwner());

                        for (int count = 0; iterObject != null; ++count) {
                            if (count > maxDepth) {
                                LOGGER.warn("Too deep a container hierarchy.");
                                containerResult.setError(ContainerErrorCode.TOO_DEEP);
                                return -1;
                            }

                            checkList.add(iterObject);
                            final GameObject obj = cbIter.getContainedBy();
                            iterObject = null;

                            if (obj != null) {
                                cbIter = obj.getContainedByProperty();

                                if (cbIter != null)
                                    iterObject = cbIter.getContainedBy();
                            }
                        }

                        if (checkList.contains(item)) {
                            LOGGER.warn("Adding item {} to {} would have introduced a container loop.",
                                    item.getNetworkId(),
                                    container.getOwner().getNetworkId());
                            containerResult.setError(ContainerErrorCode.ALREADY_IN);
                            return -1;
                        }
                    }
                }
            }
        }

        if (checkDepth(container) > maxDepth) {
            containerResult.setError(ContainerErrorCode.TOO_DEEP);
            return -1;
        }

        if (isItemContained(container, item, containerResult)) {
            LOGGER.warn("Cannot add an item {} to a container {} when it is already in it! This shouldn't happen because the item's contained by property says it is not in this container, but it is in the container's internal list.",
                    item.getNetworkId(),
                    container.getOwner().getNetworkId());
            containerResult.setError(ContainerErrorCode.ALREADY_IN);
            return -1;
        }

        containedItem.setContainedBy(container.getOwner());

        final List<GameObject> contents = ReflectionUtil.getFieldValue(contentsField, container);
        contents.add(item);

        return contents.size() - 1;
    }

    protected int find(final Container container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final List<GameObject> contents = ReflectionUtil.getFieldValue(contentsField, container);
        int index = contents.indexOf(item);

        if (index == -1)
            containerResult.setError(ContainerErrorCode.NOT_FOUND);

        return index;
    }

    protected GameObject getContents(final Container container, final int position) {
        final List<GameObject> contents = ReflectionUtil.getFieldValue(contentsField, container);
        return contents.get(position);
    }

    private boolean isItemContained(final Container container, final GameObject item, final ContainerResult containerResult) {
        return find(container, item, containerResult) != -1;
    }

    public static int checkDepth(final Container container) {
        int count = 0;
        ContainedByProperty parentContainedByProperty = container.getOwner().getContainedByProperty();

        if (parentContainedByProperty == null)
            return count;

        GameObject parentObject = parentContainedByProperty.getContainedBy();

        if (parentObject == null)
            return count;

        while (parentObject != null) {
            ++count;
            parentContainedByProperty = parentObject.getContainedByProperty();

            if (parentContainedByProperty == null)
                return count;

            parentObject = parentContainedByProperty.getContainedBy();
        }

        return count;
    }

    public static GameObject getContainedByObject(final GameObject object) {
        final ContainedByProperty property = object.getContainedByProperty();
        return property == null ? null : property.getContainedBy();
    }
}
