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
package de.jdynameta.metainfoview.filter;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.im.InputContext;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterLongTextfield implements FilterEditorComponent 
{
	private JTextField  attributeTextField;
	private InputChangedListener inputChangedListener;
	private NumberFormatter formatter;
	private long multiplier = 1;
	
	
	public FilterLongTextfield(PrimitiveAttributeInfo attrInfo, long aMaxInputValue, long aMultiplier	) 
	{
		super();
		
		this.multiplier = aMultiplier;
		attributeTextField = createTextField(aMaxInputValue); 		
		if ( attrInfo != null ) {
			attributeTextField.setName("filter_"+attrInfo.getInternalName());
		}
		attributeTextField.getDocument().addDocumentListener(new DocumentListener() 
		{
			public void changedUpdate(DocumentEvent e) 
			{
			}

			public void insertUpdate(DocumentEvent e) 
			{
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
			}

			public void removeUpdate(DocumentEvent e) {
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
			}
		});
		
		attributeTextField.setName("Filter_"+attrInfo.getInternalName());
		attributeTextField.getAccessibleContext().setAccessibleName("Filter_"+attrInfo.getInternalName() +"_Access");
		
	}

	public void markFieldAsChanged(Color markerColor) 
	{
		this.attributeTextField.setBackground(markerColor);
	}
	
	/**
	 * creates an JFormattedTextField which is used for an TableCellEditor
	 * @return
	 */
	public JTextField createTextField(long aMaxInputValue)
	{
		DecimalFormat format = new DecimalFormat();
	    format.setMaximumFractionDigits((int)Math.log10(multiplier));
	    format.setMinimumFractionDigits((int)Math.log10(multiplier));
		format.setDecimalSeparatorAlwaysShown(false);
		format.setGroupingUsed(true);

		this.formatter = new NumberFormatter(format) {
			@Override
			public Object stringToValue(String text) throws ParseException {
				return (text == null || text.trim().length() == 0) ? null
						: super.stringToValue(text);
			}
		};
		formatter.setAllowsInvalid(true);
		// formatter.setMinimum(new Double(minInputValue.doubleValue()));
		// formatter.setMaximum(new Double(maxInputValue.doubleValue()));

		final JTextField textField = new JTextField() {
			@Override
			public void replaceSelection(String content) {
				super.replaceSelection(removeNotNumberChars(content));
			}

			private String removeNotNumberChars(String text) {
				StringBuffer newText = new StringBuffer(text.length());
				for (int i = 0; i < text.length(); i++) {
					if (Character.isDigit(text.charAt(i))
							|| text.charAt(i) == '.' || text.charAt(i) == ','
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

					if (e.getID() == FocusEvent.FOCUS_LOST) {
						InputContext ic = getInputContext();

						// if there is a composed text, process it first
						if ((ic != null)) {
							ic.endComposition();
						} else {
						}
						setValueInTextField(getValueFromTextfield());
					} else if (e.getID() == FocusEvent.FOCUS_GAINED) {
						this.select(0, getText().length());
					}
				}
			}

		};

		textField.setHorizontalAlignment(JTextField.RIGHT);
		
		return textField;
	}
	
	protected void setValueInTextField(Number newValue)
	{
		try {
			this.attributeTextField.setText(formatter.valueToString(newValue));
		} catch (ParseException e) {
			this.attributeTextField.setText("");
		} 
	}
	
	protected double getMaxInputValue()
	{
		return 9999999999.99;
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeTextField, constraints);	
	}

	public Number getValueFromTextfield()
	{
		Number result = null;
		try {
			result =  (Number) formatter.stringToValue(attributeTextField.getText());
			if( result != null) {
				if( multiplier == 1 ) {
					result = new Long(result.longValue());
				} else {
					result = new Double(result.doubleValue());
				}
			}
		} catch (ParseException e) {
			return null;
		}
		
		
		return result;
	}
	
	
	public Object getValue() 
	{
		return (getValueFromTextfield() == null) ? null :  new Long( (long)(getValueFromTextfield().doubleValue()*multiplier));
	}
	
	public void setValue(Object newValue)
	{
		try {
			this.attributeTextField.setText(formatter.valueToString(newValue));
		} catch (ParseException e) {
			this.attributeTextField.setText("");
		} 
	}
		

	public boolean hasValidInput()
	{
		return true;
	}
	

	/**
	 * @param listener
	 */
	public void setInputChangedListener(InputChangedListener listener)
	{
		inputChangedListener = listener;
	}

}

