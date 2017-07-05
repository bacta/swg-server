package bacta.io.connection.server;

import com.codahale.metrics.MetricRegistry;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


/**
 * Created by kyle on 6/29/2017.
 */

@Configuration
@Data
public class ConnectionServerConfiguration {

    private ApplicationContext applicationContext;
    private final MetricRegistry metricRegistry;
    private final ConnectionServerProperties connectionServerProperties;

    @Inject
    public ConnectionServerConfiguration(final MetricRegistry metricRegistry,
                                         final ConnectionServerProperties connectionServerProperties) {
        this.metricRegistry = metricRegistry;
        this.connectionServerProperties = connectionServerProperties;
    }


}
