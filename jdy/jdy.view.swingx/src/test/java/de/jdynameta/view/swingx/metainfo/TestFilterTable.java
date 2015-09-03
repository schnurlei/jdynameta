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

import de.jdynameta.application.ApplicationManager;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.persistence.state.ApplicationModel;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.swingx.tabbed.SwingxTabbedPanelManager;
import de.jdynameta.view.swingx.tabbed.TabbedPanel;
import de.jdynameta.view.swingx.tabbed.TestManagedPanel;

public class TestFilterTable 
{
	public static void main(String[] args) 
	{
		ResourceBundle commmonBundle = DefaultResourceLoader.loadBundle(MetainfoResourceConstants.DEFAULT_BUNDLE_NAME, Locale.GERMAN);
		DefaultResourceLoader ressourceLoader =  new DefaultResourceLoader(  new ApplicationIcons(), ApplicationIcons.class, commmonBundle);
		
		File propertyHome = new File(System.getProperty("user.home"), ".testview");
		
		SwingxTabbedPanelManager panelManager = new SwingxTabbedPanelManager(ressourceLoader, propertyHome, true, true);
		
		TestDataCreator<ApplicationModel<GenericValueObjectImpl>> dataCreator = new TestDataCreator<ApplicationModel<GenericValueObjectImpl>>() 
		{
			@Override
			protected ApplicationModel<GenericValueObjectImpl> createEmptyResult(ClassInfo aClassInfo) 
			{
				return new ApplicationModel<GenericValueObjectImpl>(aClassInfo, new GenericValueObjectImpl(aClassInfo), false, null);
			}
		};
		DummyAppManager<ApplicationModel<GenericValueObjectImpl>> appMng = new DummyAppManager<ApplicationModel<GenericValueObjectImpl>>(dataCreator);
		
		TabbedPanel tabbedPanel = new TabbedPanel(panelManager);
		tabbedPanel.displayPanel(new PanelDecorator(createTablePanel( createMediumClassInfo(), panelManager, appMng)), null);
		tabbedPanel.displayPanel(new PanelDecorator(new TestManagedPanel(panelManager, "Tab 2")), null);
		
		PanelDecorator panelDecorator = new PanelDecorator(null, tabbedPanel, null);
		
		panelManager.displayPanel(panelDecorator, null);
	}

	public static SwingxFilterTablePanel<ApplicationModel<GenericValueObjectImpl>> createTablePanel( ClassInfo aClassInfo , SwingxTabbedPanelManager aPnlMngr, ApplicationManager<ApplicationModel<GenericValueObjectImpl>> anAppMngr)
	{
		try {
			SwingxFilterTablePanel<ApplicationModel<GenericValueObjectImpl>> editPnl = new SwingxFilterTablePanel<ApplicationModel<GenericValueObjectImpl>>(aClassInfo, anAppMngr, aPnlMngr, false);
			editPnl.setTitle(aClassInfo.getInternalName()+"Table");
			
			return editPnl;
		} catch (JdyPersistentException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ClassInfo createMediumClassInfo()
	{
		JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("SmallClassInfo");
		classInfo.addLongAttr("LongAttribute", -500, 500).setIsKey(true);
		classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
		classInfo.addDecimalAttr("DecimalAttribute", new BigDecimal(-100.00), new BigDecimal(100.00), 2).setNotNull(true);
		classInfo.addBooleanAttr("BooleanAttribute");
		classInfo.addFloatAttr("FloatAttribute" ).setNotNull(true);
		classInfo.addTimestampAttr("TimestampAttribute" ).setNotNull(true);
		classInfo.addVarCharAttr("VarcharAttribute",1000 ).setNotNull(true);
		
		
		return classInfo;
	}
	
}
