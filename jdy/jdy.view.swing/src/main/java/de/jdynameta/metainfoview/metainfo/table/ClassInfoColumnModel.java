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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.base.PropertyManager;

/**
 * ColumModel which display the Columns of a ClassInfo
 * in a Table. Defines the Render, Column Name, visible Columns
 * and adds a Marker Column
 * @author Rainer Schneider
 *
 */
public class ClassInfoColumnModel extends DefaultTableColumnModel 
{
	private PropertyNameCreator propertyGenerator;
	private Map<Integer, AttributeInfo> modelIdx2AtrInfoMap;
	private Map<Integer, AssociationInfo> modelIdx2AssocInfoMap;
	private ClassInfoAttrSource displayAttributs;
	private final ClassInfo classInfo;
	private final ColumnVisibilityDef visibilityDef;
	private final PanelManager panelManager;
	private RendererCreationStrategy  rendererCreateStrategy;
	
	/**
	 * Constructor for ClassInfoColumnModel.
	 */
	public ClassInfoColumnModel(ClassInfo aClassInfo, ClassInfoAttrSource aDisplayAttributs, PanelManager aPanelManager) 
	{
		this(aClassInfo, null, aDisplayAttributs, aPanelManager, null);
	}

	/**
	 * Constructor for ClassInfoColumnModel.
	 */
	public ClassInfoColumnModel(ClassInfo aClassInfo, ColumnVisibilityDef aVisibilltyDef
								, ClassInfoAttrSource aDisplayAttributs
								, PanelManager aPanelManager
								, RendererCreationStrategy renderStrat) 
	{
		super();
		assert(aClassInfo != null);
		assert(aPanelManager != null);
		
		this.modelIdx2AtrInfoMap = new HashMap<Integer, AttributeInfo>();
		this.modelIdx2AssocInfoMap = new HashMap<Integer, AssociationInfo>();
		this.propertyGenerator = new DefaultPropertyNameCreator();
		this.displayAttributs = aDisplayAttributs;
		this.classInfo = aClassInfo;
		this.panelManager = aPanelManager;
		this.visibilityDef = aVisibilltyDef;
		this.rendererCreateStrategy = renderStrat;
		if( rendererCreateStrategy == null) {
			rendererCreateStrategy = createRendererCreationStrategy();
		}

		this.createColumns();
	}
	
	protected ClassInfo getClassInfo() 
	{
		return classInfo;
	}
	
	public ColumnVisibilityDef getVisibilityDef() 
	{
		return visibilityDef;
	}
	
	public RendererCreationStrategy getRendererCreateStrategy() 
	{
		return rendererCreateStrategy;
	}
	
	protected RendererCreationStrategy createRendererCreationStrategy()
	{
		return new RendererCreationStrategyDefault();
	}
	
	public PropertyNameCreator getPropertyGenerator() 
	{
		return propertyGenerator;
	}
	
	private void createColumns()
	{
		int modelIndex = 0;
		createMarkerColumn(modelIndex++);
		modelIndex = creatAttributeColumns(this.classInfo, this.visibilityDef, this.panelManager, modelIndex);
		modelIndex = creatAssocitaionColumns(this.classInfo, this.visibilityDef, this.panelManager, modelIndex);
		List<TableColumn> specialCols = createSpecialColumns(modelIndex, this.panelManager.getResourceLoader());
		if( specialCols != null) {
			for (TableColumn tableColumn : specialCols) {
				this.addColumn(tableColumn);	
			}
		}
	}
	
	/**
	 * create columns for all attribute in the model not excluded by aVisibilltyDef
	 * @param aClassInfo
	 * @param aVisibilltyDef
	 * @param aPanelManager
	 * @param modelIndex
	 * @return
	 */
	private int creatAttributeColumns(ClassInfo aClassInfo, ColumnVisibilityDef aVisibilltyDef, PanelManager aPanelManager, int modelIndex)
	{
		for( AttributeInfo curAttribute: aClassInfo.getAttributeInfoIterator()) {
			if(aVisibilltyDef == null || aVisibilltyDef.isAttributeVisible(curAttribute) ) {
				TableColumn newColumn =  createColumnForAttributeInfo( aClassInfo, curAttribute, aPanelManager.res());
				newColumn.setModelIndex(modelIndex);
				modelIdx2AtrInfoMap.put(modelIndex, curAttribute);
				modelIndex++;
				this.addColumn(newColumn);	
			}
		}
		
		return modelIndex;
	}

	/**
	 * Create columns for all associations for which createColumnForAssociationInfo is implemented
	 * for default not assoc columns are created
	 * @param aClassInfo
	 * @param aVisibilltyDef
	 * @param aPanelManager
	 * @param modelIndex
	 * @return
	 */
	private int creatAssocitaionColumns(ClassInfo aClassInfo, ColumnVisibilityDef aVisibilltyDef, PanelManager aPanelManager, int modelIndex)
	{
		for (AssociationInfo curAssoc :  aClassInfo.getAssociationInfoIterator()) {
			TableColumn newColumn =  createColumnForAssociationInfo(modelIndex++, aClassInfo, curAssoc, aPanelManager.getResourceLoader());
			if ( newColumn != null) {
				newColumn.setModelIndex(modelIndex);
				modelIdx2AssocInfoMap.put(modelIndex, curAssoc);
				modelIndex++;
				this.addColumn(newColumn);
			}
		}
		
		return modelIndex;
	}
	
	protected List<TableColumn> createSpecialColumns(int modelIndex, JdyResourceLoader aResourceLoader )
	{
		return null;
	}
	
	
	/**
	 * read the Column configuration from the Property Manager and restore column size and pos
	 * @param aPnl
	 * @param propertyManager
	 */
	public void restoreColumnStateFromConfig(final ManagedPanel aPnl, final PropertyManager propertyManager)
	{
		for (AttributeInfo info : this.classInfo.getAttributeInfoIterator())
		{
			String property = "table." + classInfo.getInternalName() + "." +info.getInternalName();
			Integer width = propertyManager.getIntPropertyFor(aPnl, property+".width");
			Integer pos = propertyManager.getIntPropertyFor(aPnl, property+".pos");
			if( width != null && pos != null ) {
				int modelIdx = getModelIdxForAttributeInfo(info);
				if( getColumnAtModelIdx(modelIdx) != null) {
					getColumnAtModelIdx(modelIdx).setWidth(width);
					getColumnAtModelIdx(modelIdx).setPreferredWidth(width);
					try {
						moveColumn(getColumnIdxAtModelIdx(modelIdx),pos);
					} catch (IllegalArgumentException e) {
						// index out of range
					}
				}
			}
		}
	}

	/**
	 * listen to changes of column pos and width and write them to the config
	 * @param aPnl
	 * @param propertyManager
	 */
	public void writeColumnChangesToConfig(final ManagedPanel aPnl, final PropertyManager propertyManager)
	{
		this.addColumnModelListener(new TableColumnModelListener()
		{
			public void columnAdded(TableColumnModelEvent e)
			{
				saveColumnStateToConfig(aPnl, propertyManager);
			}
			public void columnRemoved(TableColumnModelEvent e)
			{
				saveColumnStateToConfig(aPnl, propertyManager);
			}
			public void columnMarginChanged(ChangeEvent e)
			{
			}
			public void columnMoved(TableColumnModelEvent e)
			{
				saveColumnStateToConfig(aPnl, propertyManager);
			}
			public void columnSelectionChanged(ListSelectionEvent e)
			{
			}			
		});

		for (Enumeration<TableColumn> colEnum = getColumns(); colEnum.hasMoreElements();)
		{
			TableColumn curCol = colEnum.nextElement();
			curCol.addPropertyChangeListener(new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveColumnStateToConfig(aPnl, propertyManager);
				}
			});
		}
	}

	
	public void saveColumnStateToConfig(ManagedPanel aPnl, PropertyManager propertyManager)
	{
		for (int i = 0; i < getColumnCount(); i++)
		{
			TableColumn curColmn = getColumn(i);
			curColmn.getWidth();
			AttributeInfo info = modelIdx2AtrInfoMap.get(curColmn.getModelIndex());
			if( info != null) {
				String property = "table." + classInfo.getInternalName() + "." +info.getInternalName();
				propertyManager.setIntPropertyFor(aPnl, property+".width", curColmn.getWidth());
				propertyManager.setIntPropertyFor(aPnl, property+".pos", i);
			}
		}
		propertyManager.saveProperties();
		
	}
	
	
	public AttributeInfo getAttributeInfoForModelIdx(int modelIdx)
	{
		return modelIdx2AtrInfoMap.get(modelIdx);
	}
	
	public AssociationInfo getAssociationForModelIdx(int modelIdx)
	{
		return modelIdx2AssocInfoMap.get(modelIdx);
	}

	
//	public<TDirective extends ValueObject> ClassInfoTableModel.Directive createSortDirectiveForModelIdx(int aModelColIdx, int direction, int aSortPos)
//	{
//		return new ClassInfoTableModel.AttributeDirective<TDirective>(aModelColIdx, direction, aSortPos, getAttributeInfoForModelIdx(aModelColIdx));
//	}
	
	private int getModelIdxForAttributeInfo(AttributeInfo aInfo)
	{
		for (Map.Entry<Integer, AttributeInfo> curEntry : modelIdx2AtrInfoMap.entrySet())
		{
			if( curEntry.getValue().equals(aInfo)) {
				return curEntry.getKey();
			}
		}
		return -1;
	}
	
	private TableColumn getColumnAtModelIdx(int modelIdx)
	{
		for (int i = 0; i < getColumnCount(); i++)
		{
			TableColumn curColmn = getColumn(i);
			if( curColmn.getModelIndex() == modelIdx) {
				return curColmn;
			}
		}
		
		return null;
	}
	
	private int getColumnIdxAtModelIdx(int modelIdx)
	{
		for (int i = 0; i < getColumnCount(); i++)
		{
			TableColumn curColmn = getColumn(i);
			if( curColmn.getModelIndex() == modelIdx) {
				return i;
			}
		}
		
		return -1;
	}	
	
	/**
	 * Set the first column as a marker
	 *
	 */
	protected void createMarkerColumn(int aModelIdx) 
	{
       // add Marker Column
	   int colSize = 40;
	   TableColumn newColumn = new TableColumn(aModelIdx, colSize, createMarkerRenderer(), null);
	   newColumn.setHeaderValue("");
	   newColumn.setMaxWidth(colSize);newColumn.setMinWidth(colSize);
	   newColumn.setWidth(colSize);
	   this.addColumn(newColumn);
	}
	
	protected TableCellRenderer createMarkerRenderer()
	{
		return rendererCreateStrategy.createMarkerRenderer();
	}
	
	
	protected TableColumn createColumnForAttributeInfo( ClassInfo aClassInfo, AttributeInfo aInfo, JdyResourceLoader aResourceLoader) 
	{
		TableColumn resultColumn = new TableColumn();
		
		String columnNameProperty =this.propertyGenerator.getPropertyNameFor(aClassInfo, aInfo);
		
		resultColumn.setHeaderValue(aResourceLoader.getString(columnNameProperty));
		resultColumn.setCellRenderer(rendererCreateStrategy.createRendererFor(aInfo, displayAttributs));
		resultColumn.setPreferredWidth(150);

		
		return resultColumn;
	}

	protected TableColumn createColumnForAssociationInfo(int i,ClassInfo classInfo2, AssociationInfo curAssoc,JdyResourceLoader resourceLoader) 
	{
		return null;
	}


}