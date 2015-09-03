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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jdynameta.view.base.ManagedPanel;



/**
 * Panel hold which can be checked, whether it can be closed
 * 
 * @author rs 26.05.2003
 *
 */
@SuppressWarnings("serial")
public abstract class OkPanel extends ManagedPanel
{
	private OkPanelListener okPanelListener;
	
	
	/**
	 * 
	 */
	public OkPanel()
	{
		super();
	}

	/**
	 * checked whether the panel is allowed to be closed, depending on its state
	 * @return
	 */
	public boolean canClose()
	{
		return true;	
	}


	/**
	 * Called when the panel is committed by the user
	 * Could be overwritten by subclasses
	 * @exception CloseVetoException thrown when the panel must not be closed
	 */
	public void close() throws CloseVetoException
	{

	}

	/**
	 * add an Listener which is notified when the canClose state ahs changed
	 * @param aOkPanelListener
	 */
	public void setOkPanelListener(OkPanelListener aOkPanelListener)
	{
		okPanelListener = aOkPanelListener;
	}

	/**
	 * Called by subclasses when the can close state has changed
	 *
	 */
	protected void fireCanCloseChanged()
	{
		if( okPanelListener != null) {
			okPanelListener.canCloseChanged();	
		}	
	}


	protected class DlgDocumentListener implements DocumentListener
	{
		public DlgDocumentListener(){ super(); }
		
		public void changedUpdate(DocumentEvent e) 
		{
			fireCanCloseChanged();
		}
	
		public void insertUpdate(DocumentEvent e) 
		{
			fireCanCloseChanged();
		}
		
		public void removeUpdate(DocumentEvent e) 
		{
			fireCanCloseChanged();
		}
	}

	/**
	 * Listener which is notified when the canClose state of its Panel has changed
	 * @author <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
	 *
	 */
	public static interface OkPanelListener
	{
		public void canCloseChanged();	
	}

}