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
package de.jdynameta.view.field;


import java.awt.BorderLayout;
import java.awt.Insets;
import java.text.Format;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;


/**	* A field for formatted text input. The field provides input for a specified
	* format and optional leading and trailing labels.
	*
	* @see java.text.Format
	*
	* @version 04/2003
	*/
public class FormattedTextField
	extends JPanel
{
	private boolean initialized = false;

	private final Format format;
	private final JTextField textField;
	private JComponent leadingComponent;
	private JComponent trailingComponent;


/**	* Creates a new text field without a specific format.
	*/
	public FormattedTextField()
	{
		this(null);
	}


/**	* Creates a new formatted text field.
	*
	* @param format		The number format to be used by this field.
	*/
	public FormattedTextField(Format aFormat)
	{
		super(new BorderLayout(0, 0));

		format = aFormat;
		textField = new JTextField();
		//_textField.addFocusListener(new FocusHandler());
		textField.setInputVerifier(new FormattedInputVerifier());

		leadingComponent = null;
		trailingComponent = null;

		add(textField, BorderLayout.CENTER);

		initialized = true;
		configureUI();
	}


	@Override
	public void updateUI()
	{
		super.updateUI();

		if(initialized){
			configureUI();
		}
	}


/**	* Initializes the text component. This method is called after the
	* text component state (e.g. editable state) has changed.
	* This method also invokes {@link #configureLeadingComponentUI()} and
	* {@link #configureTrailingComponentUI()}.
	*/
	protected void configureUI()
	{
		textField.setHorizontalAlignment(JFormattedTextField.TRAILING);

		//_textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		Insets insets = UIManager.getInsets("TextField.margin");
		if(insets != null){
			textField.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
		}else{
			textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		}

		textField.setOpaque(isOpaque());

		setBackground(textField.getBackground());
		setBorder(UIManager.getBorder("TextField.border"));

		configureLeadingComponentUI();
		configureTrailingComponentUI();
	}

	public void setInputVerifierForTextfield(InputVerifier anInputVerifier) 
	{
		textField.setInputVerifier(anInputVerifier);	
	}

/**	* Initializes the leading component. This method is called after the
	* leading component or the text component state (e.g. editable state) has
	* changed.
	*/
	protected void configureLeadingComponentUI()
	{
		if(leadingComponent != null){
			if(leadingComponent instanceof JLabel){
				Insets insets = UIManager.getInsets("TextField.margin");
				if(insets != null){
					leadingComponent.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, 2/*insets.right*/));
				}else{
					leadingComponent.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
				}
			}
			leadingComponent.setOpaque(false);
		}
	}


/**	* Initializes the trailing component. This method is called after the
	* trailing component or the text component state (e.g. editable state) has
	* changed.
	*/
	protected void configureTrailingComponentUI()
	{
		if(trailingComponent != null){
			if(trailingComponent instanceof JLabel){
				Insets insets = UIManager.getInsets("TextField.margin");
				if(insets != null){
					trailingComponent.setBorder(BorderFactory.createEmptyBorder(insets.top, 2/*insets.left*/, insets.bottom, insets.right));
				}else{
					trailingComponent.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
				}
			}
			trailingComponent.setOpaque(false);
		}
	}


/**	* Retrieves the component shown before the text editor field.
	*
	* @return					The component or <tt>null</tt>.
	*/
	public JComponent getLeadingComponent()
	{
		return leadingComponent;
	}


/**	* Sets the component shown before the text editor field.
	*
	* @param component			The component or <tt>null</tt>.
	*/
	public void setLeadingComponent(JComponent component)
	{
		if(leadingComponent != null){
			remove(leadingComponent);
		}
		leadingComponent = component;
		if(leadingComponent != null){
			add(leadingComponent, BorderLayout.WEST);
			configureLeadingComponentUI();
		}
	}


/**	* Retrieves the component shown after the text editor field.
	*
	* @return					The component or <tt>null</tt>.
	*/
	public JComponent getTrailingComponent()
	{
		return trailingComponent;
	}


/**	* Sets the component shown after the text editor field.
	*
	* @param component			The component or <tt>null</tt>.
	*/
	public void setTrailingComponent(JComponent component)
	{
		if(trailingComponent != null){
			remove(trailingComponent);
		}
		trailingComponent = component;
		if(trailingComponent != null){
			add(trailingComponent, BorderLayout.EAST);
			configureTrailingComponentUI();
		}
	}


/**	* Retrieves the format used by this text component.
	*/
	protected Format getFormat()
	{
		return format;
	}


/**	* Retrieves the editor component of this formatted text field.
	*/
	public JTextField getEditorComponent()
	{
		return textField;
	}


/**	* Retrieves the number of columns of the editor component.
	*/
	public int getColumns()
	{
		return textField.getColumns();
	}


/**	* Sets the number of columns of the editor component.
	*/
	public void setColumns(int columns)
	{
		textField.setColumns(columns);
	}


/**	* Enables or disables this component.
	*/
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		textField.setEnabled(enabled);
		if(leadingComponent != null){
			leadingComponent.setEnabled(enabled);
		}
		configureUI();
	}


/**	* Retrieves whether or not this text component is editable.
	*/
	public boolean isEditable()
	{
		return textField.isEditable();
	}


/**	* Sets the flag that determines whether or not this text component is editable.
	*/
	public void setEditable(boolean editable)
	{
		textField.setEditable(editable);
		configureUI();
	}


	@Override
	public void setOpaque(boolean isOpaque)
	{
		super.setOpaque(isOpaque);

		if(initialized){
			textField.setOpaque(isOpaque);
		}
	}


/**	* Retrieves if the value is empty. If <tt>true</tt> a call of the {@link #getObject()}
	* method returns <tt>null</tt>.
	*/
	public boolean isEmpty()
	{
		return (textField.getText().trim().length() == 0);
	}


/**	* Retrieves the value of this control.
	*
	* @return				The value object for the current text of the control
	* 						<tt>null</tt> if the control is empty.
	*
	* @see #setObject(Object)
	* @see #isEmpty()
	*/
	public Object getObject() throws ParseException
	{
		String text = textField.getText().trim();
		if(text.length() == 0){
			return null;
		}

		return (format == null) ? text : format.parseObject(text);
	}


/**	* Sets the value of this control.
	*
	* @param object			The value object to be represented by the control.
	*
	* @see #getObject()
	*/
	public void setObject(Object object)
	{
		if(object == null){
			textField.setText("");
		}else{
			textField.setText((format == null) ? object.toString() : format.format(object));
		}
	}


	private class FormattedInputVerifier
		extends InputVerifier
	{
		@Override
		public boolean verify(JComponent input)
		{
			try{
				getObject();
				return true;
			}catch(ParseException ex){
				UIManager.getLookAndFeel().provideErrorFeedback(input);
				return false;
			}
		}


		@Override
		public boolean shouldYieldFocus(JComponent input)
		{
			if(verify(input)){
				try{
					setObject(getObject());
					return true;
				}catch(ParseException ex){
					UIManager.getLookAndFeel().provideErrorFeedback(input);
				}
			}
			return false;
		}
	}
}
