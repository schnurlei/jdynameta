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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.MetainfoResourceConstants;
import de.jdynameta.view.DefaultResourceLoader;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;

@SuppressWarnings("serial")
public class TestManagedPanel extends ManagedPanel 
{
	private PanelManager pnlMngr;
	
	public TestManagedPanel(PanelManager aPanelManager, String title) 
	{
		super();
		this.pnlMngr = aPanelManager;
		this.setLayout(new GridLayout(10,1));

		this.add(new JButton(createCloseRequestAction()));
		this.add(new JButton(createDisplayAtFrontAction()));
		this.add(new JButton(createSetTitleAction()));
		this.add(new JButton(createShowStatusAction()));
		this.add(new JButton(createShowErrorAction()));
		this.setTitle(title);
	}
	
	private Action createCloseRequestAction()
	{
		return new AbstractAction("Request for close") 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try {
					TestManagedPanel.this.requestForClose("Test request for cloase");
				} catch (CloseVetoException e1) {
				}
			}
		};
	}

	private Action createDisplayAtFrontAction()
	{
		return new AbstractAction("Display at Front") 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				TestManagedPanel.this.displayPanelAtFront("Test display at front ");
			}
		};
	}

	private Action createSetTitleAction()
	{
		return new AbstractAction("Set title") 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				TestManagedPanel.this.setTitle("Title "+ + System.currentTimeMillis());
			}
		};
	}

	private Action createShowErrorAction()
	{
		return new AbstractAction("Show Error") 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				pnlMngr.displayErrorDialog(TestManagedPanel.this, "Test erorr", new Exception());
			}
		};
	}
	
	
	private Action createShowStatusAction()
	{
		return new AbstractAction("Shwo Status") 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				TestManagedPanel.this.showStatusMessage("Status Message " + System.currentTimeMillis());
			}
		};
	}
	
	public static void main(String[] args) 
	{
		ResourceBundle commmonBundle = DefaultResourceLoader.loadBundle(MetainfoResourceConstants.DEFAULT_BUNDLE_NAME, Locale.GERMAN);
		DefaultResourceLoader ressourceLoader =  new DefaultResourceLoader(  new ApplicationIcons(), ApplicationIcons.class, commmonBundle);
		
		File propertyHome = new File(System.getProperty("user.home"), ".testview");
		
		SwingxTabbedPanelManager panelManager = new SwingxTabbedPanelManager(ressourceLoader, propertyHome, true, true);
		
		PanelDecorator panelDecorator = new PanelDecorator(null, new TestManagedPanel(panelManager, "Managed Panel"), null);
		
		panelManager.displayPanel(panelDecorator, null);
	}
}
