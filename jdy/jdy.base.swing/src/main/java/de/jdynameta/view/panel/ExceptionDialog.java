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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.jdynameta.view.JdyResourceLoader;

/**
 * Common Exception Dialog, to show The Message of an Exception.
 * Shows the Stack trace on demand
 * @author Rainer Scneider
 *
 */
public class ExceptionDialog extends JDialog {


	private final AbstractAction actOk;
	private JScrollPane scrlStackTrace;
	private final JdyResourceLoader resourceLoader;
	
	/**
	 * 
	 * @param aParent
	 * @param exception
	 */
	public ExceptionDialog(Component aParent, Throwable aException, JdyResourceLoader aResourceLoader) 
	{
		this( aParent
				, aException == null ? aResourceLoader.getString("ExceptionDialog.title.Error") : aResourceLoader.getString("ExceptionDialog.title")
				, aException.getLocalizedMessage()
				, aException
				, aResourceLoader);
	}

	/**
	 * 
	 * @param aParent
	 * @param exception
	 */
	public ExceptionDialog(Component aParent, String aMessage, Throwable aException, JdyResourceLoader aResourceLoader) 
	{
		this( aParent
				, aException == null ? aResourceLoader.getString("ExceptionDialog.title.Error") : aResourceLoader.getString("ExceptionDialog.title")
				, aMessage
				, aException
				, aResourceLoader);
	}
	
	/**
	 * 
	 * @param aParent
	 * @param exception
	 */
	public ExceptionDialog(Component aParent, String aTitle, String aMessage, Throwable aException, JdyResourceLoader aResourceLoader) 
	{
		super(JOptionPane.getFrameForComponent(aParent)
				, aTitle
				, true);

		this.resourceLoader = aResourceLoader;
		actOk = createOkAction();
		this.init(aMessage, aException);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    applyComponentOrientationForDefaultLocale(this);
	    if(aParent != null) {
	    	this.setLocationRelativeTo(aParent);
	    }
	    this.pack();
	}

	/**
	 * Applies the component orientation defined in the default locale to the given component
	 * and all components contained within it
	 * @see java.awt.Container#applyComponentOrientation() 
	 * @param aComponent
	 */
	public static void applyComponentOrientationForDefaultLocale(Component aComponent)
	{
		aComponent.applyComponentOrientation( ComponentOrientation.getOrientation(Locale.getDefault()) );
	}
	
	
	private void init(String aMessage, Throwable exception)
	{
		JLabel lblMsg = new JLabel(aMessage);
		JButton btnShowDetails = new JButton(createShowDetailsAction());
		StringWriter logStringWriter = new StringWriter();
		if ( exception != null) {
			exception.printStackTrace(new PrintWriter(logStringWriter));
		}
	    JTextArea txaStackTrace = new JTextArea(logStringWriter.toString());
        this.scrlStackTrace = new JScrollPane(txaStackTrace
        		, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
				, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
	    this.scrlStackTrace.setPreferredSize(new Dimension(300,200) );
        this.scrlStackTrace.setVisible(false);
        GridBagConstraints constrMain = new GridBagConstraints(0, 0, 1, 1
    			,1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL
    			, new Insets(10,10,10,10), 0,0 );       
        JPanel pnlMain = new JPanel(new GridBagLayout());
        
        pnlMain.add(lblMsg, constrMain);
        constrMain.gridx = 1; constrMain.weightx = 0.0;
		if ( exception != null) {
			pnlMain.add(btnShowDetails, constrMain);
		}
        constrMain.gridx = 0; constrMain.gridy = 1; constrMain.gridwidth = 2;
        constrMain.weightx = 1.0; constrMain.weighty = 1.0; constrMain.fill = GridBagConstraints.BOTH; 
        pnlMain.add(scrlStackTrace, constrMain);
 
        
        final JButton okButton = new JButton(this.actOk);
        JPanel buttonPane = new JPanel(new GridLayout(1,4, 10, 10)); // gridLayout to ensure same size of buttons
        buttonPane.add(okButton);
        JPanel pnlButtonsDecorator = new JPanel(new GridBagLayout()); // Gridbag to place it in the middle, ensure size is not wider the necessary
        pnlButtonsDecorator.add(buttonPane, new GridBagConstraints(0, 0, 1, 1
    			,0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE
    			, new Insets(10,0,10,0), 0,0 ));
        
	    this.getRootPane().setDefaultButton(okButton);
	    this.getContentPane().setLayout(new BorderLayout());
	    this.getContentPane().add(pnlMain, BorderLayout.CENTER);
	    this.getContentPane().add(pnlButtonsDecorator, BorderLayout.PAGE_END);
 	}
	
	@SuppressWarnings("serial")
	private AbstractAction createOkAction()
	{
		return new AbstractAction(this.resourceLoader.getString("common.action.ok"))
		{
			public void actionPerformed(ActionEvent e) {
				
				dispose();
			}
		};
	}

	@SuppressWarnings("serial")
	private AbstractAction createShowDetailsAction()
	{
		return new AbstractAction(this.resourceLoader.getString("ExceptionDialog.action.showDetails"))
		{
			public void actionPerformed(ActionEvent e) {
				
				ExceptionDialog.this.scrlStackTrace.setVisible(!ExceptionDialog.this.scrlStackTrace.isVisible());
				ExceptionDialog.this.invalidate();
				ExceptionDialog.this.repaint();
				ExceptionDialog.this.pack();

			}
		};
	}
	
}
