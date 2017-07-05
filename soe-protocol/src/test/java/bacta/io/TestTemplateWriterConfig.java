package bacta.io;

import bacta.io.soe.util.TemplateWriterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by kyle on 6/9/2017.
 */
@Configuration
public class TestTemplateWriterConfig implements TemplateWriterConfig {

    @Override
    @Scheduled
    public String getBasePackage() {
        return "com.ocdsoft.bacta.soe.test";
    }

}
