package bacta.io.service.script;

import bacta.io.conf.BactaConfiguration;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by kyle on 4/18/2016.
 */

@Singleton
@Slf4j
public class ScriptEngine {

    private final GroovyScriptEngine groovyScriptEngine;

    @Inject
    public ScriptEngine(final BactaConfiguration configuration) throws IOException {

        Collection<String> scriptRoots = configuration.getStringCollection("Bacta/Script", "ScriptRoot");
        this.groovyScriptEngine = new GroovyScriptEngine(scriptRoots.toArray(new String[scriptRoots.size()]));
    }


    public void run(String scriptName, Binding binding) {}


}
