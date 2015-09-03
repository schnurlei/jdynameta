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

import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
@SuppressWarnings("serial")
public abstract class AdminAbstractAction extends AbstractAction
{

	/**
	 * 
	 * @param aActionName
	 * @param anIcon
	 * @param aDescription tool tip text
	 * @param aAcceleratorKey key that triggers the Action in a toolbar 
	 * 							<code> KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)) </code>
	 */
	public AdminAbstractAction(String aActionName, Icon anIcon, String aDescription, KeyStroke aAcceleratorKey)
	{
		super(aActionName, anIcon);
		
		putValue(AbstractAction.SHORT_DESCRIPTION, aDescription);
		putValue(AbstractAction.ACCELERATOR_KEY, aAcceleratorKey);
		
	}

	/**
	 * Convert an Icon into an Image
	 * a Button can only image icon disable automaticly
	 */
	public static ImageIcon createImageIcon( Icon aIcon) 
	{
		BufferedImage image = new BufferedImage(aIcon.getIconWidth(), aIcon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		aIcon.paintIcon(null, image.getGraphics(),0,0);
		return new ImageIcon(image);
	}
	
}
