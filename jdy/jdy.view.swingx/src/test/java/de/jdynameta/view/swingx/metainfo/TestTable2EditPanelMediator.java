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
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.persistence.state.ApplicationModel;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.swingx.tabbed.SwingxTabbedPanelManager;

public class TestTable2EditPanelMediator
{

	
	public static void main(String[] args) 
	{
		JdyClassInfoModel testClassInfo = TestDataCreator.createClassWithDeepReference();
		
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
		
		DummyAppManager<ApplicationModel<GenericValueObjectImpl>> appMnager = new DummyAppManager<ApplicationModel<GenericValueObjectImpl>>(dataCreator);
		
		try {
			SwingxTable2EditPanelMediator<ApplicationModel<GenericValueObjectImpl>> mediator = new SwingxTable2EditPanelMediator<ApplicationModel<GenericValueObjectImpl>>(testClassInfo,appMnager ,panelManager ,null); 
			mediator.showTablePanel(null);
		} catch (ProxyResolveException e) {
			e.printStackTrace();
		} catch (JdyPersistentException e) {
			e.printStackTrace();
		}
	}
	
}
