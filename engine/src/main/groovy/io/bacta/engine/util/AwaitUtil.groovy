package io.bacta.engine.util

class AwaitUtil {

    static def awaitTrue(Closure<Boolean> booleanClosure, int timeoutSeconds) {
        def totalWaitTime = 0;
        while(!booleanClosure.call()) {
            Thread.sleep(100)
            totalWaitTime += 100;
            if(totalWaitTime > timeoutSeconds * 1000) {
                return false;
            }
        }
        return true
    }

    static def awaitFalse(Closure<Boolean> booleanClosure, int timeoutSeconds) {
        def totalWaitTime = 0;
        while(booleanClosure.call()) {
            Thread.sleep(100)
            totalWaitTime += 100;
            if(totalWaitTime > timeoutSeconds * 1000) {
                return false;
            }
        }
        return true
    }
}
