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
package de.jdynameta.metainfoview.metainfo.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Simple component to draw a horizontal line
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class EtchedLine extends JComponent
{
	enum Position{TOP, CENTER, BOTTOM};
	
	/** Raised etched type. */
	public static final int RAISED = 0;
	/** Lowered etched type. */
	public static final int LOWERED = 1;

	protected int etchType;
	protected Color highlight;
	protected Color shadow;
	private Position pos = Position.BOTTOM;

	protected JComponent parent = new JPanel();
	
	/**
	 * Creates a lowered etched border whose colors will be derived from the
	 * background color of the component passed into the paintBorder method.
	 */
	public EtchedLine()
	{
		this(LOWERED);
	}

	/**
	 * Creates an etched border with the specified etch-type whose colors will
	 * be derived from the background color of the component passed into the
	 * paintBorder method.
	 * 
	 * @param etchType
	 *            the type of etch to be drawn by the border
	 */
	public EtchedLine(int etchType)
	{
		this(etchType, null, null);
	}

	/**
	 * Creates a lowered etched border with the specified highlight and shadow
	 * colors.
	 * 
	 * @param highlight
	 *            the color to use for the etched highlight
	 * @param shadow
	 *            the color to use for the etched shadow
	 */
	public EtchedLine(Color highlight, Color shadow)
	{
		this(LOWERED, highlight, shadow);
	}

	/**
	 * Creates an etched border with the specified etch-type, highlight and
	 * shadow colors.
	 * 
	 * @param etchType
	 *            the type of etch to be drawn by the border
	 * @param highlight
	 *            the color to use for the etched highlight
	 * @param shadow
	 *            the color to use for the etched shadow
	 */
	public EtchedLine(int etchType, Color highlight, Color shadow)
	{
		this.etchType = etchType;
		this.highlight = highlight;
		this.shadow = shadow;
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		int w = getWidth();
		int yPos = getHeight();

		switch (pos) {
			case TOP:
				yPos = 0;
				break;
			case CENTER:
				yPos = getHeight()/2;
				break;
			case BOTTOM:
				yPos = getHeight()-4;
				break;

		} 
		
		g.setColor(etchType == LOWERED ? getShadowColor(parent)
				: getHighlightColor(parent));
		g.drawLine(0, yPos, w - 2, yPos);

		g.setColor(etchType == LOWERED ? getHighlightColor(parent)
				: getShadowColor(parent));
		g.drawLine(1, yPos+1, w - 2, yPos+1);

	}
	
	/**
	 * Returns the insets of the border.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c)
	{
		return new Insets(2, 2, 2, 2);
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @param insets
	 *            the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets)
	{
		insets.left = insets.top = insets.right = insets.bottom = 2;
		return insets;
	}

	/**
	 * Returns whether or not the border is opaque.
	 */
	public boolean isBorderOpaque()
	{
		return true;
	}

	/**
	 * Returns which etch-type is set on the etched border.
	 */
	public int getEtchType()
	{
		return etchType;
	}

	/**
	 * Returns the highlight color of the etched border when rendered on the
	 * specified component. If no highlight color was specified at
	 * instantiation, the highlight color is derived from the specified
	 * component's background color.
	 * 
	 * @param c
	 *            the component for which the highlight may be derived
	 * @since 1.3
	 */
	public Color getHighlightColor(Component c)
	{
		return highlight != null ? highlight : c.getBackground().brighter();
	}

	/**
	 * Returns the highlight color of the etched border. Will return null if no
	 * highlight color was specified at instantiation.
	 * 
	 * @since 1.3
	 */
	public Color getHighlightColor()
	{
		return highlight;
	}

	/**
	 * Returns the shadow color of the etched border when rendered on the
	 * specified component. If no shadow color was specified at instantiation,
	 * the shadow color is derived from the specified component's background
	 * color.
	 * 
	 * @param c
	 *            the component for which the shadow may be derived
	 * @since 1.3
	 */
	public Color getShadowColor(Component c)
	{
		return shadow != null ? shadow : c.getBackground().darker();
	}

	/**
	 * Returns the shadow color of the etched border. Will return null if no
	 * shadow color was specified at instantiation.
	 * 
	 * @since 1.3
	 */
	public Color getShadowColor()
	{
		return shadow;
	}

}
