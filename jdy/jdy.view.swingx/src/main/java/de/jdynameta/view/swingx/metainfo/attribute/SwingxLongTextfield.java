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
package de.jdynameta.view.swingx.metainfo.attribute;

import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.im.InputContext;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

import de.jdynameta.metainfoview.attribute.AttributeComponent;
import de.jdynameta.view.icons.Java2DIconFactory;

/**
 *
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public abstract class SwingxLongTextfield implements AttributeComponent 
{
	private final JTextField  attributeTextField;
	private RepaintableLayerUI<JComponent> layerUI;
	protected Object objectToEdit;
	protected boolean isEditable;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	private boolean isNullable;	
	private final long maxInputValue;
	private final long minInputValue;
	private final Number internMax;
	private final Number internMn;
	private final int multiplier;
	private boolean isDirty;
	
	private volatile boolean fireChangedEvent;
	private NumberFormatter formatter;
	
	private Number originalLong;
	
	public SwingxLongTextfield(Locale aLocale, long aMaxInputValue) 
	{
		this(aLocale, 0, aMaxInputValue, 1);
	}
	public SwingxLongTextfield(Locale aLocale, long aMinInputValue,long aMaxInputValue, int aMultiplier) 
	{
		super();
		assert(aMultiplier >0);
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		this.maxInputValue = aMaxInputValue;
		this.minInputValue = aMinInputValue;
		 if( aMultiplier == 1) {
			internMn = new Long(aMinInputValue);
			internMax = new Long(aMaxInputValue);
	    } 	else {
			internMn = new Double((double)getMinInputValue()/(double)aMultiplier);
			internMax = new Double((double)getMaxInputValue()/(double)aMultiplier);
	    }
		
		this.isDirty = false; 
		this.multiplier = aMultiplier;
		objectToEdit = null;
		isEditable = true;		
		isNullable = true;
	
		attributeTextField = createTextField(); 		

		this.fireChangedEvent = true;
		updateComponentEditable();
	}

	/**
	 * @param listener
	 */
	@Override
	public void addInputChangedListener(InputChangedListener listener)
	{
		inputChangedListenerColl.add(listener);
	}

	@Override
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
	    format.setDecimalSeparatorAlwaysShown(false);
	    format.setMaximumFractionDigits((int)Math.log10(multiplier));
	    format.setMinimumFractionDigits((int)Math.log10(multiplier));
	    format.setGroupingUsed(true);
	    
	    this.formatter = new NumberFormatter(format);
	    
//	    {
//	    	@Override
//	    	public Object stringToValue(String text) throws ParseException 
//	    	{
//	    		if( text == null || text.trim().length() == 0 ) {
//	    			return null;
//	    		} else if (  text.equals("-")) {
//	    			return SignedNull.NEGATIVE_NULL;
//	    		}else {
//	    			 return super.stringToValue(text);
//	    		}
//	    	}
//	    	
//	    	@Override
//	    	public String valueToString(Object value) throws ParseException {
//	    		
//	    		if( value == SignedNull.NEGATIVE_NULL) {
//	    			return "-";
//	    		} else {
//	    			return super.valueToString(value);
//	    		}
//	    	}
//	    	
//	    };
	    
	    formatter.setAllowsInvalid(false);
	    
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
						setValueInTextField(getValidValue());
					} else if ( e.getID() == FocusEvent.FOCUS_GAINED && isEditable) {
						this.select(0, getText().length());
					}
				}
			}
	    	
	    };
	    	    
	    textField.setHorizontalAlignment(JTextField.RIGHT);
	    textField.setToolTipText("" + format.format(internMn) + " ... " + format.format(internMax));
	    
        // create custom LayerUI
        layerUI = new RepaintableLayerUI<JComponent>() 
        {
			@Override
			protected void paintLayer(Graphics2D g2, JXLayer<? extends JComponent> l) 
			{
				super.paintLayer(g2, l);
				if( !hasValidInput()) {
			        BufferedImage image = Java2DIconFactory.createErrorIcon();
			        g2.drawImage(image, 0,l.getHeight() - 8, null);
				}
			}
			        	
        };
	    
	    
	    textField.getDocument().addDocumentListener(new DocumentListener() 
		{
			public void changedUpdate(DocumentEvent e) 
			{
			}

			public void insertUpdate(DocumentEvent e) 
			{
				if( fireChangedEvent ) {
					isDirty = true;
					layerUI.setDirty(true);
					fireInputHasChanged();
				}
			}

			public void removeUpdate(DocumentEvent e) 
			{
				if( fireChangedEvent ) {
					isDirty = true;
					layerUI.setDirty(true);
					fireInputHasChanged();
				}
			}
		});
	    	    
	    return textField;
	}
	
	public long getMinInputValue()
	{
		return minInputValue;
	}
	
	
	public long getMaxInputValue()
	{
		return maxInputValue;
	}
	
	@Override
	public void addToContainer(Container aContainer, Object constraints) 
	{
	    JXLayer<JComponent> layer = new JXLayer<JComponent>(attributeTextField);
        layer.setUI(layerUI);
		
		aContainer.add(layer, constraints);	
	}

	@Override
	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {

			Number insertedNumber = (Number) getValidValue();
			if (insertedNumber != null) { 
				originalLong = new Long( (long)(insertedNumber.doubleValue()*multiplier));
				setLongInObject(objectToEdit, (Long)originalLong);
			} else {
				originalLong = null;
				setLongInObject(objectToEdit, (Long)originalLong);
			}
		}
		isDirty = false;
		
	}

	
	protected Number getValueFromTextfield() throws ParseException
	{
		Number result = null;
		
		if( !attributeTextField.getText().isEmpty()) {
			result =  (Number) formatter.stringToValue(attributeTextField.getText());
		} 
		if( result != null) {
			if( internMn instanceof Long ) {
				result = new Long(result.longValue());
			} else {
				result = new Double(result.doubleValue());
			}
		}

		return result;
	}
	
	protected Number getValidValue()
	{
		Number result;
		try {
			result = getValueFromTextfield();
		} catch (ParseException e) {
			result = null;
		}
		
		if( result != null && result instanceof Comparable ) {
			if ( ((Comparable) result).compareTo(internMn) < 0 ) {
				result = internMn;
			}
			if (  ((Comparable) result).compareTo(internMax) > 0 ) {
				result = internMax;
			}
		}
		
		return result;
	}
	

	protected void setValueInTextField(Number newValue)
	{
		try {
			this.fireChangedEvent = false;
			this.attributeTextField.setText(formatter.valueToString(newValue));
		} catch (ParseException e) {
			this.attributeTextField.setText("");
		} finally {
			this.fireChangedEvent = true;			
		}
	}

	public void setCurrentValue(Long newValue)
	{
		try {
			if (newValue!=null)
				this.attributeTextField.setText(formatter.valueToString(newValue/multiplier));
			else
				this.attributeTextField.setText(null);
		} catch (ParseException e) {
			this.attributeTextField.setText("");
		}
		isDirty = false;
	}

	
	@Override
	public void readValueFromObject(Object anObject)
	{		
		objectToEdit = anObject;
		Number aLong = null;
		if (anObject != null) {
			originalLong = getLongFromObject(anObject);
			if( originalLong != null ) {
				aLong = new Double( (double)originalLong.longValue()/(double)multiplier);
			} 
		} else {
			originalLong = null;
		}
		
		setValueInTextField(aLong);
//		attributeTextField.setText( attributeTextField.getText());
		updateComponentEditable();

		isDirty = false;
	}

	@Override
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
		Number value;
		try {
			value = getValueFromTextfield();
			
			return ( value == null && isNullable) || isValueInRange(value);
		} catch (ParseException e) {
			return false;
		}		
	}
	private boolean isValueInRange(Number value)
	{
		return value == null || ( ((Comparable) value).compareTo(internMn) >= 0 ) && (((Comparable) value).compareTo(internMax) <= 0 );
	}
	
	@Override
	public boolean isDirty()
	{
//		if( !this.isDirty ) {
//			boolean isDirtyTemp = false;
//			Long actLong = (getValue() == null) ? null : new Long ( (long) (((Number) getValue()).doubleValue()*(double)multiplier));				
//			if ((actLong == null && originalLong != null) || 
//					(actLong != null && originalLong == null) ||
//					(actLong != null && originalLong != null && !(actLong.longValue() == originalLong.longValue())) ) {
//				isDirtyTemp = true;
//			}
//			this.isDirty = isDirtyTemp;
//		}
		
		return this.isDirty;
	}


	/**
	 * @param b
	 */
	public void setNullable(boolean aNullableFlag)
	{
		isNullable = aNullableFlag;	
	}

	@Override
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

	
	protected abstract Long getLongFromObject(Object anObject);
	protected abstract void setLongInObject(Object anObject, Long insertedDate);

	/**
	 * Hack to allow a single - sign as valid input
	 * @author Rainer Schneider
	 *
	 */
	private static class SignedNull extends Number
	{
		public static SignedNull POSITIVE_NULL = new SignedNull(true);
		public static SignedNull NEGATIVE_NULL = new SignedNull(false);
		
		private boolean isPositive;
		
		public SignedNull(boolean aIsPositive) 
		{
			this.isPositive = aIsPositive;
		}
		
		public boolean isPositive()
		{
			return isPositive;
		}
		
		@Override
		public double doubleValue() {
			return 0;
		}

		@Override
		public float floatValue() {
			return 0;
		}

		@Override
		public int intValue() {
			return 0;
		}

		@Override
		public long longValue() {
			return 0;
		}
	}
	
	private static class RepaintableLayerUI<V extends JComponent> extends AbstractLayerUI<V>
	{
		@Override
		public void setDirty(boolean isDirty) {
			super.setDirty(isDirty);
		}
	}
	
}

