package io.bacta.game.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "io.bacta.game.db")
public class GameDatabaseProperties {
    private String connectorClassPath;
    //Temporary for now. This will be a specific to the connector setting later.
    private String databaseEnvironment;
}
