package io.bacta.game.io.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import de.javakaffee.kryoserializers.BitSetSerializer;
import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import io.bacta.engine.network.NetworkObjectByteSerializer;
import io.bacta.engine.object.NetworkObject;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.cell.CellObject;
import io.bacta.game.object.intangible.IntangibleObject;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.intangible.schematic.DraftSchematicObject;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.tangible.ship.ShipObject;
import io.bacta.game.object.template.server.*;
import io.bacta.game.object.universe.UniverseObject;
import io.bacta.game.object.universe.group.GroupObject;
import io.bacta.game.object.universe.guild.GuildObject;
import io.bacta.game.object.universe.planet.PlanetObject;
import io.bacta.swg.collision.BarrierObject;
import io.bacta.swg.collision.DoorObject;
import io.bacta.swg.object.GameObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class KryoSerializer implements NetworkObjectByteSerializer {

    private final ThreadLocal<Kryo> kryo;
    private final GameObjectSerializer gameObjectSerializer;

    public KryoSerializer(final GameObjectSerializer gameObjectSerializer,
                          final ApplicationContext applicationContext) {

        this.gameObjectSerializer = gameObjectSerializer;

        kryo = ThreadLocal.withInitial(() -> {
            final Kryo kryo = new Kryo();

            // Require explicit registration
            kryo.setRegistrationRequired(true);
            registerTypes(kryo, applicationContext);

            return kryo;
        });

        kryo.get();
    }
    private void registerTypes(final Kryo kryo, final ApplicationContext injector) {
        // Kryo uses type 1-8 for java types

        // Reserves types 9-59
        registerSwgObjects(kryo, injector);

        // Reserves types 60-199
        registerSwgTemplates(kryo, injector);

        // Types start at 200
        kryo.register(BitSet.class, new BitSetSerializer(), 200);

        kryo.register(THashMap.class, new MapSerializer() {
            @Override
            protected Map create(Kryo kryo, Input input, Class<Map> type) {
                return new THashMap();
            }
        }, 201);

        kryo.register(TIntArrayList.class, new TIntCollectionSerializer() {
            @Override
            protected TIntCollection create(Kryo kryo, Input input, Class<TIntCollection> type) {
                return new TIntArrayList();
            }
        }, 202);

        kryo.register(ArrayList.class, new CollectionSerializer() {
            @Override
            protected Collection create(Kryo kryo, Input input, Class<Collection> type) {
                return new ArrayList();
            }
        }, 203);
    }

    private void registerSwgObjects(final Kryo kryo, final ApplicationContext injector) {

        final GameObjectReferenceSerializer gameObjectReferenceSerializer = injector.getBean(GameObjectReferenceSerializer.class);
        kryo.register(GameObject.class, gameObjectReferenceSerializer, 9);

        kryo.register(BarrierObject.class, gameObjectReferenceSerializer, 10);
//        kryo.register(BuildingObject.class, gameObjectReferenceSerializer, 11);
        kryo.register(CellObject.class, gameObjectReferenceSerializer, 12);
        kryo.register(CreatureObject.class, gameObjectReferenceSerializer, 13);
        kryo.register(DoorObject.class, gameObjectReferenceSerializer, 14);
        kryo.register(DraftSchematicObject.class, gameObjectReferenceSerializer, 15);
//        kryo.register(FactoryObject.class, gameObjectReferenceSerializer, 16);
        kryo.register(GroupObject.class, gameObjectReferenceSerializer, 17);
        kryo.register(GuildObject.class, gameObjectReferenceSerializer, 18);
//        kryo.register(HarvesterInstallationObject.class, gameObjectReferenceSerializer, 19);
//        kryo.register(InstallationObject.class, gameObjectReferenceSerializer, 20);
        kryo.register(IntangibleObject.class, gameObjectReferenceSerializer, 21);
        //kryo.register(ManufactureInstallationObject.class, gameObjectReferenceSerializer, 22);
        //kryo.register(ManufactureSchematicObject.class, gameObjectReferenceSerializer, 23);
        //kryo.register(MissionObject.class, gameObjectReferenceSerializer, 24);
        kryo.register(PlanetObject.class, gameObjectReferenceSerializer, 25);
        kryo.register(PlayerObject.class, gameObjectReferenceSerializer, 26);
        //kryo.register(PlayerQuestObject.class, gameObjectReferenceSerializer, 27);
        //kryo.register(ResourceContainerObject.class, gameObjectReferenceSerializer, 28);
        kryo.register(ServerObject.class, gameObjectReferenceSerializer, 29);
        kryo.register(ShipObject.class, gameObjectReferenceSerializer, 30);
        //kryo.register(StaticObject.class, gameObjectReferenceSerializer, 31);
        kryo.register(TangibleObject.class, gameObjectReferenceSerializer, 32);
        //kryo.register(TerrainObject.class, gameObjectReferenceSerializer, 33);
        kryo.register(UniverseObject.class, gameObjectReferenceSerializer, 34);
        //kryo.register(VehicleObject.class, gameObjectReferenceSerializer, 35);
        //kryo.register(WeaponObject.class, gameObjectReferenceSerializer, 36);

//        final Reflections reflections = new Reflections();
//        final Set<Class<? extends GameObject>> subTypes = reflections.getSubTypesOf(GameObject.class);
//        subTypes.forEach(clazz -> {
//            if( kryo.getRegistration(clazz) == null) {
//                LOGGER.error("Object not registered {}", clazz.getSimpleName());
//            }
//        });
    }

    private void registerSwgTemplates(final Kryo kryo, final ApplicationContext injector) {

        ObjectTemplateSerializer objectTemplateSerializer = injector.getBean(ObjectTemplateSerializer.class);
        kryo.register(ServerObjectTemplate.class, objectTemplateSerializer, 60);

        kryo.register(ServerBattlefieldMarkerObjectTemplate.class, objectTemplateSerializer, 61);
        kryo.register(ServerBuildingObjectTemplate.class, objectTemplateSerializer, 62);
        kryo.register(ServerCellObjectTemplate.class, objectTemplateSerializer, 63);
        kryo.register(ServerCityObjectTemplate.class, objectTemplateSerializer, 64);
        kryo.register(ServerConstructionContractObjectTemplate.class, objectTemplateSerializer, 65);
        kryo.register(ServerCreatureObjectTemplate.class, objectTemplateSerializer, 66);
        kryo.register(ServerDraftSchematicObjectTemplate.class, objectTemplateSerializer, 67);
        kryo.register(ServerFactoryObjectTemplate.class, objectTemplateSerializer, 68);
        kryo.register(ServerGroupObjectTemplate.class, objectTemplateSerializer, 69);
        kryo.register(ServerGuildObjectTemplate.class, objectTemplateSerializer, 70);
        kryo.register(ServerHarvesterInstallationObjectTemplate.class, objectTemplateSerializer, 71);
        kryo.register(ServerInstallationObjectTemplate.class, objectTemplateSerializer, 72);
        kryo.register(ServerIntangibleObjectTemplate.class, objectTemplateSerializer, 73);
        kryo.register(ServerJediManagerObjectTemplate.class, objectTemplateSerializer, 74);
        kryo.register(ServerManufactureInstallationObjectTemplate.class, objectTemplateSerializer, 75);
        kryo.register(ServerManufactureSchematicObjectTemplate.class, objectTemplateSerializer, 76);
        kryo.register(ServerMissionBoardObjectTemplate.class, objectTemplateSerializer, 77);
        kryo.register(ServerMissionDataObjectTemplate.class, objectTemplateSerializer, 78);
        kryo.register(ServerMissionListEntryObjectTemplate.class, objectTemplateSerializer, 79);
        kryo.register(ServerMissionObjectTemplate.class, objectTemplateSerializer, 80);
        kryo.register(ServerPlanetObjectTemplate.class, objectTemplateSerializer, 81);
        kryo.register(ServerPlayerObjectTemplate.class, objectTemplateSerializer, 82);
        kryo.register(ServerPlayerQuestObjectTemplate.class, objectTemplateSerializer, 83);
        kryo.register(ServerResourceContainerObjectTemplate.class, objectTemplateSerializer, 84);
        kryo.register(ServerShipObjectTemplate.class, objectTemplateSerializer, 85);
        kryo.register(ServerStaticObjectTemplate.class, objectTemplateSerializer, 86);
        kryo.register(ServerTangibleObjectTemplate.class, objectTemplateSerializer, 87);
        kryo.register(ServerTokenObjectTemplate.class, objectTemplateSerializer, 88);
        kryo.register(ServerUniverseObjectTemplate.class, objectTemplateSerializer, 89);
        kryo.register(ServerVehicleObjectTemplate.class, objectTemplateSerializer, 90);
        kryo.register(ServerWeaponObjectTemplate.class, objectTemplateSerializer, 91);
        kryo.register(ServerXpManagerObjectTemplate.class, objectTemplateSerializer, 92);

//        final Reflections reflections = new Reflections();
//        final Set<Class<? extends ServerObjectTemplate>> templateSubTypes = reflections.getSubTypesOf(ServerObjectTemplate.class);
//        templateSubTypes.forEach(clazz -> {
//            if( kryo.getRegistration(clazz) == null) {
//                LOGGER.error("Template class not registered {}", clazz.getSimpleName());
//            }
//        });
    }

    @Override
    public <T extends NetworkObject> byte[] serialize(T object) {
        try {
            final Kryo kryo = this.kryo.get();
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final UnsafeOutput output = new UnsafeOutput(stream);
            kryo.writeClass(output, object.getClass());
            kryo.writeObject(output, object, gameObjectSerializer);
            output.flush();
            output.close();
            return stream.toByteArray();
        } catch (Exception exception) {
            LOGGER.error("Error with class " + object.getClass().getName());
            throw new RuntimeException(exception);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends NetworkObject> T deserialize(byte[] bytes) {
        try {
            final Kryo kryo = this.kryo.get();
            final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            final UnsafeInput input = new UnsafeInput(stream);
            final Registration registration = kryo.readClass(input);
            final T result = (T) kryo.readObject(input, registration.getType(), gameObjectSerializer);
            input.close();
            return result;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
