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
package de.jdynameta.view.mdi;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JComponent;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.DescriptivePanelDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PropertyManager;
import de.jdynameta.view.panel.OkPanel;
import de.jdynameta.view.panel.SavePanel;

/**
 *
 * @author  Rainer
 * @version 
 */
public class MdiPanelManager implements de.jdynameta.view.base.PanelManager 
{

	/** Creates new SdiPanelManager */
    public MdiPanelManager() 
    {
    }

    public void setDefaultFrameIcon(Image image) {
    	// TODO Auto-generated method stub
    }

     /**
	 * Display the Base Panel in a Frame
	 * @param aPanel the to show
	 */
	public void displayPanel(DescriptivePanelDecorator aPanel, ManagedPanel aParentPnl) 
	{
	}
	
	public JdyResourceLoader res()
	{
		return getResourceLoader();
	}
		
	public JdyResourceLoader getResourceLoader()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public boolean displayDialog(OkPanel panel, String title, Dimension defaultSize, JComponent parent)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean displayDialog(SavePanel panel, String title,	Dimension defaultSize, boolean askForSave, JComponent parent) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public PropertyManager getPropertyManager()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void displayErrorDialog(JComponent parentComponent, String message,
			Throwable exception) {
		// TODO Auto-generated method stub
		
	}	
}
