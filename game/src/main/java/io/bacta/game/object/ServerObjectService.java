package io.bacta.game.object;

import io.bacta.game.container.ContainerTransferService;
import io.bacta.game.db.InMemoryServerObjectDatabase;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.swg.container.ContainerResult;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.template.ObjectTemplateList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Constructor;

@Slf4j
@Service
public class ServerObjectService {
    private final InMemoryServerObjectDatabase database;
    private final ApplicationContext applicationContext;
    private final ObjectTemplateService objectTemplateService;
    private final NetworkIdService networkIdService;
    private final ContainerTransferService containerTransferService;

    @Inject
    public ServerObjectService(InMemoryServerObjectDatabase database,
                               ApplicationContext applicationContext,
                               ObjectTemplateService objectTemplateService,
                               NetworkIdService networkIdService,
                               ContainerTransferService containerTransferService) {
        this.database = database;
        this.applicationContext = applicationContext;
        this.objectTemplateService = objectTemplateService;
        this.networkIdService = networkIdService;
        this.containerTransferService = containerTransferService;
    }

    public <T extends ServerObject> T createObject(final String templatePath) {
        return createObject(templatePath, null);
    }

    public <T extends ServerObject> T createObject(final String templatePath, final ServerObject parent) {
        final T object = internalCreateObject(templatePath);

        if (object != null && parent != null) {
            final ContainerResult containerResult = new ContainerResult();

            if (!containerTransferService.transferItemToGeneralContainer(parent, object, null, containerResult))
                LOGGER.error("Unable to transfer object {} to parent object {} during creation. Error: {}",
                        object.getNetworkId(), parent.getNetworkId(), containerResult.getError());
        }

        return object;
    }

    public <T extends ServerObject> T createObjectInSlot(final String templatePath, final ServerObject parent, final int slotId) {
        final T object = internalCreateObject(templatePath);

        if (object != null && parent != null) {
            final ContainerResult containerResult = new ContainerResult();

            if (!containerTransferService.transferItemToSlottedContainerSlotId(parent, object, null, slotId, containerResult)) {
                LOGGER.error("Unable to transfer object {} to parent object {} in slot {} during creation. Error: {}",
                        object.getNetworkId(), parent.getNetworkId(), slotId, containerResult.getError());
            }
        }

        return object;
    }

    private <T extends ServerObject> T internalCreateObject(final String templatePath) {
        try {
            final ObjectTemplateList objectTemplateList = applicationContext.getBean(ObjectTemplateList.class);
            final SlotIdManager slotIdManager = applicationContext.getBean(SlotIdManager.class);

            final ServerObjectTemplate serverObjectTemplate = objectTemplateService.getObjectTemplate(templatePath);
            final Class<T> objectClass = objectTemplateService.getClassForTemplate(serverObjectTemplate);
            //final ObjectInitializer<T> objectInitializer = objectInitializerProvider.get(objectClass);

            final Constructor<T> gameObjectConstructor =
                    objectClass.getConstructor(ObjectTemplateList.class, SlotIdManager.class, ServerObjectTemplate.class);

            final T newObject = gameObjectConstructor.newInstance(objectTemplateList, slotIdManager, serverObjectTemplate);
            newObject.setNetworkId(networkIdService.nextNetworkId());

            //Put it in the database.
            database.put(newObject);

            //Send a message to object supervisor?!

            //Create the object
            //final T newObject = (T) networkObjectFactory.createNetworkObject(objectClass, serverObjectTemplate);
            //databaseConnector.persist(newObject);

            //Initialize the object
//            if (objectInitializer != null) {
//                objectInitializer.initializeFirstTimeObject(newObject);
//            } else {
//                LOGGER.warn("No initializer is bound for class {}.", objectClass.getName());
//            }

            //newObject.setOnDirtyCallback(new ServerObjectServiceOnDirtyCallback(newObject));

            //internalMap.put(newObject.getNetworkId(), newObject);

            return newObject;
        } catch (final Exception ex) {
            LOGGER.error("Exception creating object {}. Message: {}", templatePath, ex.getMessage());
            return null;
        }
    }

    //@Override
    @SuppressWarnings("unchecked")
    public <T extends ServerObject> T get(long key) {
        T object = database.lookup(key);

//        if (object == null) {
//            object = databaseConnector.get(key);
//
//            if(object != null) {
//                internalMap.put(key, object);
//            }
//        }

        return object;
    }

    //@Override
    public <T extends ServerObject> T get(ServerObject requester, long key) {
        //TODO: Reimplement permissions.

        return get(key);
    }

    //@Override
    public <T extends ServerObject> void updateObject(T object) {
        //databaseConnector.persist(object);
    }

//
//    // Executor?
//    private class DeltaNetworkDispatcher implements Runnable {
//
//        protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//
//        @Override
//        public void run() {
//
//            long nextIteration = 0;
//
//            while(true) {
//                try {
//                    long currentTime = System.currentTimeMillis();
//
//                    if (nextIteration > currentTime) {
//                        Thread.sleep(nextIteration - currentTime);
//                    }
//
//                    for (ServerObject object : dirtyList) {
//                        if (object.isInitialized())
//                            object.sendDeltas();
//
//                        object.clearDeltas();
//                    }
//
//                    dirtyList.clear();
//
//                    nextIteration = currentTime + deltaUpdateInterval;
//
//                } catch(Exception e) {
//                    logger.error("UNKNOWN", e);
//                }
//            }
//        }
//    }
//
//    private final class ServerObjectServiceOnDirtyCallback implements OnDirtyCallbackBase {
//        private final ServerObject serverObject;
//
//        public ServerObjectServiceOnDirtyCallback(final ServerObject serverObject) {
//            this.serverObject = serverObject;
//        }
//
//        @Override
//        public void onDirty() {
//            dirtyList.add(serverObject);
//        }
//
//    }
}
