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
package de.jdynameta.groovy.analyse;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.groovy.control.CompilationFailedException;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyBooleanType;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyFloatType;
import de.jdynameta.base.metainfo.impl.JdyPrimitiveAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyTextType;
import de.jdynameta.base.objectlist.DefaultObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.groovy.ExecuteWorkflow;

public class AnalyseGroovy
{
    public void startShellTest()
    {
        // call groovy expressions from Java code
        Binding binding = new Binding();
        binding.setVariable("foo", 2);
        GroovyShell shell = new GroovyShell(binding);

        Object value = shell.evaluate("println 'Hello World!'; x = 123; return foo * 10");
        assert value.equals(20);
        assert binding.getVariable("x").equals(123);

    }

    public void startTest() throws InstantiationException, IllegalAccessException, CompilationFailedException, IOException
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        File groovyFile = new File("src/main/java/HelloWorld.groovy");
        Class groovyClass = loader.parseClass(groovyFile);

        // let's call some method on an instance
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        Object[] groovyArgs =
        {
        };
        groovyObject.invokeMethod("run", groovyArgs);

    }

    public void startTestInterface() throws InstantiationException, IllegalAccessException, CompilationFailedException, IOException
    {
        File groovyFile = new File("src/main/java/Tester.groovy");
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class groovyClass = gcl.parseClass(groovyFile);
        Object aScript = groovyClass.newInstance();

        TestInterface ifc = (TestInterface) aScript;
        ifc.printIt(createTestInfo());
    }

    public void startExecuteWorkflow() throws InstantiationException, IllegalAccessException, CompilationFailedException, IOException
    {
        File groovyFile = new File("src/main/java/de/jdynameta/groovy/WorkflowEvaluator.groovy");
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(new File("src/main/java/de/jdynameta/groovy/GroovyValueObject.groovy"));
        Class groovyClass = gcl.parseClass(groovyFile);
        Object aScript = groovyClass.newInstance();

        ClassInfo info = createTestInfo();

        ExecuteWorkflow workflowExecutor = (ExecuteWorkflow) aScript;
        ChangeableValueObject objectToUpdate = (ChangeableValueObject) createTestData(info).get(0);
        workflowExecutor.setValueModel(objectToUpdate, info);

//		workflowExecutor.evaluate("println 'test ' + 'classinfo ' + classInfo.getInternalName() + ' attr ' + TestTableCreator.class.getName() "); // +classInfo.getInternalName()");
        workflowExecutor.evaluate("println TestTableCreator.double2 = 100.00 "); // +classInfo.getInternalName()");

        System.out.println(objectToUpdate.getValue(info.getAttributeInfoForExternalName("double2")));

    }

    private JdyClassInfoModel createTestInfo()
    {
        JdyClassInfoModel testClassInfo = new JdyClassInfoModel();
        testClassInfo.setInternalName("TestTableCreator");
        testClassInfo.setExternalName("TestTableCreator");
        testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "first", "first", true, true));
        testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "second", "second", false, false));
        testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyBooleanType(), "boolean", "boolean", false, false));
        testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyFloatType(), "double1", "double1", false, false));
        testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyFloatType(), "double2", "double2", false, false));

        return testClassInfo;
    }

    private ObjectListModel createTestData(ClassInfo info)
    {
        ArrayList<ValueObject> allData = new ArrayList<>();

        ChangeableValueObject model = new HashedValueObject();
        model.setValue(info.getAttributeInfoForExternalName("first"), "TestUpdate");
        model.setValue(info.getAttributeInfoForExternalName("second"), "Before update");
        model.setValue(info.getAttributeInfoForExternalName("boolean"), true);
        model.setValue(info.getAttributeInfoForExternalName("double1"), 123.45);
        model.setValue(info.getAttributeInfoForExternalName("double2"), 200.34);
        allData.add(model);
        model = new HashedValueObject();
        model.setValue(info.getAttributeInfoForExternalName("first"), "2. Test");
        model.setValue(info.getAttributeInfoForExternalName("second"), "Weitere Info");
        model.setValue(info.getAttributeInfoForExternalName("boolean"), false);
        model.setValue(info.getAttributeInfoForExternalName("double1"), 100.22);
        model.setValue(info.getAttributeInfoForExternalName("double2"), 200.34);
        allData.add(model);

        return new DefaultObjectListModel(allData);
    }

    public static void main(String[] args)
    {
        try
        {
//			new TestGroovy().startShellTest();
//			new TestGroovy().startTest();
            new AnalyseGroovy().startExecuteWorkflow();
        } catch (InstantiationException | IllegalAccessException | CompilationFailedException | IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
