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
 * Decorates an Panel with additional Information for displaying it in an Frame
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
public interface DescriptivePanelDecorator
{
	public ManagedPanel getManagedPanel();

	/**
	 * Image, which represent the Panel. 
	 * Displayed for example int the Titlebar of a Frame 
	 * @return the Image of the panel
	 */
	public Image getPanelImage();
	
	/**
	 * 
	 * @return List of DescriptiveActionWrapper
	 */
	public List<ActionDecorator> getActionColl();

	/**
	 * Get Action, which should be executed after the close of the Frame
	 * and some Information,  how the action should be displayed   
	 * @return 
	 */
	public ActionDecorator getCloseActionDecorator();
	
	/**
	 * Default size for the Frame
	 * @return
	 */
	public Dimension getDefaultSize();
}