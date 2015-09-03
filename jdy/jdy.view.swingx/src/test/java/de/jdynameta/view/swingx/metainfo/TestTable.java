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
import java.util.Locale;
import java.util.ResourceBundle;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.persistence.state.ApplicationModel;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.swingx.tabbed.SwingxTabbedPanelManager;
import de.jdynameta.view.swingx.tabbed.TestManagedPanel;

public class TestTable 
{
	public static void main(String[] args) 
	{
		ResourceBundle commmonBundle = DefaultResourceLoader.loadBundle(MetainfoResourceConstants.DEFAULT_BUNDLE_NAME, Locale.GERMAN);
		DefaultResourceLoader ressourceLoader =  new DefaultResourceLoader(  new ApplicationIcons(), ApplicationIcons.class, commmonBundle);
		
		File propertyHome = new File(System.getProperty("user.home"), ".testview");
		
		SwingxTabbedPanelManager panelManager = new SwingxTabbedPanelManager(ressourceLoader, propertyHome, true, true);
		
		DummyAppManager<ApplicationModel> appMng = new DummyAppManager<ApplicationModel>(null);
		
		JdyClassInfoModel testClassInfo = TestDataCreator.createMediumClassInfo();
		
		panelManager.displayPanel(new PanelDecorator(createTablePanel( testClassInfo, panelManager, appMng)), null);
	}

	public static SwingxTablePanel<ApplicationModel> createTablePanel( ClassInfo aClassInfo , SwingxTabbedPanelManager aPnlMngr, ApplicationManager<ApplicationModel> anAppMngr)
	{
		try {
			
			TestDataCreator<ApplicationModel> dataCreator = new TestDataCreator<ApplicationModel>() 
			{
				@Override
				protected ApplicationModel createEmptyResult(ClassInfo aClassInfo) 
				{
					return new ApplicationModel(aClassInfo, new GenericValueObjectImpl(aClassInfo), false, null);
				}
			};
			
			ObjectListModel<ApplicationModel> testData = dataCreator.createTestData(aClassInfo, 1000, null);
			
			SwingxTablePanel<ApplicationModel> editPnl = new SwingxTablePanel<ApplicationModel>(aClassInfo,  aPnlMngr, anAppMngr, testData);
			editPnl.setTitle(aClassInfo.getInternalName()+"Table");
			
			return editPnl;
		} catch (JdyPersistentException e) {
			e.printStackTrace();
			return null;
		}
	}
}
