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
/*
 *	SizedButton.java
 * Created on 07.08.2003
 *
 */
package de.jdynameta.view.field;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * Button with a defined Size for a GridBagLayout
 *
 * @author Rainer Schneider
 *
 */
public class FixedSizeButton extends JButton
{
	private int _width;	
	
	/**
	 * @param a
	 */
	public FixedSizeButton(Action aAction, int aWidth)
	{
		super(aAction);
		_width = aWidth;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize()
	{
		Dimension dim = super.getPreferredSize();
		dim.width = _width;
		return dim;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize()
	{
		Dimension dim = super.getMinimumSize();
		dim.width = _width;
		return dim;
	}

}
