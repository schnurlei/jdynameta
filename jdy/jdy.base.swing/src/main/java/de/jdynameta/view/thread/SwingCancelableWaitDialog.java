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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import de.jdynameta.view.thread.SwingCancelableThread.SwingActionThreadEvent;

@SuppressWarnings("serial")
public class SwingCancelableWaitDialog extends JDialog 
{
	private final LedProgressPanel waitPanel;
	private final JTextArea messageLabel;
	private SwingCancelableThread actionThread;

	public SwingCancelableWaitDialog(Frame owner, String title) 
	{
		this(owner, title, true);
	}
	
	public SwingCancelableWaitDialog(Frame owner, String title, boolean modal) 
	{
		super(owner, title, modal);
		
		this.waitPanel = new LedProgressPanel();
		this.waitPanel.setStartDelayInMs(0);
		this.messageLabel = new JTextArea("Waiting....");
		this.messageLabel.setEditable(false);
		this.messageLabel.setEnabled(true);
		this.messageLabel.setBackground(this.getBackground());
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e) {
				actionThread.cancel();
			}
		});
		this.initUi();
		this.setSize(300,100);
	}
	
	/**
	 * @return the canceled
	 */
	public boolean isCanceled() 
	{
		return 	actionThread.isCanceled();
	}

	/**
	 * @return the canceled
	 * 
	 */
	public Throwable getExceptionInRun() 
	{
		return 	actionThread.getExceptionInRun();
	}
	
	/**
	 * @param canceled the canceled to set
	 */
	public void setCanceled(boolean canceled) 
	{
		actionThread.cancel();
	}
	

	private void initUi()
	{
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(createMainPanel(),BorderLayout.CENTER);
		this.getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
		
	}

	
	public final void start(SwingCancelableRunnable aActionRunnable) 
	{
		
		this.actionThread = new SwingCancelableThread("WaitDialog", aActionRunnable);
		this.actionThread.addListener(new SwingCancelableThread.SwingActionThreadListener()
		{
			public void threadChanged(SwingActionThreadEvent aEvent) {
				setMessageText(actionThread.getCurrentStep());
				if( actionThread.hasFinished()) {
					waitPanel.stop();
					SwingCancelableWaitDialog.this.dispose();
				}
			}
		}) 	;	

		waitPanel.start();
		actionThread.start();
		pack();
		SwingCancelableWaitDialog.this.setVisible(true);
	}
	
	
	private JPanel createMainPanel()
	{
		final GridBagConstraints 	col1LblConstr = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1,1
				,1.0, 1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH
				, new Insets(5,10,0,5),0,0 );
		
		JPanel mainPnl = new JPanel(new GridBagLayout());
		
		mainPnl.add(this.messageLabel, col1LblConstr);
		col1LblConstr.fill = GridBagConstraints.NONE; col1LblConstr.weightx = 0.0; col1LblConstr.weighty = 0.0;
		mainPnl.add(this.waitPanel, col1LblConstr);

		final GridBagConstraints 	glueConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,1,1
				,0.0, 1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.VERTICAL
				, new Insets(5,0,0,5),0,0 );
		mainPnl.add(Box.createVerticalGlue(), glueConstr);

		mainPnl.setBorder( BorderFactory.createCompoundBorder(
		BorderFactory.createEmptyBorder(3,3,3,3)
		,BorderFactory.createEtchedBorder())
		);
		return mainPnl;
	}

	private JPanel createStatusBar()
	{
		final GridBagConstraints 	col1LblConstr = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,1,1
				,0.0, 0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE
				, new Insets(5,10,0,5),0,0 );
		
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(new JButton(createCancelAction()), col1LblConstr);
		statusPnl.add(Box.createVerticalStrut(20), col1LblConstr);
		

		return statusPnl;
	}
	
	@SuppressWarnings("serial")
	private AbstractAction createCancelAction()
	{
		//@TODO add multi language 
		return new AbstractAction("Abbrechen")
		{
			public void actionPerformed(ActionEvent e) {
				actionThread.cancel();
			}
		};
	}
	
	/**
	 * 
	 */
	private void setMessageText(final String newText) {
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
				messageLabel.setText(newText);
				messageLabel.invalidate();
				messageLabel.repaint();
				pack();
			}
		});
	}

	public static SwingCancelableWaitDialog createDialog(JComponent parentComponent, String title, boolean modal)
	{
        Frame window = JOptionPane.getFrameForComponent(parentComponent);
        
        SwingCancelableWaitDialog newDialog = new SwingCancelableWaitDialog(window, title, modal);  
        newDialog.setLocationRelativeTo(parentComponent);
        return newDialog;
	}
}
