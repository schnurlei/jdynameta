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
package de.jdynameta.view.action;

import javax.swing.Action;

/**
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
public class DefaultActionDescriptionWrapper implements ActionDecorator
{
	private boolean showInMenubar;
	private boolean showInToolbar;
	private boolean showInActionBar;
	private String[] menuBarPath;
	private Action wrappedAction;
	private boolean isSeperator;
	
	/**
	 * @param aShowInMenubar
	 * @param aShowInToolbar
	 * @param aShowInActionBar
	 * @param aMenuBarPath
	 * @param aWrappedAction
	 */
	public DefaultActionDescriptionWrapper( Action aWrappedAction, boolean aShowInMenubar,
			boolean aShowInToolbar, boolean aShowInActionBar,
			String[] aMenuBarPath)
	{
		super();
		this.showInMenubar = aShowInMenubar;
		this.showInToolbar = aShowInToolbar;
		this.showInActionBar = aShowInActionBar;
		this.menuBarPath = aMenuBarPath;
		this.wrappedAction = aWrappedAction;
		this.isSeperator = false;
	}

	private DefaultActionDescriptionWrapper(  boolean aShowInMenubar,
			boolean aShowInToolbar, boolean aShowInActionBar,
			String[] aMenuBarPath)
	{
		super();
		this.showInMenubar = aShowInMenubar;
		this.showInToolbar = aShowInToolbar;
		this.showInActionBar = aShowInActionBar;
		this.menuBarPath = aMenuBarPath;
		this.wrappedAction = null;
		this.isSeperator = true;
	}
	
	
	public String[] getMenuBarPath()
	{
		return this.menuBarPath;
	}
	public boolean isShowInActionBar()
	{
		return this.showInActionBar;
	}
	public boolean isShowInMenubar()
	{
		return this.showInMenubar;
	}
	public boolean isShowInToolbar()
	{
		return this.showInToolbar;
	}
	public Action getWrappedAction()
	{
		return this.wrappedAction;
	}
	
	@Override
	public boolean isSeperator()
	{
		return isSeperator;
	}
	
	@Override
	public String toString()
	{
		return "DefaultActionDescriptionWrapper " + (wrappedAction != null ? wrappedAction.getValue(Action.NAME) :"" );
	}
	
	/**
	 * Creates a separator wrapper
	 * @param aShowInMenubar
	 * @param aShowInToolbar
	 * @param aShowInActionBar
	 * @param aMenuBarPath
	 * @return
	 */
	public static DefaultActionDescriptionWrapper separator(  boolean aShowInMenubar, boolean aShowInToolbar, boolean aShowInActionBar,String[] aMenuBarPath)
	{
		return new DefaultActionDescriptionWrapper(   aShowInMenubar, aShowInToolbar,  aShowInActionBar,aMenuBarPath);
	}
}
