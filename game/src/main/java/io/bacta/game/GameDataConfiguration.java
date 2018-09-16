package io.bacta.game;

import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.shared.container.ArrangementDescriptorList;
import io.bacta.shared.container.SlotDescriptorList;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.data.SetupSharedFile;
import io.bacta.shared.datatable.DataTableManager;
import io.bacta.shared.tre.TreeFile;
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
    public TreeFile getTreeFile(BactaConfiguration configuration) {
        LOGGER.info("Setting up tree file.");

        final TreeFile treeFile = new TreeFile();

        final SetupSharedFile setupSharedFile = new SetupSharedFile(configuration, treeFile);
        setupSharedFile.install();

        return treeFile;
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
