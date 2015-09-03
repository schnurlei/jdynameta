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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import de.jdynameta.application.ApplicationManager;
import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ExpressionPrimitiveOperator;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultAssociationExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultExpressionAnd;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultObjectReferenceEqualExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorGreater;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorLess;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.filter.AssociationAttributePath.VisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.view.JdyResourceLoader;

/**
 * Component of a {@link FilterPanel} Filter Panel which could be deleted from the panel.
 * It defines an actions to remove this component from its parent filter panel.
 * The possible attributes are displayed in a combobox and the possible operators could be selected from a combobox.
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public abstract class DeletableOperatorFilterComponent<TEditObj extends ApplicationObj> extends JPanel
{
	private static int expeccoCounter=0;
	private final PropertyNameCreator propertyGenerator;
	private final JdyResourceLoader resourceLoader;
	private final ClassInfo classInfo;
	private final JComboBox operatorCombo;
	private final JComboBox attrCmbx;
	private FilterEditorComponent valueField;
	private JPanel valueFieldHolder;
	private JPanel filterPnl;
	private Collator attrComperator = Collator.getInstance();
	private Action refreshAction;
	private FilterEditorCreator<TEditObj> editorCreator;

	public DeletableOperatorFilterComponent( ApplicationManager<TEditObj> anAppMngr, ClassInfo aClassInfo, JdyResourceLoader aResourceLoader)
	{
		this(anAppMngr, aClassInfo, aResourceLoader, null, null);
	}
	
	public DeletableOperatorFilterComponent( ApplicationManager<TEditObj> anAppMngr, ClassInfo aClassInfo, JdyResourceLoader aResourceLoader, VisibilityDef aVisibilityDef, Action refreshAction)
	{
		super(new BorderLayout());
		this.attrComperator = Collator.getInstance();
		attrComperator.setStrength(Collator.TERTIARY); 
		this.propertyGenerator = new DefaultPropertyNameCreator();
		this.resourceLoader = aResourceLoader;
		this.classInfo = aClassInfo;
		this.refreshAction = refreshAction;
		editorCreator = new FilterEditorCreator(anAppMngr);
		
		Vector<AssociationAttributePath> attrPathColl = new Vector<AssociationAttributePath>();
		addAttributePaths(aVisibilityDef, attrPathColl, this.classInfo, null); 
		
		// add first level associations attributes
		for (AssociationInfo assocInfo : this.classInfo.getAssociationInfoIterator())
		{
			addAttributePaths(aVisibilityDef, attrPathColl, assocInfo.getDetailClass(), assocInfo);
			
		}		
		
		Collections.sort(attrPathColl, new Comparator<AssociationAttributePath>()
		{
			public int compare(AssociationAttributePath attr1, AssociationAttributePath attr2) 
			{
				String attrName1 = attr1.getDisplayName(resourceLoader, propertyGenerator, classInfo);
				String attrName2 = attr2.getDisplayName(resourceLoader, propertyGenerator, classInfo);
				
				return attrComperator.compare(attrName1,attrName2);
			};
		});
		this.attrCmbx = new JComboBox(attrPathColl);
		//Expecco
		//System.out.println("expeccoCounter="+ expeccoCounter+ " for New");

		this.attrCmbx.setName("DeleteTableOperatorFilterComponent.AttrCmbx"+expeccoCounter);
		this.attrCmbx.getAccessibleContext().setAccessibleName("DeleteTableOperatorFilterComponent.AttrCmbx" +"_Access"+expeccoCounter);

		this.attrCmbx.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				attributeSelectionChanged();
			}
		});
		
		this.attrCmbx.setRenderer(createAttributeTextRenderer() );
		
		this.operatorCombo = new JComboBox(new ExpressionPrimitiveOperator[]{new DefaultOperatorEqual(), new DefaultOperatorLess(false), new DefaultOperatorGreater(false), new DefaultOperatorEqual(true)});
		//Expecco
		this.operatorCombo.setName("DeleteTableOperatorFilterComponent.OperatorCombo"+expeccoCounter);
		this.operatorCombo.getAccessibleContext().setAccessibleName("DeleteTableOperatorFilterComponent.OperatorCombo" +"_Access"+expeccoCounter);
		this.operatorCombo.setPreferredSize( new Dimension(60, this.operatorCombo.getPreferredSize().height));
		
		
		this.operatorCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				markFieldAsChanged();
			}
		});
		this.valueField = null;
		this.filterPnl = new JPanel(new GridBagLayout());
		this.valueFieldHolder = new JPanel(new BorderLayout());
		initUi();
		expeccoCounter++;
		attributeSelectionChanged();
		
		
	}

	private void addAttributePaths(VisibilityDef aVisibilityDef, Vector<AssociationAttributePath> attrColl, ClassInfo aClassInfo, AssociationInfo aAssocInfo) 
	{
		for (AttributeInfo attrInfo: aClassInfo.getAttributeInfoIterator())
		{
			if( !(attrInfo instanceof PrimitiveAttributeInfo) || !(((PrimitiveAttributeInfo)attrInfo).getType() instanceof BlobType) ) {

				AssociationAttributePath assocPath = new AssociationAttributePath(aClassInfo, attrInfo, aAssocInfo) ;
				// add only top level attributes if no visibility is defined
				if( aVisibilityDef != null ) {
					if( aVisibilityDef.isAttributeVisible(assocPath)) {
						attrColl.add(assocPath);
					}
				} else {
					if( aAssocInfo == null) {
						attrColl.add(assocPath);
					}
				}
			}
		}
	}
	
	public void setRefreshAction(Action refreshAction) 
	{
		this.refreshAction = refreshAction;
	}
	
	protected ListCellRenderer createAttributeTextRenderer() 
	{
		return new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(	JList list,	Object value, int index, boolean isSelected, boolean cellHasFocus) 
			{
				Component resultComp = super.getListCellRendererComponent( list, value, index
																			,isSelected, cellHasFocus);
				if( resultComp instanceof JLabel ) {
					if( value != null && value instanceof AssociationAttributePath) {
						((JLabel)resultComp).setText(((AssociationAttributePath) value).getDisplayName(resourceLoader, propertyGenerator, classInfo));															
					} else {
						((JLabel)resultComp).setText("");															
					}
				}
				return resultComp;																			
			}

		};
	}
	
	
	private void initUi()
	{
		GridBagConstraints lblConstr = new GridBagConstraints(GridBagConstraints.RELATIVE,0, 1,1
				, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE
				, new Insets(0,0,0,5), 0, 0);
		GridBagConstraints fieldConstr = new GridBagConstraints(GridBagConstraints.RELATIVE,0, 1,1
				, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL
				, new Insets(0,0,0,10), 0, 0);
		filterPnl = new JPanel(new GridBagLayout());
		this.valueFieldHolder = new JPanel(new BorderLayout());
		valueFieldHolder.setMinimumSize(new Dimension(200,20));
		valueFieldHolder.setPreferredSize(new Dimension(200,20));
		filterPnl.add(this.attrCmbx, lblConstr);
		filterPnl.add(this.operatorCombo, lblConstr);
		filterPnl.add(this.valueFieldHolder, fieldConstr);
		
		JButton deleteButton = new JButton(createDeleteAction());
		deleteButton.setName("DeleteTableOperatorFilterComponent.DeleteButton"+expeccoCounter);
		deleteButton.getAccessibleContext().setAccessibleName("DeleteTableOperatorFilterComponent.DeleteButton" +"_Access"+expeccoCounter);

		filterPnl.add( deleteButton, lblConstr);
		
	}
	/**
	 * Add the Edit Component to the given container with the given constraints
	 * @param aContainer
	 * @param constraints
	 */
	public void addToContainer(Container aContainer, Object constraints)
	{
		aContainer.add(filterPnl, constraints);
	}
	
	
	public void removeFromContainer(Container aContainer)
	{
		aContainer.remove(filterPnl);
	}
	
	public FilterEditorCreator getEditorCreator()
	{
		return  editorCreator;
	}
	
	public void setEditorCreator(FilterEditorCreator editorCreator) 
	{
		this.editorCreator = editorCreator;
		attributeSelectionChanged();
	}
	
	private void attributeSelectionChanged()
	{
		valueFieldHolder.removeAll();
		
		if( this.valueField != null) {
			this.valueField.setInputChangedListener(null);
		}
		
		if( getSelectedAttributePath() != null) {
			
			FilterEditorCreator editorCreator = getEditorCreator();
			
			this.valueField =  editorCreator.createAttributeComponent(getSelectedAttributePath());

			if( this.valueField != null) {
				this.valueField.setInputChangedListener(new FilterEditorComponent.InputChangedListener()
				{
					public void inputHasChanged() {
						markFieldAsChanged();
					}
				});
				valueField.addToContainer(valueFieldHolder, BorderLayout.CENTER);
			}
			
			try {
				this.operatorCombo.setModel(new DefaultComboBoxModel(editorCreator.getOperators(getSelectedAttributePath())));
			} catch (JdyPersistentException e) {
				// IGNORE 
				e.printStackTrace();
			}
			
		}
		filterPnl.invalidate();
		filterPnl.revalidate();
		filterPnl.repaint();
		if (refreshAction!=null) {
			refreshAction.putValue(FilterPanel.BTN_PROPERTY_HIGHLIGHT, "true");
		}
	}


	public AssociationAttributePath getSelectedAttributePath()
	{
		return (AssociationAttributePath) this.attrCmbx.getSelectedItem();
	}
	
	
	public void setSelectedAttributePath(AssociationAttributePath anAttr)
	{
		this.attrCmbx.setSelectedItem(anAttr);
	}
	
	private Action createDeleteAction()
	{
		return new AbstractAction("x")
		{
			public void actionPerformed(ActionEvent e)
			{
				delete();
			}
		};
	}

	public abstract void delete();
	
	protected void markFieldAsChanged()
	{
		if( this.valueField != null) {
			this.valueField.markFieldAsChanged(Color.cyan);
		}
		this.operatorCombo.setBackground(Color.cyan);
		if (refreshAction!=null)
			refreshAction.putValue(FilterPanel.BTN_PROPERTY_HIGHLIGHT, "true");
	}

	public void resetMarker()
	{
		if( this.valueField != null) {
		
			this.valueField.markFieldAsChanged(Color.WHITE);
		}
		this.operatorCombo.setBackground(Color.WHITE);
	}

	public static void resetExpeccoCounter()
	{
		expeccoCounter = 0;
		//System.out.println("expeccoCounter="+ expeccoCounter+" after Reset");
	}	
	
	public ObjectFilterExpression getFilterExpr()
	{
		AssociationAttributePath assocPath = getSelectedAttributePath();
		
		ObjectFilterExpression expr = null;
		
		if( getSelectedAttributePath().getAttribute() instanceof PrimitiveAttributeInfo ){
			if ( ((PrimitiveAttributeInfo)getSelectedAttributePath().getAttribute()).getType() instanceof TimeStampType ) {
				expr = getDateFilterExpr((PrimitiveAttributeInfo) getSelectedAttributePath().getAttribute());
			} 
			
			if ( expr == null) {
				DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
				opExpr.setAttributeInfo((PrimitiveAttributeInfo) getSelectedAttributePath().getAttribute());
				opExpr.setCompareValue(getFilterValue());
				opExpr.setMyOperator((ExpressionPrimitiveOperator) this.operatorCombo.getSelectedItem());
				expr = opExpr;
			}
		} else {
			
			boolean isNotEqual = this.operatorCombo.getSelectedItem().equals(DefaultOperatorEqual.getNotEqualInstance());
			DefaultObjectReferenceEqualExpression equalExpr
				= new DefaultObjectReferenceEqualExpression((ObjectReferenceAttributeInfo) getSelectedAttributePath().getAttribute(), (ValueObject) getFilterValue(), isNotEqual);
			expr = equalExpr;
		}
		

		if( assocPath.getAssocInfoPath() != null) {

			return new DefaultAssociationExpression( assocPath.getAssocInfoPath().get(0), expr);
		} else {
			return expr;
		}
	}
	
	public ObjectFilterExpression getDateFilterExpr(PrimitiveAttributeInfo primitiveAttributeInfo)
	{
		ObjectFilterExpression returnExpr;
		
		if( getFilterValue() instanceof Date){
			Date filterDate = (Date) getFilterValue();
			
			ExpressionPrimitiveOperator operator = getOperator();
			if( operator.equals(DefaultOperatorLess.getLessInstance()) || operator.equals(DefaultOperatorLess.getLessOrEqualInstance())) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(filterDate);
				if( operator.equals(DefaultOperatorLess.getLessInstance())) {
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				} else {
					calendar.set(Calendar.SECOND, 59);
					calendar.set(Calendar.MILLISECOND, 999);
				}
				DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
				opExpr.setAttributeInfo(primitiveAttributeInfo);
				opExpr.setCompareValue(calendar.getTime()); 
				opExpr.setMyOperator(getOperator());
				returnExpr = opExpr;
			} else if( operator.equals(DefaultOperatorGreater.getGreateInstance()) || operator.equals(DefaultOperatorGreater.getGreaterOrEqualInstance())) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(filterDate);
				if( operator.equals(DefaultOperatorGreater.getGreateInstance()) ) {
					calendar.set(Calendar.SECOND, 59);
					calendar.set(Calendar.MILLISECOND, 999);
				} else {
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}
				DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
				opExpr.setAttributeInfo(primitiveAttributeInfo);
				opExpr.setCompareValue(calendar.getTime()); 
				opExpr.setMyOperator(getOperator());
				returnExpr = opExpr;
			} else if ( operator.equals(DefaultOperatorEqual.getEqualInstance())) {
				Calendar upperBound = Calendar.getInstance();
				upperBound.setTime(filterDate);
				upperBound.set(Calendar.SECOND, 59);
				upperBound.set(Calendar.MILLISECOND, 999);
				Calendar lowerBound = Calendar.getInstance();
				lowerBound.setTime(filterDate);
				lowerBound.set(Calendar.SECOND, 0);
				lowerBound.set(Calendar.MILLISECOND, 0);
		
				DefaultOperatorExpression upperExpr = new DefaultOperatorExpression();
				upperExpr.setAttributeInfo(primitiveAttributeInfo);
				upperExpr.setCompareValue(upperBound.getTime());
				System.out.println("UpperBound:" + upperBound.getTime());
				upperExpr.setMyOperator(DefaultOperatorLess.getLessOrEqualInstance());
				DefaultOperatorExpression lowerExpr = new DefaultOperatorExpression();
				lowerExpr.setAttributeInfo(primitiveAttributeInfo);
				lowerExpr.setCompareValue(lowerBound.getTime());
				System.out.println("LowerBound:" + lowerBound.getTime());
				lowerExpr.setMyOperator(DefaultOperatorGreater.getGreaterOrEqualInstance());
			
				DefaultExpressionAnd equalExprIgnoringSeconds = DefaultExpressionAnd.createAndExpr(lowerExpr, upperExpr);
				returnExpr = equalExprIgnoringSeconds;
			} else {
				returnExpr = null;
			}
			
		} else {
			returnExpr = null;
		}
		
		return returnExpr;
		
	}
	
	
	
	
	public boolean hasExpression()
	{
		return this.valueField != null;// && this.valueField.getValue() != null;
	}

	public AssociationAttributePath getAttrPathInfo() 
	{
		return getSelectedAttributePath();
	}
	
	public Object getFilterValue()
	{
//		if( getOperator() instanceof OperatorLike) {
//			return (this.valueField == null ||  this.valueField.getValue() == null) ? null : "%" + this.valueField.getValue() + "%";
//		} else {
		return (this.valueField == null) ? null : this.valueField.getValue();
//		}
		
	}
	
	public void setFilterValue(Object newValue)
	{
		if( this.valueField != null) {
			this.valueField.setValue(newValue);
		}
	}
	
	
	public ExpressionPrimitiveOperator getOperator()
	{
		return (ExpressionPrimitiveOperator) this.operatorCombo.getSelectedItem();
	}
	
	
	public void setOperator(ExpressionPrimitiveOperator selectedOperator)
	{
		this.operatorCombo.setSelectedItem(selectedOperator);
	}
	
}