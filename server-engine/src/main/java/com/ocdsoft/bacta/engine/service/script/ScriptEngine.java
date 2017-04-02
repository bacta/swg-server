package com.ocdsoft.bacta.engine.service.script;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by kyle on 4/18/2016.
 */

@Singleton
public class ScriptEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptEngine.class);

    private final GroovyScriptEngine groovyScriptEngine;

    @Inject
    public ScriptEngine(final BactaConfiguration configuration) throws IOException {

        Collection<String> scriptRoots = configuration.getStringCollection("Bacta/Script", "ScriptRoot");
        this.groovyScriptEngine = new GroovyScriptEngine(scriptRoots.toArray(new String[scriptRoots.size()]));
    }


    public void run(String scriptName, Binding binding) {}


}
