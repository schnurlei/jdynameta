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


import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import de.jdynameta.view.JdyResourceLoader;


/**
 * @author rs
 */
@SuppressWarnings("serial")
public class AdminMenuBar
{
	private JMenuBar menubar;
	private Map<String,JMenu> menuMap;
	protected final JdyResourceLoader resourceLoader;
	
	
	public AdminMenuBar(JdyResourceLoader aResourceLoader) 
	{
		resourceLoader = aResourceLoader;
		createMenuBar();
	}
	
	
	private void createMenuBar() 
	{
		menubar = new JMenuBar() 			
		{
			@Override
			public boolean isFocusable() 
			{
				return false;
			}

		};

		menuMap = new HashMap<String,JMenu>();
	}

	public JMenuBar getMenuBar(){
		return menubar;
	}

	/**
	 * Add action to the Menubar 
	 * @param actionToAdd
	 * @param menuNameRessourcePath
	 * @param isToggleBtn 
	 * @TODO implemtation for unlimited action-depth
	 */
	public void addAction(Action actionToAdd, String[] menuNameRessourcePath, boolean isSeperator) 
	{
		
		JMenuItem itemForAction = null;
		if( actionToAdd != null && !isSeperator) {
			boolean isToggleBtn = actionToAdd.getValue(Action.SELECTED_KEY) != null;
			itemForAction =  createMenuItem(actionToAdd, isToggleBtn);
		}
		
		JMenu barMenu = menuMap.get(menuNameRessourcePath[0]);
		if ( barMenu == null) {
			barMenu = new JMenu(resourceLoader.getString(menuNameRessourcePath[0]))
			{			
				@Override
				public boolean isFocusable() 
				{
					return false;
				}
			};

			menuMap.put(menuNameRessourcePath[0], barMenu);
			menubar.add(barMenu);
		}
		
		if( menuNameRessourcePath.length > 1) {

			JMenu subMenu = menuMap.get(menuNameRessourcePath[1]);
			if ( subMenu == null) {
				subMenu = new JMenu(resourceLoader.getString(menuNameRessourcePath[1]))
				{
					@Override
					public boolean isFocusable() {
						return false;
					}
				};
				menuMap.put(menuNameRessourcePath[1], subMenu);

				
				barMenu.add(subMenu);
				if( itemForAction != null ) {
					subMenu.add(itemForAction);
				} else {
					if( isSeperator) {
						subMenu.addSeparator();
					}
				}
			}
		} else {
			if( itemForAction != null ) {
				barMenu.add(itemForAction);
			}else {
				if( isSeperator) {
					barMenu.addSeparator();
				}
			}
		}

	}

	private JMenuItem createMenuItem(Action anAction, boolean isToggleBtn)
	{
		JMenuItem item;
		if( isToggleBtn) {
			
			Object groupObj = anAction.getValue(AdminActionBar.BTN_PROPERTY_BTN_GROUP);
			if( groupObj != null == groupObj instanceof ButtonGroup) {
				item = new JRadioButtonMenuItem(anAction);
	        	((ButtonGroup)groupObj).add(item);
			} else {
				item = new JCheckBoxMenuItem(anAction)
				{
					@Override
					public boolean isFocusable() 
					{
						return false;
					}
				};
			}
			
		} else {
			
			item = new JMenuItem(anAction)
			{
				@Override
				public boolean isFocusable() 
				{
					return false;
				}
			};
		}

		if(anAction.getValue(AbstractAction.ACCELERATOR_KEY) instanceof KeyStroke){
			item.setAccelerator((KeyStroke) anAction.getValue(AbstractAction.ACCELERATOR_KEY));
		}

		item.setToolTipText(null);
		
		return item;
		
	}

	public void addToggleAction(Action actionToAdd, String menuNameRessource, String subMenuRessource, ButtonGroup aButtonGroup) 
	{
		JMenu subMenu = menuMap.get(subMenuRessource);
		if ( subMenu == null) {
			subMenu = new JMenu(resourceLoader.getString(subMenuRessource));
			menuMap.put(subMenuRessource, subMenu);

			JMenu menu = menuMap.get(menuNameRessource);
			if ( menu == null) {
				menu = new JMenu(resourceLoader.getString(menuNameRessource));
				menuMap.put(menuNameRessource, menu);
				menubar.add(menu);
			}
			
			menu.add(subMenu);
		}

		JMenuItem item = new JRadioButtonMenuItem(actionToAdd);
		if(actionToAdd.getValue(AbstractAction.ACCELERATOR_KEY) instanceof KeyStroke){
			item.setAccelerator((KeyStroke) actionToAdd.getValue(AbstractAction.ACCELERATOR_KEY));
		}

		item.setToolTipText(null);
		aButtonGroup.add(item);
		subMenu.add(item);
	}


	public void addSeparator( String menuNameRessource) {
		
		JMenu menu = menuMap.get(menuNameRessource);
		if ( menu == null) {
			menu = new JMenu(resourceLoader.getString("common.menu.File"));
			menubar.add(menu);
			menuMap.put(menuNameRessource, menu);
		}
		menu.addSeparator();
	}


	/**	* Returns the menubar.
	*/
	public void addToFrame(JFrame aFrameToAdd)
	{
		aFrameToAdd.setJMenuBar(this.menubar);
	}
}
