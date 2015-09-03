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


/**
 * Panel with the behavior to check if its content has changed and
 * which contents could be changed
 * @author Rainer Schneider
 */
public abstract class SavePanel extends OkPanel
{
	private SavePanelListener savePanelListener;

	/**
	 * 
	 */
	public SavePanel()
	{
		super();

	}

	/**
	 * save the content of this panel
	 * @throws SaveException
	 */
	public abstract void save() throws SaveException;	

	public abstract void discardChanges();
	
	/**
	 * 
	 * @return has the content change since the last save?
	 * @throws DeselectVetoException
	 */
	public abstract boolean isDirty() throws DeselectVetoException;	

	@Override
	public boolean canClose() throws DeselectVetoException
	{
		return !isDirty();	
	}

	public void fireIsDirtyChanged()
	{
		if( savePanelListener != null) {
			savePanelListener.isDirtyChanged();	
		}	
	}

	public void setSavePanelListener(SavePanelListener aSavePanelListener)
	{
		super.setOkPanelListener(aSavePanelListener);
		this.savePanelListener = aSavePanelListener;
	}
	

	public static interface SavePanelListener extends OkPanelListener
	{
		public void isDirtyChanged();	
	}
	
	protected class SavePanelDocumentListener implements DocumentListener
	{
			
		public void changedUpdate(DocumentEvent e) 
		{
			fireCanCloseChanged();
			fireIsDirtyChanged();
		}
		
		public void insertUpdate(DocumentEvent e) 
		{
			fireCanCloseChanged();
			fireIsDirtyChanged();
		}
			
		public void removeUpdate(DocumentEvent e) 
		{
			fireCanCloseChanged();
			fireIsDirtyChanged();
		}
	}
	
}