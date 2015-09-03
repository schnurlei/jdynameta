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
package de.jdynameta.view.swingx.tabbed;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.metainfoview.metainfo.RepositoryTreePnl;
import de.jdynameta.persistence.state.ApplicationModel;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.swingx.metainfo.DummyAppManager;
import de.jdynameta.view.swingx.metainfo.SwingxTable2EditPanelMediator;

public class TestRepositoryTreePanel
{
	Map<ClassInfo, SwingxTable2EditPanelMediator<ApplicationModel<GenericValueObjectImpl>>> classInfo2MediatorMap = new HashMap<ClassInfo, SwingxTable2EditPanelMediator<ApplicationModel<GenericValueObjectImpl>>>();
	
	public TestRepositoryTreePanel(final SwingxTabbedPanelManager panelManager) 
	{
		TestDataCreator<ApplicationModel<GenericValueObjectImpl>> dataCreator = new TestDataCreator<ApplicationModel<GenericValueObjectImpl>>() 
		{
			@Override
			protected ApplicationModel<GenericValueObjectImpl> createEmptyResult(ClassInfo aClassInfo) 
			{
				return new ApplicationModel<GenericValueObjectImpl>(aClassInfo, new GenericValueObjectImpl(aClassInfo), false, null);
			}
		};
		
		final DummyAppManager<ApplicationModel<GenericValueObjectImpl>> appMnager = new DummyAppManager<ApplicationModel<GenericValueObjectImpl>>(dataCreator);
		final RepositoryTreePnl treePnl = new RepositoryTreePnl(TestDataCreator.createRepository(), panelManager);
		treePnl.addActionToToolbar(treePnl.getEditObjectAction());

		treePnl.setEditHandler(new RepositoryTreePnl.EditObjectHandler<ClassInfo>() {
			
			@Override
			public void selectionChanged() 
			{
			}
			
			@Override
			public boolean isObjectEditable(ClassInfo anObject) 
			{
				return true;
			}
			
			@Override
			public void editObject(ClassInfo anObject)
			{
				
				SwingxTable2EditPanelMediator<ApplicationModel<GenericValueObjectImpl>> mediator
					= classInfo2MediatorMap.get(anObject);
				try {
					if( mediator == null) {
						mediator = new SwingxTable2EditPanelMediator<ApplicationModel<GenericValueObjectImpl>>(anObject,appMnager ,panelManager ,null);
						classInfo2MediatorMap.put(anObject, mediator);
					}
					mediator.showTablePanel(null);
				} catch (JdyPersistentException ex) {
					panelManager.displayErrorDialog(treePnl, ex.getLocalizedMessage(), ex);
				} 
				
			}
		});
		
		
		panelManager.setNavigationPanel(treePnl);
	}
	
	public static void main(String[] args) 
	{
//		UIManager.put("control", new Color(195, 220, 247));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
//		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//		    if ("Nimbus".equals(info.getName())) {
//		        try {
//					UIManager.setLookAndFeel(info.getClassName());
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//		        break;
//		    }
//		}
		
		
		
		ResourceBundle commmonBundle = DefaultResourceLoader.loadBundle(MetainfoResourceConstants.DEFAULT_BUNDLE_NAME, Locale.GERMAN);
		TestResourceLoader ressourceLoader =  new TestResourceLoader(  new ApplicationIcons(), ApplicationIcons.class, commmonBundle);
		File propertyHome = new File(System.getProperty("user.home"), ".testview");
		
		final SwingxTabbedPanelManager panelManager = new SwingxTabbedPanelManager(ressourceLoader, propertyHome, true, true);

		new TestRepositoryTreePanel(panelManager) ;
	}
	
}
