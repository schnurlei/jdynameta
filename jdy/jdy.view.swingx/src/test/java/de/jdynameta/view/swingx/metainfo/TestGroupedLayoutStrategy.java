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
package de.jdynameta.view.swingx.metainfo;

import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.ResourceBundle;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.metainfoview.metainfo.ComponentLayoutStrategy;
import de.jdynameta.metainfoview.metainfo.MultiColumnLayoutStrategy;
import de.jdynameta.metainfoview.metainfo.ValueModelEditPanel;
import de.jdynameta.persistence.state.ApplicationModel;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.swingx.tabbed.SwingxTabbedPanelManager;
import de.jdynameta.view.swingx.tabbed.TabbedPanel;
import de.jdynameta.view.swingx.tabbed.TestManagedPanel;

public class TestGroupedLayoutStrategy 
{
		
	public static void main(String[] args) 
	{
		ResourceBundle commmonBundle = DefaultResourceLoader.loadBundle(MetainfoResourceConstants.DEFAULT_BUNDLE_NAME, Locale.GERMAN);
		DefaultResourceLoader ressourceLoader =  new DefaultResourceLoader(  new ApplicationIcons(), ApplicationIcons.class, commmonBundle);
		
		File propertyHome = new File(System.getProperty("user.home"), ".testview");
		
		SwingxTabbedPanelManager panelManager = new SwingxTabbedPanelManager(ressourceLoader, propertyHome, true, true);
		
		TabbedPanel tabbedPanel = new TabbedPanel(panelManager);
		tabbedPanel.displayPanel(new PanelDecorator(createEditPanel( createSmallClassInfo(), panelManager)), null);
		tabbedPanel.displayPanel(new PanelDecorator(createEditPanel( createMediumClassInfo(), panelManager)), null);
		tabbedPanel.displayPanel(new PanelDecorator(createEditPanel( createLongClassInfo(), panelManager)), null);
		tabbedPanel.displayPanel(new PanelDecorator(new TestManagedPanel(panelManager, "Tab 2")), null);
		
		PanelDecorator panelDecorator = new PanelDecorator(null, tabbedPanel, null);
		
		panelManager.displayPanel(panelDecorator, null);
	}
	
	public static ValueModelEditPanel createEditPanel( ClassInfo aClassInfo , SwingxTabbedPanelManager aPnlMngr)
	{
//		ComponentLayoutStrategy layoutStrategy = new GroupedLayoutStrategy(aPnlMngr.getResourceLoader());
//		ComponentLayoutStrategy layoutStrategy = new MultiColumnLayoutStrategy(aPnlMngr.getResourceLoader());
		ComponentLayoutStrategy layoutStrategy = new ComponentLayoutStrategy(aPnlMngr.getResourceLoader());

		try {
			ValueModelEditPanel editPnl = new ValueModelEditPanel(aClassInfo, aPnlMngr,null);
			editPnl.setLayouStrategy(layoutStrategy);
			editPnl.setCreateComponentStrategy(new SwingxCreationStrategy(aPnlMngr, null));
			editPnl.setTitle(aClassInfo.getInternalName());
			
			ApplicationModel model = new ApplicationModel(aClassInfo,new GenericValueObjectImpl(aClassInfo), true, null);
			editPnl.setObjectToEdit(model);
			
			return editPnl;
		} catch (JdyPersistentException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static ClassInfo createSmallClassInfo()
	{
		JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("SmallClassInfo");
		classInfo.addLongAttr("LongAttribute", -500, 500).setIsKey(true);
		classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
		classInfo.addBooleanAttr("BooleanAttribute");
		
		
		return classInfo;
	}

	public static ClassInfo createMediumClassInfo()
	{
		JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("MediumClassInfo");
		classInfo.addLongAttr("LongAttribute",-500, 500).setIsKey(true);
		classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
		classInfo.addDecimalAttr("DecimalAttribute", new BigDecimal(-100.00), new BigDecimal(100.00), 2).setNotNull(true);
		classInfo.addBooleanAttr("BooleanAttribute");
		classInfo.addFloatAttr("FloatAttribute" ).setNotNull(true);
		classInfo.addTimestampAttr("TimestampAttribute" ).setNotNull(true);
		classInfo.addVarCharAttr("VarcharAttribute",1000 ).setNotNull(true);
		
		
		return classInfo;
	}
	
	public static ClassInfo createLongClassInfo()
	{
		JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("LargeClassInfo");
		classInfo.addLongAttr("LongAttribute",-500, 500).setIsKey(true);
		classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
		classInfo.addDecimalAttr("DecimalAttribute",new BigDecimal(-100.00), new BigDecimal(100.00), 2).setNotNull(true);
		classInfo.addBooleanAttr("BooleanAttribute");
		classInfo.addFloatAttr("FloatAttribute" ).setNotNull(true);
		classInfo.addTimestampAttr("TimestampAttribute" ).setNotNull(true);
		classInfo.addVarCharAttr("VarcharAttribute",1000 ).setNotNull(true);
		classInfo.addLongAttr("LongAttribute3", -500, 500).setIsKey(false);
		classInfo.addLongAttr("LongAttribute4", -500, 500).setIsKey(false);
		classInfo.addLongAttr("LongAttribute5", -500, 500).setIsKey(false);
		classInfo.addLongAttr("LongAttribute6", -500, 500).setIsKey(false);
		
		
		return classInfo;
	}
	
}
