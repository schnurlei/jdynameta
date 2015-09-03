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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import de.jdynameta.view.base.DescriptivePanelDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelEnvironment;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;
import de.jdynameta.view.panel.OkPanel;
import de.jdynameta.view.panel.SavePanel;
import de.jdynameta.view.sdi.AbstractSdiFrame;

@SuppressWarnings("serial")
public class TabbedPanel extends ManagedPanel 
{
	private final JPanel navigationPanelHolder;
	private JTabbedPane panelHolderTab;
	private final HashMap<ManagedPanel,AbstractSdiFrame> panel2FrameMap;
	private PanelManager pnlMngr;
	
	public TabbedPanel(PanelManager aPnlMngr) 
	{
		super();
		this.pnlMngr = aPnlMngr;
		this.navigationPanelHolder = new JPanel(new BorderLayout());
		this.panelHolderTab = new JTabbedPane();
		// add default tab
		this.panelHolderTab.addTab("", null);
		this.panel2FrameMap = new HashMap<ManagedPanel,AbstractSdiFrame>();
		initUi();
	}

	public void setNavigationPanel(JPanel aNavigationPanel)
	{
		this.navigationPanelHolder.removeAll();
		this.navigationPanelHolder.add(aNavigationPanel);
	}
	
	private void initUi()
	{
		this.setLayout(new BorderLayout());
		
		// show navigation in tab to ensure same height of navigation and holder
		JTabbedPane navigationTab = new JTabbedPane();
		navigationTab.addTab("", navigationPanelHolder);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigationTab, panelHolderTab);
		splitPane.setDividerLocation(0.25);
		this.add(splitPane, BorderLayout.CENTER);
	}
	
	public void addManagedPanel(ManagedPanel aManagedPnl)
	{
		panelHolderTab.addTab(aManagedPnl.getTitle(), aManagedPnl);
	}
	

	public void displayPanel(DescriptivePanelDecorator aPanelDecorator, ManagedPanel parentPnl) 
	{
		if ( panel2FrameMap.containsKey(aPanelDecorator.getManagedPanel())) {
			panel2FrameMap.get(aPanelDecorator.getManagedPanel());
			panelHolderTab.setSelectedComponent(aPanelDecorator.getManagedPanel());
		} else {
			ImageIcon icon = aPanelDecorator.getPanelImage() == null ? null : new ImageIcon(aPanelDecorator.getPanelImage()) ;
			panelHolderTab.addTab(aPanelDecorator.getManagedPanel().getTitle(), icon , aPanelDecorator.getManagedPanel());
			panelHolderTab.setTabComponentAt(panelHolderTab.indexOfComponent(aPanelDecorator.getManagedPanel()), new ButtonTabbedComponent(aPanelDecorator.getManagedPanel().getTitle(),createCloseTabListener()));
			panelHolderTab.setSelectedComponent(aPanelDecorator.getManagedPanel());

			
////			frameToDisplay = createFrameForPanel(aPanelDecorator);
////			addDependentFrames(parentPnl, frameToDisplay);
////			addActionsToFrame(aPanelDecorator, frameToDisplay);
////			setFrameIcon(aPanelDecorator, frameToDisplay);
//			
			TabbedPanelEnvironment environment = new TabbedPanelEnvironment(this, aPanelDecorator );
			aPanelDecorator.getManagedPanel().setEnvironment(environment);
//			environment.panelTitleChanged();
//			
//			displayPanelInFrame(aPanelDecorator, frameToDisplay);
//
//			this.panel2FrameMap.put(aPanelDecorator.getManagedPanel(), frameToDisplay);
//
//			WindowAdapter windowListenr = createFrameListener(aPanelDecorator.getManagedPanel());
//			frameToDisplay.addWindowListener(windowListenr);
//			frameToDisplay.addWindowStateListener(windowListenr);
//			frameToDisplay.addComponentListener(createFrameComponentListener(aPanelDecorator.getManagedPanel()));
		}
//		
//		if( wasInvisible) {
//			aPanelDecorator.getManagedPanel().makeVisible();
//		}
//		frameToDisplay.setVisible(true);
	}
	
	private ActionListener createCloseTabListener() 
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev) 
			{
				Object source = ev.getSource();
				
				if ( source instanceof ButtonTabbedComponent.TabButton ) {
					
					ButtonTabbedComponent tabComp = ((ButtonTabbedComponent.TabButton) source).getTabComponent();
					int idx = panelHolderTab.indexOfTabComponent(tabComp);
					ManagedPanel managedPnl = (ManagedPanel) panelHolderTab.getComponentAt(idx);
					
					try {
						if( managedPnl instanceof OkPanel) {
							((OkPanel) managedPnl).close();
						}
						panelHolderTab.remove(managedPnl);
						managedPnl.closed();
					} catch (CloseVetoException ex) {
						pnlMngr.displayErrorDialog(TabbedPanel.this, ex.getLocalizedMessage(), ex);
					}
				}
			}
		};
	}
	
	
	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(SavePanel aPanel) 
	{
		
//		if ( savePanel != null) {
//			savePanel.setOkPanelListener(null);
//		}
//		
//		if( aPanel != null) {
//			savePanel = aPanel;
//		} else {
//			savePanel = new DefaultSavePanel();
//		}	
//
//		savePanel.setOkPanelListener(savePanelListener);
//		okAction.setEnabled(true); //savePanel.canClose());
//
//		super.displayPanel(aPanel);
	}
	
	
	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(OkPanel aPanel) 
	{

//		if ( okPanel != null) {
//			okPanel.setOkPanelListener(null);
//		}
//		
//		if( aPanel != null) {
//			okPanel = aPanel;
//		} else {
//			okPanel = new DefaultOkPanel();
//		}	
//
//		okPanel.setOkPanelListener(okPanelListener);
//		closeAction.setEnabled(okPanel.canClose());
//			
//		this.okPanel = aPanel;
//		super.displayPanel(aPanel);
	}
	
	private static class TabbedPanelEnvironment implements PanelEnvironment
	{
		private TabbedPanel tabbedPanel;
		private ManagedPanel managedPanel;
		
		public TabbedPanelEnvironment(TabbedPanel aTabbedPanel, DescriptivePanelDecorator aPanelDecorator) 
		{
			this.tabbedPanel = aTabbedPanel;
			this.managedPanel = aPanelDecorator.getManagedPanel();
		}

		@Override
		public void displayPanelAtFront() 
		{
			tabbedPanel.panelHolderTab.setSelectedComponent(managedPanel);
		}
		
		@Override
		public void panelTitleChanged() 
		{
			int tabIdx = tabbedPanel.panelHolderTab.indexOfComponent(managedPanel);
			if( tabIdx >= 0) {
				tabbedPanel.panelHolderTab.setTitleAt(tabIdx, managedPanel.getTitle());

				Component comp = tabbedPanel.panelHolderTab.getTabComponentAt(tabIdx);
				if( comp instanceof ButtonTabbedComponent) {
					((ButtonTabbedComponent)comp).setTitle(managedPanel.getTitle());
				}
			}
			
			
		}		
		
		@Override
		public void panelWantsToClose() throws CloseVetoException 
		{
			tabbedPanel.panelHolderTab.remove(managedPanel);
		}
		
		@Override
		public void showStatusMessage(String aMessage) 
		{
			tabbedPanel.showStatusMessage(aMessage);
			
		}		
	}
}
