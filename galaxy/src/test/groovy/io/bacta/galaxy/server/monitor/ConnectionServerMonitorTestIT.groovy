package io.bacta.galaxy.server.monitor

import io.bacta.connection.server.ConnectionServerApplication
import io.bacta.engine.util.AwaitUtil
import io.bacta.engine.utils.FileSearchUtil
import io.bacta.galaxy.server.GalaxyServerTestApplication
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject
import java.nio.file.Paths

@SpringBootTest(classes = GalaxyServerTestApplication.class)
class ConnectionServerMonitorTestIT extends Specification {

    @Inject
    LocalProcessMonitor procMon

    def "Start "() {
        when:
        procMon.start(".", "java", "-version");
        AwaitUtil.awaitFalse(procMon.&isRunning, 5)
        procMon.checkProcess()

        then:
        noExceptionThrown()
        procMon.getStartCount() > 1
    }

    def "Start and Stop "() {
        when:

        File file = FileSearchUtil.getSingleMatch(Paths.get("../"), "connection-server-.*.jar", 3)
        File parent = file.getParentFile();

        procMon.start(".","java", "-cp", "lib" + File.separator + "*", ConnectionServerApplication.class.getCanonicalName());
        AwaitUtil.awaitFalse(procMon.&isRunning, 5)
        procMon.checkProcess()

        then:
        noExceptionThrown()
        procMon.getStartCount() > 1
    }
}
