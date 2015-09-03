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

import javax.swing.JComponent;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.panel.OkPanel;
import de.jdynameta.view.panel.SavePanel;


/**
 * Manages the opening and closing of Panels
 * Must be implemented, for special kinds of PanelManager like sdi or mdi
 * @author  Rainer
 * @version 
 */
public interface PanelManager 
{
	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(DescriptivePanelDecorator aPanel, ManagedPanel aParentPnl);

	/**
	 * Get the resource loader
	 * @return
	 */
	public JdyResourceLoader res();
	
	@Deprecated 
	public JdyResourceLoader getResourceLoader();

	public boolean displayDialog(OkPanel aPanel, String aTitle, Dimension aDefaultSize, JComponent parent);

	public boolean displayDialog(SavePanel aPanel, String aTitle, Dimension aDefaultSize, boolean askForSave, JComponent parent);
	
	public PropertyManager getPropertyManager();

	public void displayErrorDialog(JComponent parentComponent, String message,	Throwable exception);

	public void setDefaultFrameIcon(Image image);

}
