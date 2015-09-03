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

import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.base.DescriptivePanelDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelEnvironment;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;
import de.jdynameta.view.panel.OkPanel;

@SuppressWarnings("serial")
public class SwingxTabbedPanel extends ManagedPanel 
{
	private final JPanel navigationPanelHolder;
	private JTabbedPane panelHolderTab;
	private final HashMap<ManagedPanel,TabbedPanelHolder> panel2HolderMap;
	private PanelManager pnlMngr;
	private JSplitPane splitPane;
	private boolean showTabcloseButton;
	
	public SwingxTabbedPanel(PanelManager aPnlMngr, boolean isShowTabCloseBtn) 
	{
		super();
		this.pnlMngr = aPnlMngr;
		this.showTabcloseButton = isShowTabCloseBtn;
		this.navigationPanelHolder = new JPanel(new BorderLayout());
		this.panelHolderTab = new JTabbedPane();
		// add default tab
		this.panelHolderTab.addTab("", null);
		this.panel2HolderMap = new HashMap<ManagedPanel,TabbedPanelHolder>();
		initUi();
	}

	public void setNavigationPanel(JPanel aNavigationPanel)
	{
		this.navigationPanelHolder.removeAll();
		this.navigationPanelHolder.add(aNavigationPanel);
		this.splitPane.resetToPreferredSizes();
	}
	
	private void initUi()
	{
		this.setLayout(new BorderLayout());
		
		// show navigation in tab to ensure same height of navigation and holder
		JTabbedPane navigationTab = new JTabbedPane();
		navigationTab.addTab("", navigationPanelHolder);
		
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigationTab, panelHolderTab);
		this.splitPane.setDividerLocation(0.25);
		this.add(splitPane, BorderLayout.CENTER);
	}
	
	public void addManagedPanel(ManagedPanel aManagedPnl)
	{
		panelHolderTab.addTab(aManagedPnl.getTitle(), aManagedPnl);
	}
	
	public void closePanel(ManagedPanel aManagedPnl) throws CloseVetoException
	{
		if ( panel2HolderMap.containsKey(aManagedPnl)) {
			if( aManagedPnl instanceof OkPanel) {
				((OkPanel) aManagedPnl).close();
			}
	
			panelHolderTab.remove(panel2HolderMap.get(aManagedPnl));
			panel2HolderMap.remove(aManagedPnl);
			aManagedPnl.closed();
		}
		
		
	}

	public void displayPanel(DescriptivePanelDecorator aPanelDecorator, ManagedPanel parentPnl) 
	{
		if ( panel2HolderMap.containsKey(aPanelDecorator.getManagedPanel())) {
			panelHolderTab.setSelectedComponent(panel2HolderMap.get(aPanelDecorator.getManagedPanel()));
		} else {
			ImageIcon icon = aPanelDecorator.getPanelImage() == null ? null : new ImageIcon(aPanelDecorator.getPanelImage()) ;
			
			TabbedPanelHolder pnlHolder = new TabbedPanelHolder(aPanelDecorator.getManagedPanel(), pnlMngr.res()) ;
			if( aPanelDecorator.getActionColl() != null) {
				for (ActionDecorator curWrapper : aPanelDecorator.getActionColl())
				{
					pnlHolder.addAction(curWrapper);
				}
			}
			
			panelHolderTab.addTab(aPanelDecorator.getManagedPanel().getTitle(), icon , pnlHolder);
			if( this.showTabcloseButton) {
				panelHolderTab.setTabComponentAt(panelHolderTab.indexOfComponent(pnlHolder), new ButtonTabbedComponent(aPanelDecorator.getManagedPanel().getTitle(),createCloseTabListener()));
			}
			panelHolderTab.setSelectedComponent(pnlHolder);
			panel2HolderMap.put(aPanelDecorator.getManagedPanel(), pnlHolder);
			
			
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
					TabbedPanelHolder managedPnl = (TabbedPanelHolder) panelHolderTab.getComponentAt(idx);
					
					try {
						if( managedPnl.getWrappedPnl() instanceof OkPanel) {
							((OkPanel) managedPnl.getWrappedPnl()).close();
						}
						panelHolderTab.remove(managedPnl);
						TabbedPanelHolder removedTab = panel2HolderMap.remove(managedPnl.getWrappedPnl());
						managedPnl.getWrappedPnl().closed();
	
					} catch (CloseVetoException ex) {
						pnlMngr.displayErrorDialog(SwingxTabbedPanel.this, ex.getLocalizedMessage(), ex);
					}
				}
			}
		};
	}
	
	
	
	private static class TabbedPanelEnvironment implements PanelEnvironment
	{
		private SwingxTabbedPanel tabbedPanel;
		private ManagedPanel managedPanel;
		
		public TabbedPanelEnvironment(SwingxTabbedPanel aTabbedPanel, DescriptivePanelDecorator aPanelDecorator) 
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
