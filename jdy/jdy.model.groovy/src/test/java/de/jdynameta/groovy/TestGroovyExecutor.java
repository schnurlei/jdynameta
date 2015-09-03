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
import de.jdynameta.base.value.defaultimpl.HashedValueObject;

public class TestGroovyExecutor
{
    public void startExecuteWorkflow() throws InstantiationException, IllegalAccessException, CompilationFailedException, IOException
    {
        GroovyExecutor executor = new GroovyExecutor();

        ClassInfo info = createTestInfo();

        ChangeableValueObject objectToUpdate = createTestData(info).get(0);
        executor.executeGroovyScript(info, objectToUpdate, "delegate.first = second + double1 ");
        executor.executeGroovyScript(info, objectToUpdate, "setfirst( second + double1) ");

        System.out.println("In Java " + objectToUpdate.getValue(info.getAttributeInfoForExternalName("first")));

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

    private ObjectListModel<ChangeableValueObject> createTestData(ClassInfo info)
    {
        ArrayList<ChangeableValueObject> allData = new ArrayList<>();

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

        return new DefaultObjectListModel<>(allData);
    }

    public static void main(String[] args)
    {
        try
        {
            new TestGroovyExecutor().startExecuteWorkflow();
        } catch (CompilationFailedException | InstantiationException | IllegalAccessException | IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

}
