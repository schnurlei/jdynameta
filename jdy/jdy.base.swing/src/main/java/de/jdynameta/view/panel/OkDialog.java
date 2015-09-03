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
package de.jdynameta.view.panel;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.field.FixedSizeButton;


/**
 * @author rs
 * @version 24.10.2002
 */
public class OkDialog
	extends JDialog 
{
	private JPanel mainPanel;
	protected int result;
	protected final JdyResourceLoader resourceLoader;


    public OkDialog(JdyResourceLoader aResourceLoader, Dialog owner, String title, boolean modal) throws HeadlessException 
    {
        super(owner, title, modal);     
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        resourceLoader =aResourceLoader;
        createMainPanel();
    }

    public OkDialog(JdyResourceLoader aResourceLoader, Frame owner, String title, boolean modal) throws HeadlessException 
    {
        super(owner, title, modal);     
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        resourceLoader =aResourceLoader;
        createMainPanel();
    }

	public void setPanel(JPanel aPanel) 
	{
		mainPanel.add(aPanel, BorderLayout.CENTER);	
	}

	public int getResult() 
	{
		return result;
	}


	private void createMainPanel()
	{
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,0));
		JButton okButton = new FixedSizeButton(createOkAction(), 100);
		btnPanel.add(okButton);
		btnPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		this.setContentPane(mainPanel);
		
		this.getRootPane().setDefaultButton(okButton);
	}


	private Action createOkAction() 
	{
		return new AbstractAction(resourceLoader.getString("common.action.ok")) 
		{
			public void actionPerformed(ActionEvent event)
			{
				result = JOptionPane.OK_OPTION;
				OkDialog.this.dispose();
			}
		};	
	}


 	public  static Window getWindowForComponent(Component parentComponent)
        throws HeadlessException 
	{
    	if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }


	public static OkDialog createDialog(JdyResourceLoader aResourceLoader, Component parentComponent, String title, boolean modal) 
	{
		OkDialog dialog; 
    	Window window = getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new OkDialog(aResourceLoader, (Frame)window, title, modal);	
        } else {
            dialog = new OkDialog(aResourceLoader, (Dialog)window, title, modal);
        }

		return dialog;		
	}
}
