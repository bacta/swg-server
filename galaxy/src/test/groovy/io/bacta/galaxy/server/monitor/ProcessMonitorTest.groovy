package io.bacta.galaxy.server.monitor

import io.bacta.engine.util.AwaitUtil
import spock.lang.Specification

class ProcessMonitorTest extends Specification {

    LocalProcessMonitor procMon = new LocalProcessMonitor();

    def "Start"() {
        when:
        procMon.start("java", "-version");
        AwaitUtil.awaitFalse(procMon.&isRunning, 5)
        procMon.checkProcess()

        then:
        noExceptionThrown()
        procMon.getStartCount() > 1
    }
}
