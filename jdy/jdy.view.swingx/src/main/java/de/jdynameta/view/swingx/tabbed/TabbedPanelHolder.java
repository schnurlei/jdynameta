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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.action.AdminActionBar;
import de.jdynameta.view.action.AdminMenuBar;
import de.jdynameta.view.action.AdminToolbar;
import de.jdynameta.view.base.ManagedPanel;

@SuppressWarnings("serial")
public class TabbedPanelHolder extends JPanel
	{
		private AdminToolbar toolbar;
		private AdminMenuBar menuBar;
		private AdminActionBar actionBar;
		private ManagedPanel wrappedPnl;
		
		public TabbedPanelHolder(ManagedPanel aWrappedPnl, JdyResourceLoader aResourceLoader)
		{
			super(new BorderLayout());
			
			this.wrappedPnl = aWrappedPnl;
			this.add(aWrappedPnl, BorderLayout.CENTER);
			this.toolbar = new AdminToolbar();
			this.actionBar = new AdminActionBar();
			this.menuBar = new AdminMenuBar(aResourceLoader);
			this.toolbar.addToFrame(this, BorderLayout.PAGE_START);
			this.actionBar.addToPanel(this, BorderLayout.PAGE_END);
//			this.menuBar.addToFrame(aFrameToAdd) 
		}
		
		public ManagedPanel getWrappedPnl()
		{
			return wrappedPnl;
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
		
	}