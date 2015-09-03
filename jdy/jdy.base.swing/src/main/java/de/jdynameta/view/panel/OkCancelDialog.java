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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.field.FixedSizeButton;


/**
 * @author rs
 * @version 24.10.2002
 */
public class OkCancelDialog extends JDialog 
{
	private JPanel mainPanel;
	protected OkPanel.OkPanelListener savePanelListener;
	private OkPanel okPanel;
	private Action okAction; // lazy initilized
	
	protected int result;
	protected final JdyResourceLoader resourceLoader;
	protected WindowListener windowCloseListner;
	

    public OkCancelDialog(JdyResourceLoader aResourceLoader, Dialog owner, String title, boolean modal) throws HeadlessException 
    {
        super(owner, title, modal);
        resourceLoader = aResourceLoader;
		savePanelListener =  createOkPanelListener();
		windowCloseListner = createWindowCloseListener();
		addWindowListener(windowCloseListner);

       createMainPanel();
    }

    public OkCancelDialog(JdyResourceLoader aResourceLoader, Frame owner, String title, boolean modal) throws HeadlessException 
    {
        super(owner, title, modal);     
        resourceLoader =aResourceLoader;
		savePanelListener =  createOkPanelListener();
		windowCloseListner = createWindowCloseListener();
		addWindowListener(windowCloseListner);

        createMainPanel();
    }

	private WindowListener createWindowCloseListener()
	{
		return new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					cancelDialog();
				}
				@Override
				public void windowClosed(WindowEvent e)
				{
					OkCancelDialog.this.removeWindowListener(windowCloseListner);
					windowCloseListner = null;
				}
			};
	}

	public void setPanel(OkPanel aPanel) 
	{
		if ( okPanel != null) {
			okPanel.setOkPanelListener(null);
		}
		
		if( aPanel != null) {
			okPanel = aPanel;
		} else {
			okPanel = new DefaultOkPanel();
		}	

		okPanel.setOkPanelListener(savePanelListener);
		okAction.setEnabled(okPanel.canClose());

		mainPanel.add(okPanel, BorderLayout.CENTER);	
	}


	/**
	 * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION;
	 */
	public int getResult() 
	{
		return result;
	}

	protected  OkPanel.OkPanelListener createOkPanelListener()
	{
		return new OkPanel.OkPanelListener()
		{
			public void canCloseChanged() 
			{
				getOkAction().setEnabled(okPanel.canClose());
			}
		};		
	}

	private void commitDialog()
	{
		try
		{
			okPanel.close();
			okPanel.closed();
			result = JOptionPane.OK_OPTION;
			OkCancelDialog.this.dispose();

		} catch (CloseVetoException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage()); //TODO
		}
	}


	private void cancelDialog()
	{
		result = JOptionPane.CANCEL_OPTION;
		this.dispose();
	}

	private void createMainPanel()
	{
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,0));
		
		FixedSizeButton okBtn = new FixedSizeButton(getOkAction(), 100);
		btnPanel.add(okBtn);
		btnPanel.add(new FixedSizeButton(createCancelAction(), 100 ));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.setContentPane(mainPanel);
		this.getRootPane().setDefaultButton(okBtn);
	}


	private Action getOkAction() 
	{
		if ( okAction == null) {
		
			String actionText = (resourceLoader != null) ?  resourceLoader.getString("action.dialog.ok") : "Ok";
		
			okAction = new AbstractAction(actionText) 
			{
				public void actionPerformed(ActionEvent event)
				{
					commitDialog();
				}
			};
		}
		
		return okAction;
	
	}

	private Action createCancelAction() 
	{
		String actionText = (resourceLoader != null) ?  resourceLoader.getString("action.dialog.cancel") : "Cancel";

		return new AbstractAction(actionText) 
		{
			public void actionPerformed(ActionEvent event)
			{
				cancelDialog();
			}
		};	
	}

 	public  static Window getWindowForComponent(Component parentComponent)
        throws HeadlessException 
	{
    	if (parentComponent == null || parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }


	public static OkCancelDialog createDialog(JdyResourceLoader aResourceLoader, Component parentComponent, String title, boolean modal) 
	{
		OkCancelDialog dialog; 
    	Window window = getWindowForComponent(parentComponent);
        if (window == null || window instanceof Frame) {
            dialog = new OkCancelDialog(aResourceLoader, (Frame)window, title, modal);	
        } else {
            dialog = new OkCancelDialog(aResourceLoader, (Dialog)window, title, modal);
        }

		return dialog;		
	}
		
}
