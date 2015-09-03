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
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.action.AdminActionBar;
import de.jdynameta.view.action.AdminMenuBar;
import de.jdynameta.view.action.AdminToolbar;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;
import de.jdynameta.view.util.DefaultStatusLine;


/**
 * Frame which handles a <@link de.jdynameta.view.base.ManagedPanel />
 * With Menubar, Toolbar and Statusline
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
@SuppressWarnings("serial")
public class TabbedFrame extends JFrame
{
	private final JPanel mainPnl;
	private AdminToolbar toolbar;
	private AdminMenuBar menuBar;
	private AdminActionBar actionBar;
	private DefaultStatusLine statusLine;
	private final JPanel pageEndPnl;
	private final PanelManager panelManager;
	private final ArrayList<TabbedFrame> dependentFrameColl;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public TabbedFrame(PanelManager aPanelManager) throws HeadlessException
	{
		super();
		this.panelManager = aPanelManager;
		this.dependentFrameColl = new ArrayList<TabbedFrame>();
		this.pageEndPnl = new JPanel(new BorderLayout());
		this.mainPnl = createMainPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.mainPnl);
		this.setMenubar(new AdminMenuBar(aPanelManager.res()));
		this.setToolbar(new AdminToolbar());
		this.setActionbar(new AdminActionBar());
		this.setStatusline(new DefaultStatusLine());
		
		// handle window close by the Window Listener
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
	   	this.addWindowListener ( new WindowAdapter ()
    	{
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					closeAllFrames();
				} catch (CloseVetoException ex) {
					// ignore, because the thrower of the exception is responsible for the user nofitication
				}
			}
		});
		
	}

	/** 
	 * Add a Dependent Frame which should be closed when this Frame is closed
	 * @param aChildFrame
	 */
	public void addDependentFrame(TabbedFrame aChildFrame)
	{
		dependentFrameColl.add(aChildFrame);
	}
	
	protected JdyResourceLoader getResourceLoader()
	{
		return panelManager.res();
	}
	
	public PanelManager getPanelManager() 
	{
		return panelManager;
	}
	
	public void setToolbar(AdminToolbar aToolbar)
	{
		if( toolbar != null) {
			this.toolbar.removeFromFrame(this.mainPnl);
		}
		this.toolbar = aToolbar;
		this.toolbar.addToFrame(this.mainPnl, BorderLayout.NORTH);
	}

	public void setActionbar(AdminActionBar aActionbar)
	{
		if( actionBar != null) {
			this.actionBar.removeFromPanel(this.pageEndPnl);
		}
		this.actionBar = aActionbar;
		this.actionBar.addToPanel(this.pageEndPnl, BorderLayout.CENTER);
	}
	
	
	public void setMenubar(AdminMenuBar aMenubar)
	{
		this.menuBar = aMenubar;
		this.menuBar.addToFrame(this);
	}

	public void setStatusline(DefaultStatusLine aStatusline)
	{
		if( statusLine != null) {
			pageEndPnl.remove(statusLine);
		}
		this.statusLine = aStatusline; 
		pageEndPnl.add( statusLine, BorderLayout.PAGE_END);
	}
	
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(pageEndPnl, BorderLayout.PAGE_END);
		return mainPanel;
	}
	
	protected void addActionsToActionBar(Action aNewAction, boolean isSeperator)
	{
		this.actionBar.addActionsToActionBar(aNewAction, isSeperator);
	}

	protected void addActionsToActionBarRight(Action aNewAction, boolean setAsDefault)
	{
		JButton newBtn = this.actionBar.addActionsToActionBarRight(aNewAction);
		if( setAsDefault ) {
			this.getRootPane().setDefaultButton(newBtn);
		}
	}
	
	public void addActionToMenuBar(Action aAction, String[] aMenuPath, boolean isSeperator)
	{
		this.menuBar.addAction(aAction, aMenuPath, isSeperator);
	}

	protected void addActionToToolbar(Action aAction, boolean isSeperator)
	{
		this.toolbar.addAction(aAction, isSeperator);
	}
	
	

	public void showStatusMessage(String aMessage)
	{
		this.statusLine.showMessage(aMessage);
	}
	
	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(ManagedPanel aPanel) 
	{
		this.mainPnl.add(aPanel, BorderLayout.CENTER);
	}
	
	public void addCloseAction()
	{
		
	}
	
	public void addAction(ActionDecorator aActionWrapper)
	{
		if(aActionWrapper.isShowInToolbar()) {
			addActionToToolbar(aActionWrapper.getWrappedAction(), aActionWrapper.isSeperator());
		}

		if(aActionWrapper.isShowInMenubar()) {
			addActionToMenuBar(aActionWrapper.getWrappedAction(), aActionWrapper.getMenuBarPath(), aActionWrapper.isSeperator());
		}
		
		if(aActionWrapper.isShowInActionBar()) {
			addActionsToActionBar(aActionWrapper.getWrappedAction(), aActionWrapper.isSeperator());
		}
	}
	
	/**
	 * Closes this Frame and all dependent Frames by calling their closeFrame() Method
	 * @throws CloseVetoException throws this exception, when the frame could not be closed, sender is responsiple for
	 * 				a proper user notification
	 */
	public void closeAllFrames() throws CloseVetoException
	{
		for (TabbedFrame curFrame : dependentFrameColl) {
			if( curFrame.isVisible()) {
				curFrame.closeAllFrames();
			}
		}
		closeFrame();
	}
	
	
	/**
	 * Closes and dispose this Frame 
	 * @throws CloseVetoException throws this exception, when the frame could not be closed, sender is responsiple for
	 * 				a proper user notification
	 */
	protected void closeFrame() throws CloseVetoException
	{
		this.dispose();
	}
}
