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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.jdynameta.application.ApplicationManager;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ExpressionPrimitiveOperator;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultExpressionAnd;
import de.jdynameta.base.metainfo.impl.JdyBlobType;
import de.jdynameta.base.metainfo.impl.JdyBooleanType;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyDecimalType;
import de.jdynameta.base.metainfo.impl.JdyFloatType;
import de.jdynameta.base.metainfo.impl.JdyLongType;
import de.jdynameta.base.metainfo.impl.JdyPrimitiveAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyTextType;
import de.jdynameta.base.metainfo.impl.JdyTimeStampType;
import de.jdynameta.base.metainfo.impl.JdyVarCharType;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.util.ActionUtil;

@SuppressWarnings("serial")
public class FilterPanel<TEditObj extends ApplicationObj> extends ManagedPanel
{
	public static final String BTN_PROPERTY_HIGHLIGHT = "Highlight";

	private final ClassInfo filterClassInfo;
	private JdyResourceLoader resources;
	private JPanel filterComponentPnl;
	private int filterPnlCounter;
	private int filterColumns;
	private GridBagConstraints filterConstaint;
	private List<DeletableOperatorFilterComponent<TEditObj>> componentList;
	private AssociationAttributePath.VisibilityDef attributeVisibility;
	private Action refreshAction;
	private Action resetAction;
	private FilterEntry[] defaultFilter;
	private ApplicationManager<TEditObj> appMngr;
	
	public FilterPanel(ApplicationManager<TEditObj> anAppMngr, ClassInfo aFilterClassInfo, JdyResourceLoader aResourceLoader, Action refreshAction)
	{
		super();
		this.appMngr = anAppMngr;
		this.filterClassInfo = aFilterClassInfo;
		this.resources = aResourceLoader;
		this.filterColumns = 1;
		this.filterConstaint = new GridBagConstraints(0,GridBagConstraints.RELATIVE,1,1,
													1.0,0.0,GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL
													, new Insets(0,0,0,0),0,0 );
		this.filterComponentPnl = new JPanel(new GridBagLayout());
		this.componentList = new ArrayList<DeletableOperatorFilterComponent<TEditObj>>();
		this.refreshAction = refreshAction;
		this.resetAction = createResetFilterAction(aResourceLoader);
		this.defaultFilter = new FilterEntry[0];
		initUi();
	
	}
	
	private AbstractAction createResetFilterAction(JdyResourceLoader resourceLoader)
	{
		AbstractAction createdAction =  new AbstractAction(resourceLoader.getString("common.action.resetFilter"))
		{
			public void actionPerformed(final ActionEvent e) 
			{
				resetToDefaultFilter();
				if( refreshAction != null) {
					SwingUtilities.invokeLater( new Runnable()
					{
						public void run() {
							refreshAction.actionPerformed(e);
						}
					});
				}
			}
		};
		
		ActionUtil.addDescriptionsToAction(createdAction, resourceLoader.getString("common.action.resetFilter.short"), resourceLoader.getString("common.action.resetFilter.long"));
		return createdAction;
	}
	
	public void setDefaultFilter(FilterEntry[] defaultFilter) 
	{
		this.defaultFilter = defaultFilter;
		this.resetToDefaultFilter();
	}
	
	private void resetToDefaultFilter() 
	{
		for (DeletableOperatorFilterComponent<TEditObj> curComponent : componentList) {
			curComponent.removeFromContainer(filterComponentPnl);
		}
		
		componentList.clear();
		
		for (FilterEntry curEntry : defaultFilter) {
			
			DeletableOperatorFilterComponent<TEditObj> deleteComponent = createDeletableComponent();
			
			deleteComponent.setSelectedAttributePath(curEntry.getAttrInfo());
			deleteComponent.setFilterValue(curEntry.getFilterValue());
			deleteComponent.setOperator(curEntry.getOperator());
			
			this.componentList.add(deleteComponent);
			deleteComponent.addToContainer(filterComponentPnl, filterConstaint);
		}
		
		this.invalidate();
		this.revalidate();
		this.repaint();
	}
	
	
	
	public ClassInfo getFilterClassInfo() 
	{
		return filterClassInfo;
	}

	public void setRefreshAction(Action aRefreshAction) 
	{
		this.refreshAction = aRefreshAction;
		for (DeletableOperatorFilterComponent<TEditObj> curComponent : componentList) {
			curComponent.setRefreshAction(aRefreshAction);
		}
	}
	
	public AssociationAttributePath.VisibilityDef getAttributeVisibility() 
	{
		return attributeVisibility;
	}
	
	public void setAttributeVisibility(AssociationAttributePath.VisibilityDef anAttributeVisibility) 
	{
		this.attributeVisibility = anAttributeVisibility;
	}
	
	private void initUi() 
	{
		this.setLayout(new BorderLayout());
		this.add(this.filterComponentPnl, BorderLayout.CENTER);
		
		
		JPanel actionBarPnl = new JPanel();
		actionBarPnl.setLayout((new GridLayout(1,4, 10, 10))); // gridLayout to ensure same size of buttons
        JPanel pnlButtonsDecorator = new JPanel(new GridBagLayout()); // Gridbag to ensure size is not wider the necessary
        pnlButtonsDecorator.add(actionBarPnl, new GridBagConstraints(0, 0, 1, 1
    			,0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE
    			, new Insets(10,0,10,0), 0,0 ));
        
        JPanel buttonPnl = new JPanel( new BorderLayout());
        buttonPnl.add(pnlButtonsDecorator, BorderLayout.LINE_START);
		
		GridBagConstraints buttonConstr = new GridBagConstraints(GridBagConstraints.RELATIVE,0,1,1,
				0.0,0.0,GridBagConstraints.PAGE_START, GridBagConstraints.NONE
				, new Insets(0,0,0,0),0,0 );
		actionBarPnl.add(new JButton(createAddComponentAction()), buttonConstr);
		actionBarPnl.add(new JButton(resetAction), buttonConstr);
//		buttonConstr.weightx=1.0; buttonConstr.fill=GridBagConstraints.HORIZONTAL;
//		buttonPnl.add(Box.createHorizontalGlue(), buttonConstr);
		
		this.add(buttonPnl, BorderLayout.PAGE_END);
	}
	
	private Action createAddComponentAction()
	{
		AbstractAction createdAction = new AbstractAction(resources.getString("common.action.addFilter"))
		{
			public void actionPerformed(ActionEvent e)
			{
				addComponentToPanel();
				if (refreshAction!=null) {
					refreshAction.putValue(BTN_PROPERTY_HIGHLIGHT, "true");
				}
			}
		};
		
		ActionUtil.addDescriptionsToAction(createdAction, resources.getString("common.action.addFilter.short"), resources.getString("common.action.addFilter.long"));
		return createdAction;		
	}
	
	public void addComponentToPanel()
	{
		this.filterConstaint.gridx = filterPnlCounter % filterColumns;
		
		DeletableOperatorFilterComponent<TEditObj> deleteComponent = createDeletableComponent();
		this.componentList.add(deleteComponent);
		deleteComponent.addToContainer(filterComponentPnl, filterConstaint);
		this.invalidate();
		this.revalidate();
		this.repaint();
		if (refreshAction!=null) {
			refreshAction.putValue(BTN_PROPERTY_HIGHLIGHT, "true");
		}

		filterPnlCounter++;
	}

	protected DeletableOperatorFilterComponent<TEditObj> createDeletableComponent() 
	{
		DeletableOperatorFilterComponent<TEditObj> deleteComponent = new DeletableOperatorFilterComponent<TEditObj>(appMngr, filterClassInfo, resources, getAttributeVisibility(), refreshAction)
		{
			@Override
			public void delete()
			{
				removeComponent(this);
			}
		};
		return deleteComponent;
	}
	
	protected void removeComponent(DeletableOperatorFilterComponent<TEditObj> deletableComponent)
	{
		deletableComponent.removeFromContainer(filterComponentPnl);
		componentList.remove(deletableComponent);
		filterPnlCounter--;
		FilterPanel.this.invalidate();
		FilterPanel.this.revalidate();
		FilterPanel.this.repaint();
		if (refreshAction!=null) {
			refreshAction.putValue(BTN_PROPERTY_HIGHLIGHT, "true");
		}
		
	}
	
	
	public void resetMarker()
	{

		for (DeletableOperatorFilterComponent<TEditObj> operatorComponent : componentList)
		{
			operatorComponent.resetMarker();
		}

	}
	
	public ObjectFilterExpression getFilterExpr()
	{
		ArrayList<ObjectFilterExpression> exprList = new ArrayList<ObjectFilterExpression>();
		for (DeletableOperatorFilterComponent<TEditObj> operatorComponent : componentList)
		{
			if( operatorComponent.hasExpression() ) {
				exprList.add(operatorComponent.getFilterExpr());
			}
		}
		
		return (exprList.size() == 0) ? null : new DefaultExpressionAnd(exprList);
	}

	
	public static void main(String[] args)
	{
		JdyClassInfoModel testClassInfo  = new JdyClassInfoModel();
		testClassInfo.setInternalName("AllAttributeTypes");
		testClassInfo.setExternalName("AllAttributeTypesEx");
		testClassInfo.setShortName("AP");

		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyLongType(new Long(Integer.MIN_VALUE), new Long(Integer.MAX_VALUE)), "IntegerData" , "IntegerDataEx" ,true,true) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyBooleanType(), "BooleanData" , "BooleanDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyBlobType(), "BlobData" , "BlobDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyVarCharType(5000), "ClobData" , "ClobDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyDecimalType(), "CurrencyData" , "CurrencyDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTimeStampType(true, false), "DateData" , "DateDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyFloatType(), "FloatData" , "FloatDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyLongType(), "LongData" , "LongDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(70), "TextData" , "TextDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTimeStampType(), "TimestampData" , "TimestampDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTimeStampType(false, true), "TimeData" , "TimeDataEx" ,false,false) );
		testClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyVarCharType(), "VarCharData" , "VarCharDataEx" ,false,false) );
		
		JdyResourceLoader resources = new JdyResourceLoader()
		{
			public String getString(String aKey)
			{
				return aKey;
			}

			public ImageIcon getIcon(String aFilename)
			{
				return null;
			}

			@Override
			public String getMessage(String aKey, Object... messageArguments)
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
		JFrame testFrame = new JFrame();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.getContentPane().add(new FilterPanel(null, testClassInfo,resources, null));
		testFrame.pack();
		testFrame.setVisible(true);
		
	}
	
	public static class FilterEntry
	{
		private AssociationAttributePath attrInfo; 
		private Object filterValue;
		private ExpressionPrimitiveOperator operator;
		
		public FilterEntry(AssociationAttributePath attrInfo, Object filterValue,ExpressionPrimitiveOperator operator) 
		{
			super();
			this.attrInfo = attrInfo;
			this.filterValue = filterValue;
			this.operator = operator;
		}
		
		public AssociationAttributePath getAttrInfo() 
		{
			return attrInfo;
		}
		
		public Object getFilterValue() 
		{
			return filterValue;
		}
		
		public ExpressionPrimitiveOperator getOperator() 
		{
			return operator;
		}
	}
}
