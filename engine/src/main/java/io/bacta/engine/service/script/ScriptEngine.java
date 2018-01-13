/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.engine.service.script;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import io.bacta.engine.conf.BactaConfiguration;
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
