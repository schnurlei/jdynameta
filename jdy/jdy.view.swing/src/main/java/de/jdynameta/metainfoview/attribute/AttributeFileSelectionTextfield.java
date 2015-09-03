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
package de.jdynameta.metainfoview.attribute;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 * Attribute component to select a Filename into a Textfield
 * @author Rainer Schneider
 *
 */
public abstract class AttributeFileSelectionTextfield implements AttributeComponent 
{
	private static File lastSelectedFile = null;
	private JTextField attributeTextField;
	private JButton fileSelectionBtn;
	private JPanel  selectionPnl;

	protected Object objectToEdit;
	protected boolean isEditable;
	private boolean isDirty;
	private boolean isNullable; 	
	private final ArrayList<InputChangedListener> inputChangedListenerColl;

	/**
	 * 
	 */
	public AttributeFileSelectionTextfield(long maxLength) 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		objectToEdit = null;
		isEditable = true;
		isNullable = true;
		this.attributeTextField = new JTextField();
		this.attributeTextField.setEditable(false);
		this.fileSelectionBtn = new JButton(createFileSelectionAction());
		this.selectionPnl = new JPanel(new BorderLayout());
		selectionPnl.add(this.attributeTextField, BorderLayout.CENTER);
		selectionPnl.add(this.fileSelectionBtn, BorderLayout.LINE_END);
		
		AbstractDocument doc = (AbstractDocument)attributeTextField.getDocument();		
		doc.setDocumentFilter( new AttributeDocumentFilter(maxLength));
		
		isDirty = false;
		attributeTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				setIsDirty(true); 
			}
			public void insertUpdate(DocumentEvent e)
			{
				setIsDirty(true); 
			}
			public void removeUpdate(DocumentEvent e)
			{
				setIsDirty(true); 
			}
		});
		updateComponentEditable();
	}


	/* (non-Javadoc)
	 * @see com.siemens.cdscms.client.ui.attribute.AttributeComponent#addToContainer(java.awt.Container, java.lang.Object)
	 */
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(selectionPnl, constraints);	
		
	}

	protected JTextField getAttributeTextField() 
	{
		return attributeTextField;
	}
	
	public void addSelectionChangeListener(ActionListener selectionChangeListener)
	{
		attributeTextField.addActionListener(selectionChangeListener);
	}

	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {
			setStringInObject(objectToEdit,attributeTextField.getText());
		}
		isDirty = false;
	}


	public void readValueFromObject(Object anObject)
	{
		objectToEdit = anObject;
		String aString = null;
		if (anObject != null) {
			aString = getStringFromObject(anObject);
		}
		attributeTextField.setText(aString);
		updateComponentEditable();
		isDirty = false;
	}

	public void setEditable(boolean editable)
	{
		isEditable = editable;
		updateComponentEditable();
	}

	protected void updateComponentEditable()
	{
		if( attributeTextField  != null) {
			fileSelectionBtn.setEnabled(isEditable 
										&& objectToEdit != null);
		}
	}

	/* (non-Javadoc)
	 */
	public boolean hasValidInput()
	{
		return isNullable || (attributeTextField.getText() != null && attributeTextField.getText().length() > 0);
	}

	/* (non-Javadoc)
	 */
	public boolean isDirty()
	{
		return isDirty;
	}

	/* (non-Javadoc)
	 */
	protected void setIsDirty(boolean aFlag)
	{
		isDirty = aFlag;
		fireInputHasChanged();	
	}


	/**
	 * @param b
	 */
	public void setNullable(boolean aNullableFlag)
	{
		isNullable = aNullableFlag;	
	}



	public void addLabelToContainer(String aLabelText, Container aContainer, Object constraints )
	{
		JLabel newLabel = new JLabel(aLabelText +":");
//		URL url = AttributeStringTextfield.class.getResource("text_rs.png");
//		if( url != null) {
//			ImageIcon image = new ImageIcon(url);
//			newLabel.setIcon(image);
//		}
		newLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		newLabel.setVerticalTextPosition(SwingConstants.CENTER);
		newLabel.setIconTextGap(1);
		newLabel.setLabelFor(this.attributeTextField);
	
		if( !isNullable) {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.BOLD) );
		} else {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.PLAIN) );
		}
	
		aContainer.add(newLabel, constraints);	
	}

	@SuppressWarnings("serial")
	protected Action createFileSelectionAction()
	{
		return new AbstractAction("...") {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				selectFile();
			}
		};
	}
	
	protected void selectFile()
	{
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(lastSelectedFile);

		int returnVal = fileChooser.showOpenDialog(this.selectionPnl);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			lastSelectedFile = fileChooser.getSelectedFile();
			this.attributeTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		 }	
	}
	
	/**
	 * @param listener
	 */
	public void addInputChangedListener(InputChangedListener listener)
	{
		inputChangedListenerColl.add(listener);
	}

	public void removeInputChangedListener(InputChangedListener listener) {
		inputChangedListenerColl.remove(listener);
	}
	
	protected void fireInputHasChanged()
	{
		for (InputChangedListener curListener : inputChangedListenerColl) {
			curListener.inputHasChanged();
		}
	}
	
	protected abstract String getStringFromObject(Object anObject);
	protected abstract void setStringInObject(Object anObject, String insertedDate);

}
