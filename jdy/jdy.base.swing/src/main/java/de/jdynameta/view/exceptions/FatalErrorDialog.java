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
package de.jdynameta.view.exceptions;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import de.jdynameta.view.util.GuiUtil;

public class FatalErrorDialog extends JDialog
{

	public FatalErrorDialog()
	{
		super((JDialog)null, true);
		 
		setResizable(false);
		getContentPane().setLayout(new GridBagLayout());
		setSize(330, 138);
		setTitle("About");

		GuiUtil.centerWindow(this);
	
		JButton close= new JButton("Close");
		close.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);
		getRootPane().setDefaultButton(close);
		JLabel label1= new JLabel("JUnit");
			
		JLabel label2= new JLabel("FATAL ERROR occured");
		label2.setFont(new Font("dialog", Font.PLAIN, 14));
		label2.setFont(new Font("dialog", Font.PLAIN, 36));
	
//		JLabel logo= createLogo();

		GridBagConstraints constraintsLabel1= new GridBagConstraints();
		constraintsLabel1.gridx = 3; constraintsLabel1.gridy = 0;
		constraintsLabel1.gridwidth = 1; constraintsLabel1.gridheight = 1;
		constraintsLabel1.anchor = GridBagConstraints.CENTER;
		getContentPane().add(label1, constraintsLabel1);

		GridBagConstraints constraintsLabel2= new GridBagConstraints();
		constraintsLabel2.gridx = 2; constraintsLabel2.gridy = 1;
		constraintsLabel2.gridwidth = 2; constraintsLabel2.gridheight = 1;
		constraintsLabel2.anchor = GridBagConstraints.CENTER;
		getContentPane().add(label2, constraintsLabel2);

		GridBagConstraints constraintsButton1= new GridBagConstraints();
		constraintsButton1.gridx = 2; constraintsButton1.gridy = 2;
		constraintsButton1.gridwidth = 2; constraintsButton1.gridheight = 1;
		constraintsButton1.anchor = GridBagConstraints.CENTER;
		constraintsButton1.insets= new Insets(8, 0, 8, 0);
		getContentPane().add(close, constraintsButton1);

		GridBagConstraints constraintsLogo1= new GridBagConstraints();
		constraintsLogo1.gridx = 2; constraintsLogo1.gridy = 0;
		constraintsLogo1.gridwidth = 1; constraintsLogo1.gridheight = 1;
		constraintsLogo1.anchor = GridBagConstraints.CENTER;
		getContentPane().add(new JLabel("icon"), constraintsLogo1);

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			}
		);
	
	}
	
//	protected JLabel createLogo() 
//	{
//		Icon icon= FatalErrorDialog.getIconResource(FatalErrorDialog.class, "logo.gif");
//		return new JLabel(icon);
//	}
	
//	public static Icon getIconResource(Class clazz, String name) {
//		URL url= clazz.getResource(name);
//		if (url == null) {
//			System.err.println("Warning: could not load \""+name+"\" icon");
//			return null;
//		}
//		return new ImageIcon(url);
//	}
	
	public static void main(String[] args)
	{
		FatalErrorDialog dialog = new FatalErrorDialog();
		dialog.pack();
		dialog.setVisible(true);
	}
}
