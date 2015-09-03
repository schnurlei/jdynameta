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
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.CurrencyType;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterCurrencyTextfield implements FilterEditorComponent 
{
	private JFormattedTextField  attributeTextField;
	private InputChangedListener inputChangedListener;
	
	
	public FilterCurrencyTextfield( PrimitiveAttributeInfo aCurrencyAttributeInfo) 
	{
		super();
		assert(aCurrencyAttributeInfo.getType() instanceof CurrencyType);
		
		
		attributeTextField = createTextField(); 		
		if ( aCurrencyAttributeInfo != null ) {
			attributeTextField.setName("filter_"+aCurrencyAttributeInfo.getInternalName());
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
		
		attributeTextField.setName("Filter_"+aCurrencyAttributeInfo.getInternalName());
		attributeTextField.getAccessibleContext().setAccessibleName("Filter_"+aCurrencyAttributeInfo.getInternalName() +"_Access");
		
	}

	public void markFieldAsChanged(Color markerColor) 
	{
		this.attributeTextField.setBackground(markerColor);
	}
	
	/**
	 * creates an JFormattedTextField which is used for an TableCellEditor
	 * @return
	 */
	public JFormattedTextField createTextField()
	{
	    DecimalFormat format = new DecimalFormat();
	    format.setDecimalSeparatorAlwaysShown(false);
	    format.setMaximumFractionDigits(2);
	    format.setGroupingUsed(false);
	    format.setMinimumFractionDigits(2);
	    
	    
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setAllowsInvalid(false);
	    formatter.setMinimum(new Double(0));
	    formatter.setMaximum(new Double(getMaxInputValue()));
	    
	    final JFormattedTextField textField = new JFormattedTextField(formatter);
	    	    
	    textField.setHorizontalAlignment(JTextField.RIGHT);
	    	    
	    return textField;
	}
	
	protected double getMaxInputValue()
	{
		return 9999999999.99;
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeTextField, constraints);	
	}

	public Object getValue()
	{
		Number insertedNumber = (Number) attributeTextField.getValue();
		BigDecimal decimal = null; 
		if (insertedNumber != null) { 
			decimal = new BigDecimal(insertedNumber.doubleValue());
		} else {
		}
		return decimal;
	}
	
	public void setValue(Object newValue)
	{
		if( newValue instanceof BigDecimal) {
			attributeTextField.setValue( ((BigDecimal) newValue));
		}
	}

	
	public boolean hasValidInput()
	{
		return attributeTextField.isEditValid();
	}
	

	/**
	 * @param listener
	 */
	public void setInputChangedListener(InputChangedListener listener)
	{
		inputChangedListener = listener;
	}

}

