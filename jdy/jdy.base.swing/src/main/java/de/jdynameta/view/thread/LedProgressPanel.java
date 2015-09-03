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
package de.jdynameta.view.thread;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;


/**
 * Panel that show blinking led's while a progress is running
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class LedProgressPanel extends JComponent
{
	private final int borderSize = 2;
	private final int preferredLedWidth = 5;
	private final int preferredLedHeigth = 5;
	
	private int startDelayInMs;

	private int ledToMark = 1;
	private final Object lock = new Object();
	
	public LedProgressPanel()
	{
		super();
		this.startDelayInMs = 1000;
		ledToMark = -1;
		setVisible(false);
	}
	

	public void stop()
	{
		synchronized (lock) {
			ledToMark = -1;
			LedProgressPanel.this.repaint();
		}
	}

	/**
	 * Start LED blink thead
	 */
	public final void start() 
	{
		
		synchronized (lock) {
			ledToMark = 0;
		}
		
		if( startDelayInMs == 0 ) {
			LedProgressPanel.this.invalidate();
			LedProgressPanel.this.repaint();
			setVisible(true);			
		}
		
		final long startTime = System.currentTimeMillis();

		new Thread() 
		{
			public void run() 
			{
				while(ledToMark != -1) {
					
					if( ( System.currentTimeMillis() - startTime) > startDelayInMs)  {
						setVisible(true);
						synchronized (lock) {
							ledToMark++;
							if (ledToMark == 5) {
								ledToMark = 1;
							}
						}
						LedProgressPanel.this.invalidate();
						LedProgressPanel.this.repaint();
					}

					try {
						sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				setVisible(false);
			};
		}.start();
	}
	
	@Override
	public Dimension getPreferredSize() {

		return new Dimension(preferredLedWidth*2+ borderSize*3
				, preferredLedHeigth*2+ borderSize*3);
	}
	
	@Override
	public void paintComponent(Graphics aGraphics)
	{
		super.paintComponent(aGraphics);

		Dimension size = getSize();
		
		final int ledWidth = (size.width-borderSize*3)/2;
		final int ledHeigth = (size.height-borderSize*3)/2;

		setColorForLedAtPos(aGraphics,1);
		aGraphics.fill3DRect(borderSize, borderSize, ledWidth, ledHeigth,false);
		setColorForLedAtPos(aGraphics,2);
		aGraphics.fill3DRect(2*borderSize+ledWidth, borderSize, ledWidth, ledHeigth,false);
		setColorForLedAtPos(aGraphics,4);
		aGraphics.fill3DRect(borderSize, 2*borderSize+ledHeigth, ledWidth, ledHeigth,false);
		setColorForLedAtPos(aGraphics,3);
		aGraphics.fill3DRect(2*borderSize+ledWidth, 2*borderSize+ledHeigth, ledWidth, ledHeigth,false);
		
	}

	private void setColorForLedAtPos(Graphics aGraphics, int ledPos) {
		if(ledToMark % 5 == ledPos ) {
			aGraphics.setColor(Color.GREEN);
		} else {
			aGraphics.setColor(Color.BLACK);			
		}
	}

	/**
	 * @param startDelayInMs the startDelayInMs to set
	 */
	public void setStartDelayInMs(int startDelayInMs) 
	{
		this.startDelayInMs = startDelayInMs;
	}

}

