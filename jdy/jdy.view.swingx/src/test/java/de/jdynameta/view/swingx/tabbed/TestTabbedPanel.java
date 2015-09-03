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

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTitledPanel;

import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.action.DefaultActionDescriptionWrapper;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.panel.OkPanel;

public class TestTabbedPanel 
{
		
	public static void main(String[] args) 
	{
		ResourceBundle commmonBundle = DefaultResourceLoader.loadBundle(MetainfoResourceConstants.DEFAULT_BUNDLE_NAME, Locale.GERMAN);
		DefaultResourceLoader ressourceLoader =  new DefaultResourceLoader(  new ApplicationIcons(), ApplicationIcons.class, commmonBundle);
		
		File propertyHome = new File(System.getProperty("user.home"), ".testview");
		
		SwingxTabbedPanelManager panelManager = new SwingxTabbedPanelManager(ressourceLoader, propertyHome, true, true);
		ArrayList<ActionDecorator> actionList = new ArrayList<ActionDecorator>();
		for (Action curAction : panelManager.createChangeLookAndFeelActions()) {
			actionList.add(new DefaultActionDescriptionWrapper(curAction, true, false,false, new String[] { "common.menu.Help" }));
		} 		
		panelManager.displayPanel(new PanelDecorator(new TestManagedPanel(panelManager, "Tab 1")), null);
		panelManager.displayPanel(new PanelDecorator(new TestManagedPanel(panelManager, "Tab 2")), null);
		panelManager.displayPanel(new PanelDecorator(null, new TestTitlePanel(), actionList), null);
	}
	
	
	public static class TestTitlePanel extends OkPanel 
	{
		
		public TestTitlePanel() 
		{
			super();
			this.setLayout(new BorderLayout());
			JXTitledPanel titledPnl = new JXTitledPanel("Title",new JPanel());
			titledPnl.setBorder(BorderFactory.createEmptyBorder());
			this.add(titledPnl);
			this.add(new JXTitledPanel("Second title",new JPanel()), BorderLayout.SOUTH);
			this.setTitle("test title");
		}
		
	}	
}
