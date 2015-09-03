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

import java.awt.Container;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.im.InputContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AttributeDoubleTextfield implements AttributeComponent 
{
	private final JTextField  attributeTextField;
	protected Object objectToEdit;
	protected boolean isEditable;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	private final Double maxInputValue;
	private final Double minInputValue;
	private long scale;
	private boolean isDirty;
	private boolean isNullable;	
	private volatile boolean fireChangedEvent;
	
	private NumberFormatter formatter;
	private Double originalValue;
	
	public AttributeDoubleTextfield(Locale aLocale, Double aMaxInputValue, long aScale) 
	{
		this(aLocale, new Double(0), aMaxInputValue, aScale);
	}

	public AttributeDoubleTextfield(Locale aLocale, Double aMinInputValue, Double aMaxInputValue, long aScale) 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		
		this.isDirty = false; 
		this.objectToEdit = null;
		this.isEditable = true;		
		this.isNullable = true;
		this.minInputValue = aMinInputValue;
		this.maxInputValue= aMaxInputValue;
		this.scale = aScale;
		
		attributeTextField = createTextField(); 		

		attributeTextField.getDocument().addDocumentListener(new DocumentListener() 
		{
			public void changedUpdate(DocumentEvent e) 
			{
			}

			public void insertUpdate(DocumentEvent e) 
			{
				if( fireChangedEvent ) {
					isDirty = true;
					fireInputHasChanged();
				}
			}

			public void removeUpdate(DocumentEvent e) 
			{
				if( fireChangedEvent ) {
					isDirty = true;
					fireInputHasChanged();
				}
			}
		});
		updateComponentEditable();
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
	  
	protected JTextField getAttributeTextField() 
	{
		return attributeTextField;
	}
	
	/**
	 * creates an JFormattedTextField which is used for an TableCellEditor
	 * @return
	 */
	public JTextField createTextField()
	{
	    DecimalFormat format = new DecimalFormat();
	    format.setDecimalSeparatorAlwaysShown(true);
	    format.setMaximumFractionDigits((int)scale);
	    format.setGroupingUsed(true);
	    format.setMinimumFractionDigits((int)scale);
	    
	    
	    this.formatter = new NumberFormatter(format)
	    {
	    	@Override
	    	public Object stringToValue(String text) throws ParseException 
	    	{
	    		return (text == null || text.trim().length() == 0) ? null : super.stringToValue(text);
	    	}
	    };
	    formatter.setAllowsInvalid(true);
//	    formatter.setMinimum(new Double(minInputValue.doubleValue()));
//	    formatter.setMaximum(new Double(maxInputValue.doubleValue()));

	    
	    final JTextField textField = new JTextField()
	    {
	    	@Override
	    	public void replaceSelection(String content) {
	    		super.replaceSelection(removeNotNumberChars(content));
	    	}
	    	
	    	private String removeNotNumberChars(String text)
	    	{
	    		StringBuffer newText = new StringBuffer(text.length());
	    		for( int i= 0; i < text.length(); i++) {
	    			if ( Character.isDigit(text.charAt(i)) || text.charAt(i) == '.' || text.charAt(i) == ','
	    				|| text.charAt(i) == '-' || text.charAt(i) == '+') {
	    				newText.append(text.charAt(i));
	    			}
	    		}
	    		
	    		return newText.toString();
	    	}
	    	
	    	  protected void processFocusEvent(FocusEvent e) {
				super.processFocusEvent(e);

				// ignore temporary focus event
				if (!e.isTemporary()) {

					if ( e.getID() == FocusEvent.FOCUS_LOST) {
						InputContext ic = getInputContext();

						// if there is a composed text, process it first
						if ((ic != null) ) {
							ic.endComposition();
						} else {
						}
						setValueInTextField(getValue());
					} else if ( e.getID() == FocusEvent.FOCUS_GAINED && isEditable) {
						this.select(0, getText().length());
					}
				}
			}
	    	
	    };
	    	    
	    textField.setHorizontalAlignment(JTextField.RIGHT);
	    
	    textField.setToolTipText("" + format.format(minInputValue) + " ... " + format.format(maxInputValue));
	    
	    	    
	    return textField;
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeTextField, constraints);	
	}

	
	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {

			Double decimal =  getValue();
			if (decimal != null) { 
				setDoubleInObject(objectToEdit, decimal);
			} else {
				setDoubleInObject(objectToEdit, null);
			}
			originalValue = decimal;
		}
		isDirty = false;
	}
	
	public void readValueFromObject(Object anObject)
	{		
		objectToEdit = anObject;
		Double decimal = null;
		if (anObject != null) {
			decimal = getDoubleFromObject(anObject);
		}
		originalValue = decimal;
		setValueInTextField(decimal == null ? null  :decimal);
		updateComponentEditable();
		isDirty = false;
	}


	
	protected Double getValue()
	{
		Number fieldValue = null;
		try {
			fieldValue =  (Number) formatter.stringToValue(attributeTextField.getText());
		} catch (ParseException e) {
			return null;
		}

		Double actDouble = (fieldValue == null) ? null : new Double ( fieldValue.doubleValue());
	
		return actDouble;
	}
	

	protected void setValueInTextField(Double newValue)
	{
		try {
			this.fireChangedEvent = false;			
			if( newValue == null) {
				this.attributeTextField.setText("");
			} else {
				this.attributeTextField.setText(formatter.valueToString(new Double(newValue.doubleValue())));
			}
		} catch (ParseException e) {
			this.attributeTextField.setText("");
		} finally {
			this.fireChangedEvent = true;			
		}
	}

	

	public void setEditable(boolean editable)
	{
		isEditable = editable;
		updateComponentEditable();
	}

	protected void updateComponentEditable()
	{
		attributeTextField.setEditable(isEditable && objectToEdit != null);
	}

	public boolean hasValidInput()
	{
		return (isNullable || getValue() != null);
	}
	

	public boolean isDirty()
	{
		
//		if( !isDirty ) {
//			boolean tempDirty = false;
//			
//			BigDecimal actDecimal = getValue();
//			
//			if ((actDecimal == null && originalValue != null) || 
//					(actDecimal != null && originalValue == null) ||
//					(actDecimal != null && originalValue != null && !actDecimal.equals(originalValue)) ) {
//				tempDirty = true;
//			}
//			isDirty = tempDirty;
//		}
		
		return isDirty;
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
	
	protected abstract Double getDoubleFromObject(Object anObject);
	protected abstract void setDoubleInObject(Object anObject, Double insertedDate);

	
}

