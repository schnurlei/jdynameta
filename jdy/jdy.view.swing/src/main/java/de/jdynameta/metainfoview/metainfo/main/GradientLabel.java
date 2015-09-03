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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * JLabel with simple gradient background
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class GradientLabel extends JLabel
{
	private Color start = Color.WHITE;
	private Color end = Color.LIGHT_GRAY;

	public GradientLabel()
	{
	}

	public GradientLabel(String text)
	{
		super(text);
	}

	public GradientLabel(Icon image)
	{
		super(image);
	}

	public GradientLabel(String text, int horizontalAlignment)
	{
		super(text, horizontalAlignment);
	}

	public GradientLabel(Icon image, int horizontalAlignment)
	{
		super(image, horizontalAlignment);
	}

	public GradientLabel(String text, Icon icon, int horizontalAlignment)
	{
		super(text, icon, horizontalAlignment);
	}

	public void paint(Graphics oldG)
	{

		int width = getWidth();
		int height = getHeight();

		GradientPaint paint = new GradientPaint(0, 0, start, width, height,	end, true);
		Graphics2D g2d = (Graphics2D) oldG;
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(paint);

		// fill the background using the paint
		g2d.fillRect(0, 0, width, height);

		// restore the original paint
		g2d.setPaint(oldPaint);

		super.paint(oldG);
	}

}
