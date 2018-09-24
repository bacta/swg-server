package io.bacta.game;

import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.game.data.TreeFileService;
import io.bacta.swg.container.ArrangementDescriptorList;
import io.bacta.swg.container.SlotDescriptorList;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.datatable.DataTableManager;
import io.bacta.swg.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
@ConfigurationProperties
@Slf4j
public class GameDataConfiguration {
    private final GameServerProperties gameServerProperties;

    @Inject
    public GameDataConfiguration(GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
    }

    @Bean
    public TreeFile getTreeFile(TreeFileService treeFileService) {
        //TODO: Maybe we shouldn't expose the tree file like this, but rather just pass the service around.
        return treeFileService.getTreeFile();
    }

    @Bean
    public DataTableManager getDataTableManager(TreeFile treeFile) {
        return new DataTableManager(treeFile);
    }

    @Bean
    public SlotIdManager getSlotIdManager(TreeFile treeFile, BactaConfiguration bactaConfiguration) {
        return new SlotIdManager(treeFile, bactaConfiguration);
    }

    @Bean
    public ArrangementDescriptorList getArrangementDescriptorList(TreeFile treeFile, SlotIdManager slotIdManager) {
        return new ArrangementDescriptorList(treeFile, slotIdManager);
    }

    @Bean
    public SlotDescriptorList getSlotDescriptorList(TreeFile treeFile, SlotIdManager slotIdManager) {
        return new SlotDescriptorList(treeFile, slotIdManager);
    }

}
