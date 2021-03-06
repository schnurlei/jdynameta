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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AttributeDateTextfield implements AttributeComponent 
{
	
	private final JSpinner  dateSpin;
	private final JSpinner  timeSpin;
	private JPanel  dateTimePnl;
	private Object objectToEdit;
	private boolean isEditable;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	private boolean isNullable;	
	private boolean isDirty;
	
	private java.util.Date originalDate;
	
	public AttributeDateTextfield(Locale aLocale) 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		objectToEdit = null;
		isEditable = true;		
		isNullable = true;
		
		this.dateTimePnl = new JPanel(new GridBagLayout());
		this.dateSpin = createDateJSpinner(); 		
		this.timeSpin = createTimeJSpinner(); 	
		
		GridBagConstraints constr = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1,1
				,0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE
				, new Insets(0,0,0,5), 0,0	);
		dateTimePnl.add(this.dateSpin, constr);
		dateTimePnl.add(this.timeSpin, constr);
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.weightx = 1.0;
		dateTimePnl.add(Box.createHorizontalGlue(), constr);
		
		
		ChangeListener spinChangeListener  =createSpinnerChangeListener();
		this.dateSpin.addChangeListener(spinChangeListener);
		this.timeSpin.addChangeListener(spinChangeListener);

		updateComponentEditable();
	
		addInsertCurrentDateActionToSpinner();
		this.isDirty = false;
		
	}

	protected JSpinner getDateSpin() 
	{
		return dateSpin;
	}
	
	protected JSpinner getTimeSpin() 
	{
		return timeSpin;
	}
	
	
	private void addInsertCurrentDateActionToSpinner()
	{
		AbstractAction insertCurrentDateAct = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (dateSpin.isEnabled() && dateSpin.isVisible() ) {
					dateSpin.setValue(new java.sql.Date(System.currentTimeMillis()));
				}
				if (timeSpin.isEnabled() && timeSpin.isVisible()) {
					timeSpin.setValue(new java.sql.Date(System.currentTimeMillis()));
				}
				
			}
		};
		insertCurrentDateAct.putValue(AbstractAction.SHORT_DESCRIPTION, "insert current date");
		insertCurrentDateAct.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));	
		
		dateSpin.getInputMap(JComponent.WHEN_FOCUSED)
					.put((KeyStroke) insertCurrentDateAct.getValue(AbstractAction.ACCELERATOR_KEY),"insertDate");
		dateSpin.getActionMap().put("insertDate", insertCurrentDateAct);
		timeSpin.getInputMap(JComponent.WHEN_FOCUSED)
				.put((KeyStroke) insertCurrentDateAct.getValue(AbstractAction.ACCELERATOR_KEY),"insertDate");
		timeSpin.getActionMap().put("insertDate", insertCurrentDateAct);
	}

	private void setDateInSpinners(Date aDateToSet)
	{
		// set start date to start of day
		if ( aDateToSet == null) {
			aDateToSet = new Date();
		}
			
		this.dateSpin.setValue(aDateToSet);
		this.timeSpin.setValue(aDateToSet);
	}

	private Date getDateInSpinners()
	{
		// set start date to start of day
		Calendar calDate = Calendar.getInstance();
		calDate.setTime( (Date) this.dateSpin.getValue() );
		Calendar calTime = Calendar.getInstance();
		calTime.setTime( (Date) this.timeSpin.getValue() );

		if( calDate != null) {
			calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
			calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
			calDate.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
			calDate.set(Calendar.MILLISECOND, 0);
		}
	
		return ( calDate != null ) ? calDate.getTime() : null;
	}

	private ChangeListener createSpinnerChangeListener()
	{
		return new ChangeListener() 
		{

			public void stateChanged(ChangeEvent e)
			{
//				try
//				{
//					dateSpin.commitEdit();
//					timeSpin.commitEdit();
//				} catch (ParseException e1)
//				{
//					e1.printStackTrace();
//				}
				isDirty = true;
				fireInputHasChanged();	
			}
		};
	}

	private JSpinner createDateJSpinner() 
	{
		DateFormatter dateFormatter = new DateFormatter()			
		{
			@Override
			public Object stringToValue(String string) throws ParseException 
			{
				if (string == null || string.length() == 0) {
					return null;
				}
				return super.stringToValue(string);
			}		
		};
		dateFormatter.setFormat(DateFormat.getDateInstance(DateFormat.SHORT));
		// dateFormatter.setOverwriteMode(true);
		dateFormatter.setAllowsInvalid(true);
		SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
		JSpinner createdSpinner = new JSpinner(model);
		JSpinner.DateEditor editor = new JSpinner.DateEditor(createdSpinner);
		editor.getTextField().setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
		createdSpinner.setEditor(editor);
		return createdSpinner;
	}
	
	private JSpinner createTimeJSpinner() 
	{
		DateFormatter dateFormatter = new DateFormatter()			
		{
			@Override
			public Object stringToValue(String string) throws ParseException 
			{
				if (string == null || string.length() == 0) {
					return null;
				}
				return super.stringToValue(string);
			}		
		};
		dateFormatter.setFormat(DateFormat.getTimeInstance(DateFormat.SHORT));
		// dateFormatter.setOverwriteMode(true);
		dateFormatter.setAllowsInvalid(true);
		SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
		JSpinner createdSpinner = new JSpinner(model);
		JSpinner.DateEditor editor = new JSpinner.DateEditor(createdSpinner);
		editor.getTextField().setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
		createdSpinner.setEditor(editor);
		return createdSpinner;
	}

	
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(dateTimePnl, constraints);	
	}

	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {

			Date insertedDate = getDateInSpinners();
			if (insertedDate != null) { 
				setDateInObject(objectToEdit, insertedDate);
			} else {
				setDateInObject(objectToEdit, null);
			}
			originalDate = insertedDate;
		}
		isDirty = false;
	}
	


	public void readValueFromObject(Object anObject)
	{		
		objectToEdit = anObject;
		if (anObject != null) {
			originalDate = getDateFromObject(anObject);
		}
		
		setDateInSpinners(originalDate);
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
		dateSpin.setEnabled(isEditable && objectToEdit != null);
		timeSpin.setEnabled(isEditable && objectToEdit != null);
	}

	public boolean hasValidInput()
	{
		return isNullable || (dateSpin.getValue() != null && timeSpin.getValue() != null);
	}
	
	public boolean isDirty()
	{
//		boolean isDirty = false;
//		Date actDate = getDateInSpinners();				
//		if ((actDate == null && originalDate != null) || 
//				(actDate != null && originalDate == null) ||
//				(actDate != null && originalDate != null && actDate.getTime() != originalDate.getTime())) {
//			isDirty = true;
//		}
		return isDirty;
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
//		URL url = AttributeStringTextfield.class.getResource("./datum_rs.png");
//		ImageIcon image = new ImageIcon(url);
//		newLabel.setIcon(image);
		newLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		newLabel.setVerticalTextPosition(SwingConstants.CENTER);
		newLabel.setIconTextGap(1);
		newLabel.setLabelFor(this.dateTimePnl);
	
		if( !isNullable) {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.BOLD) );
		} else {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.PLAIN) );
		}
	
		aContainer.add(newLabel, constraints);	
			
	}

	protected abstract Date getDateFromObject(Object anObject);
	protected abstract void setDateInObject(Object anObject, Date insertedDate);

}

