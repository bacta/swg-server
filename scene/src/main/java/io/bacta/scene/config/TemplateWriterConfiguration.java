package io.bacta.scene.config;

import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import io.bacta.soe.util.NullGameNetworkMessageTemplateWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TemplateWriterConfiguration {

    @Bean
    public GameNetworkMessageTemplateWriter getNullWriter() {
        return new NullGameNetworkMessageTemplateWriter();
    }
}
