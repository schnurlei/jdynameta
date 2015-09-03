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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ExpressionPrimitiveOperator;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorGreater;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorLess;
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
import de.jdynameta.base.objectlist.ChangeableObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.metainfoview.attribute.model.ModelComboboxField;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;

public class FilterEditorCreator<TEditObj extends ApplicationObj> 
{
	private final JComponentCreatorVisitor componentCreatorVisitor = new JComponentCreatorVisitor();
	private ApplicationManager<TEditObj> appMngr;
	
	
	public FilterEditorCreator(ApplicationManager<TEditObj> anAppMngr)
	{
		super();
		
		this.appMngr = anAppMngr; 
	}
	
	
	/**
	 * Create the Component for the given AttributeInfo
	 * @param associationAttributePath.getAttribute()
	 * @return the new component 
	 * @throws JdyPersistentException
	 */
	public FilterEditorComponent createAttributeComponent(final AssociationAttributePath anAssocAttrPath)
	{
		
		if( anAssocAttrPath.getAttribute() instanceof PrimitiveAttributeInfo) {

			List<? extends DbDomainValue<? extends Object>> domainValues = null;
			
			PrimitiveAttributeInfo aInfo = (PrimitiveAttributeInfo) anAssocAttrPath.getAttribute();
			if( aInfo.getType() instanceof LongType ) {
				domainValues = ((LongType) aInfo.getType()).getDomainValues();
			} else if ( aInfo.getType() instanceof CurrencyType ) {
				domainValues = ((CurrencyType) aInfo.getType()).getDomainValues();
			} else if ( aInfo.getType() instanceof TextType ) {
				domainValues = ((TextType) aInfo.getType()).getDomainValues();
			}
			
			if ( domainValues == null || domainValues.size() == 0) {
				componentCreatorVisitor.setAttrInfo((PrimitiveAttributeInfo)anAssocAttrPath.getAttribute());
				try {
					((PrimitiveAttributeInfo)anAssocAttrPath.getAttribute()).getType().handlePrimitiveKey(componentCreatorVisitor, null);
				} catch (JdyPersistentException e) {
					// Ignore , no persistence necessary
					
					e.printStackTrace();
				}
				return componentCreatorVisitor.getFilterEditor();
			} else {
				ChangeableObjectListModel<ModelComboboxField.DomainValueComboElem>  comboElemColl  = new ChangeableObjectListModel<ModelComboboxField.DomainValueComboElem>();
				for (DbDomainValue<? extends Object> curDomValue : domainValues) {
					comboElemColl.addToListContent(new ModelComboboxField.DomainValueComboElem(curDomValue));
				}
				return new FilterDomainValueCombobox(comboElemColl);
			}
		} else {
			try {
				return new FilterObjectReferenceCombobox<TEditObj>((ObjectReferenceAttributeInfo)anAssocAttrPath.getAttribute(),  appMngr);
			} catch (JdyPersistentException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public ExpressionPrimitiveOperator[] getOperators(AssociationAttributePath anAssocAttrPath) throws JdyPersistentException
	{
		if( anAssocAttrPath.getAttribute() instanceof PrimitiveAttributeInfo) {
			JOperatorListVisitor operatorVisitor = new JOperatorListVisitor(((PrimitiveAttributeInfo)anAssocAttrPath.getAttribute()));
			((PrimitiveAttributeInfo)anAssocAttrPath.getAttribute()).getType().handlePrimitiveKey(operatorVisitor, null);
			return operatorVisitor.getOperators();
		} else {
			return JOperatorListVisitor.REFERENCE_OPERATORS;
		}
	}
	
	
	/**
	 * Type visitor that add a JCompnonet and Label to the given Panel for
	 * every visited type   
	 * @author Rainer
	 */
	protected static class JComponentCreatorVisitor implements PrimitiveTypeVisitor
	{
		private PrimitiveAttributeInfo attrInfo;
		
		private FilterEditorComponent filterEditor;
		
		public JComponentCreatorVisitor()
		{
		}


		
		public void handleValue(Boolean aValue, BooleanType aType)
			throws JdyPersistentException
		{
			this.filterEditor = new FilterBooleanCheckbox();
		}

		public void handleValue(BigDecimal aValue, CurrencyType aType)
			throws JdyPersistentException
		{
			this.filterEditor = new FilterCurrencyTextfield( attrInfo );
		}

		public void handleValue(Double aValue, FloatType aType)
			throws JdyPersistentException
		{
			this.filterEditor = new FilterDoubleTextfield();
		}


		public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
		{
			this.filterEditor = new FilterLongTextfield( attrInfo, aType.getMaxValue(), 1 );
		}

		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			this.filterEditor = null;	// Blobs could not be filtered		
		}		
		
		public void handleValue(String aValue, VarCharType aType)
			throws JdyPersistentException
		{
			this.filterEditor = new FilterStringTextfield((int) aType.getLength());
		}

		public void handleValue(String aValue, TextType aType)
			throws JdyPersistentException
		{
			this.filterEditor = new FilterStringTextfield( aType.getLength());
		}

		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
		{
			//this.filterEditor = new FilterDateTextfield(attrInfo);
			this.filterEditor = new FilterDateTextfield();
		}

		
		/**
		 * @param aInfo
		 */
		public void setAttrInfo(PrimitiveAttributeInfo aInfo)
		{
			attrInfo = aInfo;
		}

		/**
		 * @return
		 */
		public FilterEditorComponent getFilterEditor()
		{
			return filterEditor;
		}

		/**
		 * @return
		 */
		public PrimitiveAttributeInfo getAttrInfo()
		{
			return attrInfo;
		}

	}
	
	/**
	 * Type visitor that add a JCompnonet and Label to the given Panel for
	 * every visited type   
	 * @author Rainer
	 */
	protected static class JOperatorListVisitor implements PrimitiveTypeVisitor
	{
		private static final ExpressionPrimitiveOperator[] DEFAULT_OPERATOR 
			= new ExpressionPrimitiveOperator[]{ DefaultOperatorEqual.getEqualInstance(), DefaultOperatorEqual.getNotEqualInstance(), DefaultOperatorLess.getLessInstance(), DefaultOperatorGreater.getGreateInstance()
											, DefaultOperatorLess.getLessOrEqualInstance(), DefaultOperatorGreater.getGreaterOrEqualInstance()};
		private static final ExpressionPrimitiveOperator[] BOOLEAN_OPERATOR = new ExpressionPrimitiveOperator[]{ DefaultOperatorEqual.getEqualInstance(),  DefaultOperatorEqual.getNotEqualInstance()};
		private static final ExpressionPrimitiveOperator[] STRING_OPERATOR 
				= new ExpressionPrimitiveOperator[]{ DefaultOperatorEqual.getEqualInstance(), DefaultOperatorEqual.getNotEqualInstance(), DefaultOperatorLess.getLessInstance(), DefaultOperatorGreater.getGreateInstance()
										};
		private static final ExpressionPrimitiveOperator[] REFERENCE_OPERATORS 
			= new ExpressionPrimitiveOperator[]{ DefaultOperatorEqual.getEqualInstance(), DefaultOperatorEqual.getNotEqualInstance()};

		
		private PrimitiveAttributeInfo attrInfo;
		private ExpressionPrimitiveOperator[] operators;
		
		public JOperatorListVisitor(PrimitiveAttributeInfo aAttrInfo)
		{
			this.attrInfo = aAttrInfo;
		}


		
		public void handleValue(Boolean aValue, BooleanType aType)
			throws JdyPersistentException
		{
			this.operators = BOOLEAN_OPERATOR;
		}

		public void handleValue(BigDecimal aValue, CurrencyType aType)
			throws JdyPersistentException
		{
			this.operators = DEFAULT_OPERATOR;
		}

		public void handleValue(Double aValue, FloatType aType)
			throws JdyPersistentException
		{
			this.operators = DEFAULT_OPERATOR;
		}


		public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
		{
			this.operators = DEFAULT_OPERATOR;
		}


		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			this.operators = DEFAULT_OPERATOR;	// Blobs could not be filtered		
		}		
		
		public void handleValue(String aValue, VarCharType aType)
			throws JdyPersistentException
		{
			this.operators = STRING_OPERATOR; // Clobs could not be filtered		
		}

		public void handleValue(String aValue, TextType aType)
			throws JdyPersistentException
		{
			this.operators = STRING_OPERATOR;
		}

		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
		{
			this.operators = DEFAULT_OPERATOR;
		}

		
		/**
		 * @param aInfo
		 */
		public void setAttrInfo(PrimitiveAttributeInfo aInfo)
		{
			attrInfo = aInfo;
		}

		/**
		 * @return
		 */
		public ExpressionPrimitiveOperator[] getOperators()
		{
			return operators;
		}

		/**
		 * @return
		 */
		public PrimitiveAttributeInfo getAttrInfo()
		{
			return attrInfo;
		}

	}
	

}
