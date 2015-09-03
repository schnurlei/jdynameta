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
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterDateTextfield implements FilterEditorComponent 
{
	
	private final JSpinner  dateSpin;
	private final JSpinner  timeSpin;
	private JPanel  dateTimePnl;
	private InputChangedListener inputChangedListener;
	
	public FilterDateTextfield() 
	{
		super();
		
		this.dateTimePnl = new JPanel(new GridBagLayout());
		this.dateSpin = createDateJSpinner(); 		
		this.timeSpin = createTimeJSpinner(); 	
		GridBagConstraints constr = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1,1
													,0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE
													, new Insets(0,0,0,5), 0,0	);
//		this.dateSpin.setPreferredSize(new Dimension(100,this.dateSpin.getPreferredSize().height));
//		this.dateSpin.setMinimumSize(dateSpin.getPreferredSize());
//		this.timeSpin.setPreferredSize(new Dimension(80,this.dateSpin.getPreferredSize().height));
//		this.timeSpin.setMinimumSize(dateSpin.getPreferredSize());
		dateTimePnl.add(this.dateSpin, constr);
		dateTimePnl.add(this.timeSpin, constr);
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.weightx = 1.0;
		dateTimePnl.add(Box.createHorizontalGlue(), constr);
		
		
		ChangeListener changeListener  = createSpinnerChangeListener();
		this.dateSpin.addChangeListener(changeListener);
		this.timeSpin.addChangeListener(changeListener);

		addInsertCurrentDateActionToSpinner();
		
	}

	public void markFieldAsChanged(Color markerColor) 
	{
		for (Component curComponent : dateSpin.getEditor().getComponents()) {
			curComponent.setBackground(markerColor);
		}

		for (Component curComponent : timeSpin.getEditor().getComponents()) {
			curComponent.setBackground(markerColor);
		}
		
		this.dateSpin.setBackground(markerColor);
		this.dateSpin.getEditor().setBackground(markerColor);
		this.timeSpin.setBackground(markerColor);
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
		
		dateSpin.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) insertCurrentDateAct.getValue(AbstractAction.ACCELERATOR_KEY),"insertDate");
		dateSpin.getActionMap().put("insertDate", insertCurrentDateAct);
		timeSpin.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) insertCurrentDateAct.getValue(AbstractAction.ACCELERATOR_KEY),"insertDate");
		timeSpin.getActionMap().put("insertDate", insertCurrentDateAct);
		
	
		dateTimePnl.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) insertCurrentDateAct.getValue(AbstractAction.ACCELERATOR_KEY),"insertDate");
		dateTimePnl.getActionMap().put("insertDate", insertCurrentDateAct);
		
	}

	public void setDateInSpinners(Date aDateToSet)
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
		try {
			dateSpin.commitEdit();
			timeSpin.commitEdit();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date result = null;
		if( this.dateSpin.getValue() == null ) {
			result = null;
		} else {
			// set start date to start of day
			Calendar calDate = null;
			Calendar calTime = null;
				
			if ( this.dateSpin.getValue()  != null ) {
				calDate =  Calendar.getInstance();
				calDate.setTime( (Date) this.dateSpin.getValue() );
			}
			
			if ( this.timeSpin.getValue()  != null ) {
				calTime = Calendar.getInstance();
				calTime.setTime( (Date) this.timeSpin.getValue() );
			}
	
			// set time values in Date
			if( calDate != null) {
				if( calTime != null) {
					calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
					calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
					calDate.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
					calDate.set(Calendar.MILLISECOND, 0);
				} else {
					calDate.set(Calendar.HOUR_OF_DAY, 0);
					calDate.set(Calendar.MINUTE, 0);
					calDate.set(Calendar.SECOND, 0);
					calDate.set(Calendar.MILLISECOND, 0);
				}
				result = calDate.getTime();
			} else if(  calTime != null){
				// use Today if calDate == null
				calDate =  Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
				calDate.set(Calendar.MILLISECOND, 0);
				result = calDate.getTime();
			}
		}
	
		return result;
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
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
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
		SpinnerDateModel model = new NullableSpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
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
		SpinnerDateModel model = new NullableSpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
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

	public Object getValue()
	{

		Date insertedDate = getDateInSpinners();
		return  insertedDate;
	}
	
	public void setValue(Object newValue)
	{
		if( newValue instanceof Date) {
			setDateInSpinners( ((Date) newValue));
		}
	}
	
	/**
	 * @param listener
	 */
	public void setInputChangedListener(InputChangedListener listener)
	{
		inputChangedListener = listener;
	}

	
	/**
	 * mostly a copy of SpinnerDateModel but it allows null as value
	 * @author Rainer Schneider
	 *
	 */
	class NullableSpinnerDateModel extends  SpinnerDateModel
	{
	    private Comparable start, end;
	    private Calendar value;
	    private int calendarField;


	    private boolean calendarFieldOK(int calendarField) {
			switch(calendarField) {
			case Calendar.ERA:
			case Calendar.YEAR:
			case Calendar.MONTH:
			case Calendar.WEEK_OF_YEAR:
			case Calendar.WEEK_OF_MONTH:
			case Calendar.DAY_OF_MONTH:
			case Calendar.DAY_OF_YEAR:
			case Calendar.DAY_OF_WEEK:
			case Calendar.DAY_OF_WEEK_IN_MONTH:
			case Calendar.AM_PM:
			case Calendar.HOUR:
			case Calendar.HOUR_OF_DAY:
			case Calendar.MINUTE:
			case Calendar.SECOND:
			case Calendar.MILLISECOND:
			    return true;
			default:
			    return false;
			}
	    }
	    

	    /**
	     */
	    public NullableSpinnerDateModel(Date value, Comparable start, Comparable end, int calendarField) 
	    {
			if (!calendarFieldOK(calendarField)) {
			    throw new IllegalArgumentException("invalid calendarField");
			}
			if (!(((start == null) || (start.compareTo(value) <= 0)) && 
			      ((end == null) || (end.compareTo(value) >= 0)))) {
			    throw new IllegalArgumentException("(start <= value <= end) is false");
			}
			this.value = Calendar.getInstance();
			this.start = start;
			this.end = end;
			this.calendarField = calendarField;
	
			this.value.setTime(value);
	    }


	    /**
	     * Constructs a <code>SpinnerDateModel</code> whose initial
	     * <code>value</code> is the current date, <code>calendarField</code>
	     * is equal to <code>Calendar.DAY_OF_MONTH</code>, and for which 
	     * there are no <code>start</code>/<code>end</code> limits.
	     */
	    public NullableSpinnerDateModel() 
	    {
	    	this(new Date(), null, null, Calendar.DAY_OF_MONTH);
	    }


	    /**
	     */
	    public void setStart(Comparable start) 
	    {
			if ((start == null) ? (this.start != null) : !start.equals(this.start)) {
			    this.start = start;
			    fireStateChanged();
			}
	    }


	    /**
	     */
	    public Comparable getStart() 
	    {
	    	return start;
	    }


	    /** 
	     */
	    public void setEnd(Comparable end) 
	    {
			if ((end == null) ? (this.end != null) : !end.equals(this.end)) {
			    this.end = end;
			    fireStateChanged();
			}
	    }

	    
	    /**
	     * Returns the last <code>Date</code> in the sequence.
	     * 
	     * @return the value of the <code>end</code> property
	     * @see #setEnd
	     */
	    public Comparable getEnd() 
	    {
	    	return end;
	    }


	    /**
	     * Changes the size of the date value change computed
	     * by the <code>nextValue</code> and <code>previousValue</code> methods.
	     * The <code>calendarField</code> parameter must be one of the 
	     * <code>Calendar</code> field constants like <code>Calendar.MONTH</code> 
	     * or <code>Calendar.MINUTE</code>.
	     * The <code>nextValue</code> and <code>previousValue</code> methods
	     * simply move the specified <code>Calendar</code> field forward or backward 
	     * by one unit with the <code>Calendar.add</code> method.
	     * You should use this method with care as some UIs may set the
	     * calendarField before commiting the edit to spin the field under
	     * the cursor. If you only want one field to spin you can subclass
	     * and ignore the setCalendarField calls.
	     * 
	     * @param calendarField one of 
	     *  <ul>
	     *    <li><code>Calendar.ERA</code>
	     *    <li><code>Calendar.YEAR</code>
	     *    <li><code>Calendar.MONTH</code>
	     *    <li><code>Calendar.WEEK_OF_YEAR</code>
	     *    <li><code>Calendar.WEEK_OF_MONTH</code>
	     *    <li><code>Calendar.DAY_OF_MONTH</code>
	     *    <li><code>Calendar.DAY_OF_YEAR</code>
	     *    <li><code>Calendar.DAY_OF_WEEK</code>
	     *    <li><code>Calendar.DAY_OF_WEEK_IN_MONTH</code>
	     *    <li><code>Calendar.AM_PM</code>
	     *    <li><code>Calendar.HOUR</code>
	     *    <li><code>Calendar.HOUR_OF_DAY</code>
	     *    <li><code>Calendar.MINUTE</code>
	     *    <li><code>Calendar.SECOND</code>
	     *    <li><code>Calendar.MILLISECOND</code>
	     *  </ul>
	     * <p>
	     * This method fires a <code>ChangeEvent</code> if the
	     * <code>calendarField</code> has changed.
	     * 
	     * @see #getCalendarField
	     * @see #getNextValue
	     * @see #getPreviousValue
	     * @see Calendar#add
	     * @see #addChangeListener
	     */
	    public void setCalendarField(int calendarField) 
	    {
			if (!calendarFieldOK(calendarField)) {
			    throw new IllegalArgumentException("invalid calendarField");
			}
			if (calendarField != this.calendarField) {
			    this.calendarField = calendarField;
			    fireStateChanged();
			}
	    } 


	    /**
	     */
	    public int getCalendarField() 
	    {
	    	return calendarField;
	    }


	    /**
	     */
	    public Object getNextValue() 
	    {
	    	if( value == null) {
	    		return end;
	    	} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(value.getTime());
				cal.add(calendarField, 1);
				Date next = cal.getTime();
				return ((end == null) || (end.compareTo(next) >= 0)) ? next : null;
	    	}
	    }


	    /**
	     */
	    public Object getPreviousValue() 
	    {
	    	if( value == null) {
	    		return start;
	    	} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(value.getTime());
				cal.add(calendarField, -1);
				Date prev = cal.getTime();
				return ((start == null) || (start.compareTo(prev) <= 0)) ? prev : null;
	    	}
	    }


	    /**
	     * Returns the current element in this sequence of <code>Date</code>s.
	     * This method is equivalent to <code>(Date)getValue</code>.
	     *
	     * @return the <code>value</code> property
	     * @see #setValue
	     */
	    public Date getDate()
	    {
	    	return value == null ? null : value.getTime();
	    }


	    /**
	     * Returns the current element in this sequence of <code>Date</code>s.
	     * 
	     * @return the <code>value</code> property
	     * @see #setValue
	     * @see #getDate
	     */
	    public Object getValue() 
	    {
	    	return (value == null) ? null : value.getTime();
	    }


	    public void setValue(Object aValue) 
	    {
	    	if( aValue == null) {
	    		if( this.value != null ) {
	    			this.value = null;
	    			fireStateChanged();
	    		}
	    	}else {
		    	if (!(aValue instanceof Date)) {
				    throw new IllegalArgumentException("illegal value");
				}
				if ( value == null || !value.equals(this.value.getTime())) {
					if(value == null) {
						this.value = Calendar.getInstance();
					}
				    this.value.setTime((Date)aValue);
				    fireStateChanged();
				}
	    	}
	    }
}
	
	
 	

}

