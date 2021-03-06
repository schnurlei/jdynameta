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
package de.jdynameta.metainfoview.metainfo.table;

import java.awt.Component;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.ClassInfoAttrSource;

/**
 * @author Rainer Schneider
 *
 */
public class AttributeTableCellRenderer implements TableCellRenderer 
{
	private AttributeInfo attributeInfo;
	
	private TableCellRenderer rendererComponent;
	private JLabel errorComponent;
	private ClassInfoAttrSource displayAttributs;
	
	/**
	 * Constructor for AttributeTableCellRenderer.
	 */
	public AttributeTableCellRenderer(AttributeInfo aInfo, ClassInfoAttrSource aAttrSource) 
	{
		super();
		this.attributeInfo = aInfo;
		this.displayAttributs = aAttrSource;
		
		errorComponent = new JLabel("ERROR");
		
		rendererComponent = this.createRendererFor(aInfo);
	}


	private TableCellRenderer createRendererFor(AttributeInfo aInfo)
	{
		AttributeRendererHandler handler = new AttributeRendererHandler();
		try {
			aInfo.handleAttribute(handler, null);
		} catch (JdyPersistentException excp) {
			excp.printStackTrace();
		}
		
		return handler.getRenderer();
	}



	public Component getTableCellRendererComponent(JTable table, Object value,	boolean isSelected
						,boolean hasFocus, int row,int column) 
	{
		Component result = null;

		if ( value instanceof ValueObject) {
			Object attributeValue = ((ValueObject) value).getValue(this.attributeInfo);
			result = rendererComponent.getTableCellRendererComponent(table, attributeValue, isSelected
						,hasFocus, row, column);
		} else 
		{
			result = errorComponent;
		}

		return result;
	}

	private class AttributeRendererHandler implements  AttributeHandler
	{
		private TableCellRenderer renderer;
			
		public void handleObjectReference(	final ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
			throws JdyPersistentException 
		{
			renderer = new DefaultTableCellRenderer()
			{
				@Override
				public void setValue(Object value) 
				{
					setText(getTextForAttribute( aInfo, value));
				}
			};

		}

		public void handlePrimitiveAttribute( PrimitiveAttributeInfo aInfo,	Object objToHandle)
			throws JdyPersistentException 
		{
			try {
				PrimitiveRendererCreator columnCreator = new PrimitiveRendererCreator();	
				aInfo.getType().handlePrimitiveKey(columnCreator, null);
				renderer = columnCreator.getRenderer();
			} catch (JdyPersistentException ex) {
				// no exception is thrown
				ex.printStackTrace();
			}
		}

		public TableCellRenderer getRenderer() 
		{
			return renderer;
		}

		private String getTextForAttribute(ObjectReferenceAttributeInfo aReferenceInfo, Object aValue)
		{
			final StringBuffer text = new StringBuffer(); 
			final List<AttributeInfo> attrList = displayAttributs.getDisplayAttributesFor(aReferenceInfo.getReferencedClass());
			AttributeHandler showTexthandler = new AttributeHandler()
			{
				public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,	ValueObject objToHandle)
					throws JdyPersistentException
				{
					if(aInfo.isKey()) {
						aInfo.getReferencedClass().handleAttributes(this, objToHandle);
					}
				}

				public void handlePrimitiveAttribute(	PrimitiveAttributeInfo aInfo, Object objToHandle)
					throws JdyPersistentException
				{
					if( objToHandle != null) {
						if(( attrList == null && aInfo.isKey()) || ( attrList != null && attrList.contains(aInfo))) {
							text.append(objToHandle.toString() + "  ");
						}
					}
				}
		
			};
			
			try
			{
				aReferenceInfo.getReferencedClass().handleAttributes(showTexthandler,(ValueObject) aValue);
			} catch (JdyPersistentException excp)
			{
				excp.printStackTrace();
			}
			
			return text.toString();
		}
		
	}

	
	/**
	 * @author Rainer Schneider
	 *
	 */
	public static class PrimitiveRendererCreator implements PrimitiveTypeVisitor 
	{
		private TableCellRenderer renderer;
		
		/**
		 * Returns the renderer.
		 * @return TableCellRenderer
		 */
		public TableCellRenderer getRenderer() 
		{
			return renderer;
		}


		/**
		 * Constructor for PrimitiveColumnHandler.
		 */
		public PrimitiveRendererCreator() 
		{
			super();
		}

		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(BigDecimal, CurrencyType)
		 */
		public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException 
		{
			renderer = new DoubleRenderer();		
		}

		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(Boolean, BooleanType)
		 */
		public void handleValue(Boolean aValue, BooleanType aType)throws JdyPersistentException 
		{
			renderer = new BooleanRenderer();		
		}

		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(Date, DateType)
		 */
		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException 
		{
			renderer = new DateRenderer();		
		}

		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(Double, FloatType)
		 */
		public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException 
		{
			renderer = new DoubleRenderer();		
		}


		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(Long, LongType)
		 */
		public void handleValue(Long aValue, LongType aType)throws JdyPersistentException 
		{
			renderer = new DoubleRenderer();		
		}

		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(String, TextType)
		 */
		public void handleValue(String aValue, TextType aType)throws JdyPersistentException 
		{
			renderer = new DefaultTableCellRenderer();		
		}

		/**
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeHandler#handleValue(String, VarCharType)
		 */
		public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException 
		{
			renderer = new DefaultTableCellRenderer();		
		}


		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			renderer = new ClobRenderer();
		}
	}
	
    static class BooleanRenderer extends JCheckBox implements TableCellRenderer
    {
		public BooleanRenderer() 
		{
		    super();
		    setHorizontalAlignment(JLabel.CENTER);
		}

        public Component getTableCellRendererComponent(JTable table, Object value,
						       boolean isSelected, boolean hasFocus, int row, int column) 
		{
		    if (isSelected) {
		        setForeground(table.getSelectionForeground());
		        super.setBackground(table.getSelectionBackground());
		    }
		    else {
		        setForeground(table.getForeground());
		        setBackground(table.getBackground());
		    }
            setSelected((value != null && ((Boolean)value).booleanValue()));
            return this;
        }
    }
	
	
	static class NumberRenderer extends DefaultTableCellRenderer 
	{
		public NumberRenderer() 
		{
	    	super();
	    	setHorizontalAlignment(JLabel.RIGHT);
		}
    }
	
	static class DoubleRenderer extends NumberRenderer 
	{
		NumberFormat formatter;
		public DoubleRenderer() 
		{
			super(); 
		}

		@Override
		public void setValue(Object value) 
		{
		    if (formatter == null) {
				formatter = NumberFormat.getInstance();
		    }
			setText((value == null) ? "" : formatter.format(value));
		}
    }

	static class DateRenderer extends DefaultTableCellRenderer 
	{
		DateFormat formatter;
		public DateRenderer() 
		{
			super(); 
		}
	
		@Override
		public void setValue(Object value) {
	    	if (formatter==null) {
				formatter = DateFormat.getDateInstance();
	    	}
		    setText((value == null) ? "" : formatter.format(value));
		}
	}

    static class ClobRenderer extends JLabel implements TableCellRenderer
    {
		public ClobRenderer() 
		{
		    super();
		}

        public Component getTableCellRendererComponent(JTable table, Object value,
						       boolean isSelected, boolean hasFocus, int row, int column) 
		{
		    if (isSelected) {
		        if(value == null) {
			        setForeground(table.getSelectionForeground().brighter());
		        } else {
			        setForeground(table.getSelectionForeground());
		        }
		        setBackground(table.getSelectionBackground());
		    }
		    else {
		        if(value == null) {
			        setForeground(table.getForeground().brighter());
		        } else {
			        setForeground(table.getForeground());
		        }
		        setBackground(table.getBackground());
		    }
            
		    setText(value != null ? "CLOB" : "clob");
            return this;
        }
    }
}
