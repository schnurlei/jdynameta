/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.metainfoview.metainfo.table;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
import de.jdynameta.view.JdyResourceLoader;

/**
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class TableExample extends JPanel {

	JdyClassInfoModel info;


	public TableExample(JdyResourceLoader aResourceLoader )
	{
		this.info =  createTestInfo();		
		this.setLayout(new BorderLayout())	;
		this.add(new JScrollPane(createTable(aResourceLoader)));
		
	} 


	public JTable createTable(JdyResourceLoader aResourceLoader )
	{
		
		ClassInfoColumnModel colModel = new ClassInfoColumnModel(this.info, null, null);

		ClassInfoTableModel aModel = new ClassInfoTableModel(this.info);
		aModel.setListModel(createTestData());		
		
		return new JTable( aModel,colModel);
	}

	private JdyClassInfoModel createTestInfo()
	{
		JdyClassInfoModel testClassInfo = new JdyClassInfoModel();
		testClassInfo.setExternalName("TestTableCreator");
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "first" , "first" ,true,true) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "second" , "second" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyBooleanType(), "boolean" , "boolean" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyFloatType(), "double" , "double" ,false,false) );

		return testClassInfo;
	}

	private ObjectListModel createTestData()
	{
		ArrayList<ValueObject> allData = new ArrayList<ValueObject>();	
		
		ChangeableValueObject model = new HashedValueObject();
		model.setValue(info.getAttributeInfoForExternalName("first"), "TestUpdate");
		model.setValue(info.getAttributeInfoForExternalName("second"), "Before update");
		model.setValue(info.getAttributeInfoForExternalName("boolean"), new Boolean(true));
		model.setValue(info.getAttributeInfoForExternalName("double"), new Double(123.45));
		allData.add(model);
		model = new HashedValueObject();
		model.setValue(info.getAttributeInfoForExternalName("first"), "2. Test");
		model.setValue(info.getAttributeInfoForExternalName("second"), "Weitere Info");		
		model.setValue(info.getAttributeInfoForExternalName("boolean"), new Boolean(false));
		model.setValue(info.getAttributeInfoForExternalName("double"), new Double(99.873));
		allData.add(model);

		return new DefaultObjectListModel(allData);
	}

	public static void main(String[] args) 
	{
		JdyResourceLoader resources = new JdyResourceLoader()
		{
			public String getString(String aKey)
			{
				return aKey;
			}

			public ImageIcon getIcon(String aFilename)
			{
				return null;
			}
			public String getMessage(String key, Object[] messageArguments)
			{
				return null;
			}			
		};
		JFrame mainFrame = new JFrame("TableExmaple");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().add(new TableExample(resources));
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	
}
