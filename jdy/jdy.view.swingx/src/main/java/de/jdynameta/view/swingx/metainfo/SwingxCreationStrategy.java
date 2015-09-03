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
package de.jdynameta.view.swingx.metainfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.ClassInfo;
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
import de.jdynameta.base.objectlist.ChangeableObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.metainfoview.attribute.model.ModelBlobPanel;
import de.jdynameta.metainfoview.attribute.model.ModelBooleanCheckbox;
import de.jdynameta.metainfoview.attribute.model.ModelCurrencyTextField;
import de.jdynameta.metainfoview.attribute.model.ModelDoubleTextField;
import de.jdynameta.metainfoview.attribute.model.ModelObjectReferenceCombobox;
import de.jdynameta.metainfoview.attribute.model.ModelTextField;
import de.jdynameta.metainfoview.attribute.model.ModelVarCharArea;
import de.jdynameta.metainfoview.metainfo.AssociationTablePanel;
import de.jdynameta.metainfoview.metainfo.ComponentCreationStrategy;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.swingx.metainfo.attribute.SwingxModelComboboxField;
import de.jdynameta.view.swingx.metainfo.attribute.SwingxModelDateTextField;
import de.jdynameta.view.swingx.metainfo.attribute.SwingxModelLongTextField;

public class SwingxCreationStrategy<TEditObj extends ApplicationObj> 
	implements ComponentCreationStrategy
{
	final PanelManager panelManager;
	final ApplicationManager<TEditObj> appManager;
	private final JComponentCreatorVisitor componentCreatorVisitor;
	
	
	
	public SwingxCreationStrategy(PanelManager aPanelManager, ApplicationManager<TEditObj> anAppManager)
	{
		super();
		this.panelManager = aPanelManager;
		this.appManager = anAppManager;
		this.componentCreatorVisitor = new JComponentCreatorVisitor();
	}
	
	public PanelManager getPanelManager() {
		return panelManager;
	}
	
	public ApplicationManager<TEditObj> getAppManager() 
	{
		return appManager;
	}

	
	public List<AttrInfoComponent> createAssociationComponents(ColumnVisibilityDef aVisibility, ClassInfo aInfo, WorkflowCtxt aWorkflowContext )
	{
		final ArrayList<AttrInfoComponent> newModelComponentColl =  new ArrayList<AttrInfoComponent>(10);

		for( AssociationInfo curAssoc :aInfo.getAssociationInfoIterator()) {
			
			if( aVisibility == null || aVisibility.isAssociationVisible(curAssoc)) {
				AttrInfoComponent assocPnl  = createAssociationComponent(aInfo, curAssoc, aWorkflowContext);
				if ( assocPnl != null) {
					newModelComponentColl.add(assocPnl);
				}
			}
		}
		
		return newModelComponentColl;
	}
	

	public List<AttrInfoComponent> createAttributeComponents(final ColumnVisibilityDef visibility, final ClassInfo aTypeInfo) throws JdyPersistentException
	{
		final ArrayList<AttrInfoComponent> newModelComponentColl =  new ArrayList<AttrInfoComponent>(10);
		
		aTypeInfo.handleAttributes( new AttributeHandler()
		{
			
			public void handleObjectReference(	final ObjectReferenceAttributeInfo aInfo,	ValueObject objToHandle)
				throws JdyPersistentException
			{
				if( visibility == null || visibility.isAttributeVisible(aInfo)) {
					newModelComponentColl.add(createObjectReferenceComponent(aTypeInfo, aInfo));
				}
			}


			public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,	Object objToHandle)
				throws JdyPersistentException
			{
				if( visibility == null || visibility.isAttributeVisible(aInfo)) {
					newModelComponentColl.add(createPrimitiveAttributeComponent(aTypeInfo,aInfo));
				}
			}
		}, null);
		
		return newModelComponentColl;
	}
	
	/**
	 * Create the Component for the given ObjectReferenceAttributeInfo
	 * @param aTypeInfo 
	 * @param aInfo
	 * @return the new component 
	 * @throws JdyPersistentException
	 */
	protected AttrInfoComponent createObjectReferenceComponent(ClassInfo aTypeInfo, final ObjectReferenceAttributeInfo aInfo)
			throws JdyPersistentException 
	{
		ModelObjectReferenceCombobox<TEditObj> attrCombo
			=	new ModelObjectReferenceCombobox<TEditObj>( aInfo, panelManager, appManager);
		attrCombo.setNullable( !aInfo.isNotNull() || aInfo.isGenerated());
		attrCombo.setShowNewAction(true);
		return attrCombo;
	}

	/**
	 * Create the Component for the given PrimitiveAttributeInfo
	 * @param aTypeInfo 
	 * @param aInfo
	 * @return the new component 
	 * @throws JdyPersistentException
	 */
	protected AttrInfoComponent createPrimitiveAttributeComponent(ClassInfo aTypeInfo, final PrimitiveAttributeInfo aInfo)
		throws JdyPersistentException 
	{
		List<? extends DbDomainValue<? extends Object>> domainValues = null;
		
		if( aInfo.getType() instanceof LongType ) {
			domainValues = ((LongType) aInfo.getType()).getDomainValues();
		} else if ( aInfo.getType() instanceof CurrencyType ) {
			domainValues = ((CurrencyType) aInfo.getType()).getDomainValues();
		} else if ( aInfo.getType() instanceof TextType ) {
			domainValues = ((TextType) aInfo.getType()).getDomainValues();
		}
		
		
		// return a ComboBox when the domain values are defined for the attributes
		if( domainValues == null || domainValues.size() == 0) {
			componentCreatorVisitor.setAttrInfo(aInfo);
			aInfo.getType().handlePrimitiveKey(componentCreatorVisitor, null);
			componentCreatorVisitor.getAttrComponent().setNullable( !aInfo.isNotNull() || aInfo.isGenerated());
			return componentCreatorVisitor.getAttrComponent();
		} else {
			ChangeableObjectListModel<SwingxModelComboboxField.DomainValueComboElem>  comboElemColl  = new ChangeableObjectListModel<SwingxModelComboboxField.DomainValueComboElem>();
			for (DbDomainValue<? extends Object> curDomValue : domainValues) {
				comboElemColl.addToListContent(new SwingxModelComboboxField.DomainValueComboElem(curDomValue));
			}
			return new SwingxModelComboboxField(comboElemColl,aInfo);
		}		
		
	}
	
	protected AttrInfoComponent createAssociationComponent(ClassInfo aInfo, final AssociationInfo curAssoc, WorkflowCtxt aWorkflowContext)
	{
		AssociationTablePanel<TEditObj> assocPnl = new AssociationTablePanel<TEditObj>(curAssoc, panelManager, appManager);
		assocPnl.setWorkflowContext(aWorkflowContext);
		// pass component creation strategy to dependent panels
		assocPnl.setEditPanelCreateComponentStrat(this);
		return assocPnl;
	}
	
	
	/**
	 * Type visitor that add a JCompnonet and Label to the given Panel for
	 * every visited type   
	 * @author Rainer
	 */
	protected static class JComponentCreatorVisitor  implements PrimitiveTypeVisitor
	{
		private PrimitiveAttributeInfo attrInfo;
		private AttrInfoComponent attrComponent;
		
		public JComponentCreatorVisitor()
		{
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
		public AttrInfoComponent getAttrComponent()
		{
			return attrComponent;
		}

		/**
		 * @return
		 */
		public PrimitiveAttributeInfo getAttrInfo()
		{
			return attrInfo;
		}


		public void handleValue(Boolean aValue, BooleanType aType)
			throws JdyPersistentException
		{
			this.attrComponent = new ModelBooleanCheckbox(attrInfo);
		}

		public void handleValue(BigDecimal aValue, CurrencyType aType)
			throws JdyPersistentException
		{
			this.attrComponent = new ModelCurrencyTextField( attrInfo, Locale.getDefault() );
		}

		public void handleValue(Double aValue, FloatType aType)
			throws JdyPersistentException
		{
			this.attrComponent = new ModelDoubleTextField( attrInfo, Locale.getDefault() );
		}


		public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
		{
			this.attrComponent = new SwingxModelLongTextField( attrInfo, Locale.getDefault() );
		}

		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			this.attrComponent = new ModelBlobPanel(attrInfo);			
		}		
		
		public void handleValue(String aValue, VarCharType aType)
			throws JdyPersistentException
		{
			this.attrComponent = new ModelVarCharArea(attrInfo);
		}

		public void handleValue(String aValue, TextType aType)
			throws JdyPersistentException
		{
			this.attrComponent = new ModelTextField(attrInfo);
		}

		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
		{
			this.attrComponent = new SwingxModelDateTextField(attrInfo, Locale.getDefault());
		}

		

	}	

}
