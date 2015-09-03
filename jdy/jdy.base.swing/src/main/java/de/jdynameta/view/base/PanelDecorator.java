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

import java.awt.Dimension;
import java.awt.Image;
import java.util.List;

import de.jdynameta.view.action.ActionDecorator;

/**
 * Default implementation of an DescriptivePanelDecorator
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
public class PanelDecorator implements DescriptivePanelDecorator
{
	private final Image panelImage;
	private final ManagedPanel managedPanel;
	private final List<ActionDecorator> allActionsColl;
	private Dimension defaultSize;
	private ActionDecorator closeActionDecorator;
	
	public PanelDecorator(  ManagedPanel aManagedPanel)
	{
		this(null,aManagedPanel,null, null);
	}
	
	/**
	 * @param aPanelTitle
	 * @param aPanelImage
	 * @param aManagedPanel
	 */
	public PanelDecorator( Image aPanelImage, ManagedPanel aManagedPanel
	        								,  List<ActionDecorator> aAllActionsColl)
	{
		this(aPanelImage,aManagedPanel,aAllActionsColl, null);
	}

	/**
	 * @param aPanelTitle
	 * @param aPanelImage
	 * @param aManagedPanel
	 */
	public PanelDecorator( Image aPanelImage, ManagedPanel aManagedPanel
	        								,  List<ActionDecorator> aAllActionsColl
	        								, Dimension aDefaultSize)
	{
		super();
		this.panelImage = aPanelImage;
		this.managedPanel = aManagedPanel;
		this.allActionsColl = aAllActionsColl;
		this.defaultSize = aDefaultSize;
	}
	
	
	public ManagedPanel getManagedPanel()
	{
		return this.managedPanel;
	}

	public Image getPanelImage()
	{
		return this.panelImage;
	}
	
	public List<ActionDecorator> getActionColl() {
	    
	    return this.allActionsColl;
	}
	
	public Dimension getDefaultSize()
	{
		return defaultSize;
	}
	
	public ActionDecorator getCloseActionDecorator() 
	{
		return this.closeActionDecorator;
	}
	
	public void setCloseActionDecorator(ActionDecorator closeActionDecorator) 
	{
		this.closeActionDecorator = closeActionDecorator;
	}
}
