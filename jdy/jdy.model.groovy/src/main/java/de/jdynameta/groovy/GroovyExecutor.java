/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ChangeableValueObject;

public class GroovyExecutor
{
    private final Class groovyClass;

    public GroovyExecutor() throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException
    {
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(new GroovyCodeSource(GroovyExecutor.class.getResource("GroovyValueObject.groovy")));
        this.groovyClass = gcl.parseClass(new GroovyCodeSource(GroovyExecutor.class.getResource("WorkflowEvaluator.groovy")));
    }

    public void executeGroovyScript(ClassInfo aClassInfo, ChangeableValueObject anObjectToEdit, String groovyScript) throws InstantiationException, IllegalAccessException, CompilationFailedException, IOException
    {
        ExecuteWorkflow workflowExecutor = (ExecuteWorkflow) groovyClass.newInstance();
        workflowExecutor.setValueModel(anObjectToEdit, aClassInfo);
        workflowExecutor.evaluate(groovyScript);
    }

}
