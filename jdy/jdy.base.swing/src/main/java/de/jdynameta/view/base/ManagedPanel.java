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
package de.jdynameta.view.base;

import javax.swing.JPanel;

import de.jdynameta.view.panel.CloseVetoException;

/**
 * Panel managed by the PanelManager
 * @subclass 
 * @author  Rainer
 * @version 
 */
@SuppressWarnings("serial")
public abstract class ManagedPanel extends JPanel 
{
	/** Objct wich processe the notifications from the Panel to the
		Frame which displays the panel */
	private PanelEnvironment panelEnvironment; 
	  
	/** Title of the Panel, displayed for example in the Titlebar of a Frame 
		which displays this panel*/
	private String title;
	
	/** Creates new PanelObject */
    public ManagedPanel() 
    {
		super();
		this.title = "";
		this.panelEnvironment = new NullPanelEnvironment();
    }


	/**
	 * Set the Holder of this panel
	 * Should only be used by the panel Handler 
	 */
	public void setEnvironment(PanelEnvironment aEnvironment) 
	{
		this.panelEnvironment = aEnvironment;
		if( this.panelEnvironment == null ) {
			this.panelEnvironment = new NullPanelEnvironment();
		}
	}
	
	/**
	 * Called from the Handler when the panel is activated
	 * @subclass could overwite it, when something must be done on activtion
	 *				(set Focus for Example)
	 */
	public void activated() 
	{
		
	}
	
	/**
	 * Called from the Handler when the panel is deactivated
	 * @subclass could overwite it, when something must be done on deactivtion
	 */
	public void deActivated() 
	{
		
	}
	
	/**
	 * Called from the Handler when the panel is closed
	 * @subclass could overwrite it, when something must be done on closing
	 *					save contents for example
	 */
	public void closed() 
	{
		
	}	

	public void makeVisible()
	{
		
	}
	
	/**
	 * Get the title of the Panel, displayed for example int the Titlebar of a Frame 
	 * @return the title of the Panel 
	 */
	public String getTitle() 
	{
		return this.title;
	}

	/**
	 * Set the title of the panel 
	 * @subclass use this Method to set the title of the Panel
	 */
	public void setTitle(String aNewTitle) 
	{
		this.title = aNewTitle;
		this.panelEnvironment.panelTitleChanged();
	}
	
	protected void showStatusMessage(String aMessage) 
	{
		this.panelEnvironment.showStatusMessage(aMessage);
	}
	
	protected void displayPanelAtFront(String aMessage) 
	{
		this.panelEnvironment.displayPanelAtFront();
	}

	protected void requestForClose(String aMessage) throws CloseVetoException 
	{
		this.panelEnvironment.panelWantsToClose();
	}
	
	
	public static class NullPanelEnvironment implements PanelEnvironment
	{
		public NullPanelEnvironment()
		{
		}
		
		public void displayPanelAtFront()
		{
		}
		
		public void panelTitleChanged()
		{
		}		

		public void panelWantsToClose()
		{
		}	

		public void showStatusMessage(String aMessage)
		{
		}		
	}

	
}
