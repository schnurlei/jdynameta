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
package de.jdynameta.view.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author  Rainer
 * @version 
 */
public class GuiUtil {

	/** Creates new GuiUtil */
    public GuiUtil() {
    }

	/** Calculate the size of the frame relative to the Size of the whole Screen
	 *
	*/
	public void setFrameSizeFromSreenSize(JFrame aFrame) {
		// load Frame size of last time
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point pos = new Point (0,0);
		aFrame.setSize(screenSize);
		aFrame.setLocation(pos);
	}
	
	public static void centerWindow(Component c) 
	{
		Dimension paneSize= c.getSize();
		Dimension screenSize= c.getToolkit().getScreenSize();
		c.setLocation((screenSize.width-paneSize.width)/2, (screenSize.height-paneSize.height)/2);
	}
	
	
	public static void installSystemLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException excp)
		{
			excp.printStackTrace();
		} catch (InstantiationException excp)
		{
			excp.printStackTrace();
		} catch (IllegalAccessException excp)
		{
			excp.printStackTrace();
		} catch (UnsupportedLookAndFeelException excp)
		{
			excp.printStackTrace();
		}
	}
	
}
