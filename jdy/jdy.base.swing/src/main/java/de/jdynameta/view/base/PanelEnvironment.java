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

import de.jdynameta.view.panel.CloseVetoException;

/**
 * Manage the notifications from the Panel to the Frame (o similar Object) 
 * which displays the panel 
 * @author  Rainer
 * @version 
 */
public interface PanelEnvironment 
{
	public void panelWantsToClose() throws CloseVetoException;
	
	public void panelTitleChanged();
	
	public void displayPanelAtFront();
	
	public void showStatusMessage(String aMessage);
}

