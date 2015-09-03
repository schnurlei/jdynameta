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

import java.util.ArrayList;
import java.util.List;

/**
 * Panel which holds many other ManagedPanels
 * @author Rainer Schneider
 *
 */
public class CompositeManagedPanel extends ManagedPanel 
{
	private List<ManagedPanel> panelColl;
	
	public CompositeManagedPanel() 
	{	
		this.panelColl = new ArrayList<ManagedPanel>();
	}	

	/**
	 * Add a Panel to the List of Managed Panels
	 * @param aPanel
	 */
	protected void addSubPanel(ManagedPanel aPanel)
	{
		this.panelColl.add(aPanel);
	}
	
	/**
	 * Remove a Panel from the List of Managed Panels
	 * @param aPanel
	 */
	protected void removeSubPanel(ManagedPanel aPanel)
	{
		this.panelColl.remove(aPanel);
	}
	
	public void setEnvironment(PanelEnvironment aEnvironment) 
	{
		super.setEnvironment(aEnvironment);
		for (ManagedPanel curPnl : panelColl) {
			curPnl.setEnvironment(aEnvironment);
		}
	}
	
	public void activated() 
	{
		for (ManagedPanel curPnl : panelColl) {
			curPnl.activated();
		}
	}

	public void deActivated() 
	{
		for (ManagedPanel curPnl : panelColl) {
			curPnl.deActivated();
		}
	}
	
	public void closed() 
	{
		for (ManagedPanel curPnl : panelColl) {
			curPnl.closed();
		}
	}	

}