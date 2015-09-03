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
package de.jdynameta.view.about;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.panel.OkDialog;


/** * Application-Info.
	*
	* @version 09/2003
	*/
@SuppressWarnings("serial")
public class AboutPanel extends ManagedPanel
{
	private static int EMPTY_LINE_HEIGHT = 20;
	protected final JdyResourceLoader resourceLoader;
	private final Version currentVersion;

	public AboutPanel(JdyResourceLoader aResourceLoader, Version aVersion)
	{
		super();
		resourceLoader = aResourceLoader;
		currentVersion = aVersion;

		this.setBackground(new Color(255, 204, 0));
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80)),
			BorderFactory.createLineBorder(Color.black)));
		setPreferredSize(new Dimension(550,200));
	}


	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 */
	@Override
	public void paint(Graphics aGraphics) {
		super.paint(aGraphics);
		
		Graphics myGraphics = aGraphics.create();
		
		if( myGraphics instanceof Graphics2D ) {
			((Graphics2D) myGraphics).setPaint(new GradientPaint(0, 0
										,new Color(255, 204, 0)
									  	,getSize().width, getSize().height, new Color(255, 242, 191), false));
			myGraphics.fillRect(getX(),getY(), getWidth(), getHeight());
		}
		
		//Font fontBig = new Font("Dialog", Font.BOLD, 20);
		Font fontNormalBold = new Font("Dialog", Font.BOLD, 12);
		Font fontNormal = new Font("Dialog", Font.PLAIN, 12);

		int x = 20;
		int y = 20;
		
		/* use this to paint an info icon
		 * _iconLoader.getIcon("about").paintIcon(this, g, 0, 0);*/
		

		String text =  currentVersion.getProjectName();
		myGraphics.setFont(fontNormalBold);	
		myGraphics.setColor(Color.BLACK);
		myGraphics.drawString(text, x, y);
		
		y += fontNormalBold.getSize() + EMPTY_LINE_HEIGHT ;

		// version
		myGraphics.drawString(resourceLoader.getString("about.version") +": "+ currentVersion.getVersionText(),x, y);
		y += fontNormalBold.getSize();
		y += fontNormalBold.getSize() + EMPTY_LINE_HEIGHT ;
		myGraphics.drawString(resourceLoader.getString("about.build")+": "+ currentVersion.getBuildText()	,x, y);
					
		y += fontNormalBold.getSize() + EMPTY_LINE_HEIGHT ;			

		// copyright

		StringTokenizer st = new StringTokenizer(resourceLoader.getString("about.copyright"), "\n");
		int i = 0;
		while(st.hasMoreTokens()){
			myGraphics.setFont(fontNormal);	
			myGraphics.drawString(st.nextToken(),x, y);
			y += fontNormal.getSize();
			++i;
		}

		y += EMPTY_LINE_HEIGHT ;			


	}
	
	private void showSystemInfo()
	{
		Vector<Vector<Object>> propertyData = getSystemProperties();
		
		
		Vector<String> colNames = new Vector<String>();
		colNames.add(resourceLoader.getString("Property"));
		colNames.add(resourceLoader.getString("Wert"));
		
		JTable aTable = new JTable(propertyData, colNames);
		JPanel propertyPnl = new JPanel(new BorderLayout());
		propertyPnl.add( new JScrollPane(aTable));
		propertyPnl.add(createMemoryUsagePanel(), BorderLayout.PAGE_END);
		
		OkDialog dialog = OkDialog.createDialog(resourceLoader, this
									,resourceLoader.getString("common.action.systeminfo"),true);
		dialog.setPanel(propertyPnl);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private JPanel createMemoryUsagePanel()
	{
		JPanel usagePanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints lblConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1
				, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE
				, new Insets(5,5,5,5), 0, 0);
		GridBagConstraints fieldConstr = new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1
				, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL
				, new Insets(5,5,5,5), 0, 0);
		GridBagConstraints fillerConstr = new GridBagConstraints(2, 0, 1, 1
				, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL
				, new Insets(5,5,5,5), 0, 0);

		Runtime rt = Runtime.getRuntime();		
		usagePanel.add(new JLabel("Total Memory (Mb):"), lblConstr);
		usagePanel.add(creteRightAlignedLabel(""+ convertToMegabyte(rt.totalMemory())), fieldConstr);
		usagePanel.add(new JLabel("Free Memory (Mb):", JLabel.RIGHT), lblConstr);
		usagePanel.add(creteRightAlignedLabel(""+ convertToMegabyte(rt.freeMemory())), fieldConstr);
		usagePanel.add(new JLabel("Max. Memory (Mb):", JLabel.RIGHT), lblConstr);
		usagePanel.add(creteRightAlignedLabel(""+ convertToMegabyte(rt.maxMemory())), fieldConstr);
		usagePanel.add(new JLabel("Avail. processors", JLabel.RIGHT), lblConstr);
		usagePanel.add(creteRightAlignedLabel(""+ rt.availableProcessors()), fieldConstr);
		
		usagePanel.add(creteRightAlignedLabel(""), fillerConstr);
		
		return usagePanel;
	}

	private long convertToMegabyte(long valueInBytes)
	{
		return Math.round((double) valueInBytes/(1000.0 * 1000.0));
	}

	private JLabel creteRightAlignedLabel(String text)
	{
		JLabel createdLbl = new JLabel(text, JLabel.RIGHT);
		createdLbl.setHorizontalTextPosition(SwingConstants.RIGHT);
		return createdLbl;
	}
	
	private Vector<Vector<Object>> getSystemProperties()
	{
		Properties sysProp = System.getProperties();
		Vector<Vector<Object>> propertyData = new Vector<Vector<Object>>(sysProp.size());
		for( Enumeration<Object> keyEnum = sysProp.keys(); keyEnum.hasMoreElements(); ) {
			
			Vector<Object> curVect = new Vector<Object>();
			Object curKey = keyEnum.nextElement();
			curVect.add(curKey);
			curVect.add(sysProp.get(curKey));

			propertyData.add(curVect);
		}
		
		Collections.sort(propertyData, new Comparator<Vector<Object>>()
		{
			public int compare(Vector<Object> o1, Vector<Object> o2)
			{
				return o1.elementAt(0).toString().compareTo(o2.elementAt(0).toString());
			}

		});
		return propertyData;
	}

	public AbstractAction createSystemInfoAction()
	{
		AbstractAction infoAct = new AbstractAction(resourceLoader.getString("common.action.systeminfo")+ "...")
		{
			public void actionPerformed(ActionEvent aEvent) 
			{
				showSystemInfo();
			}			
		};
		
		return infoAct;
	}
	
}
