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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.IdentifierHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.swingx.search.AbstractSearchable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.metainfoview.metainfo.table.RendererCreationStrategy;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class SwingxTablePanel<TEditObj extends ValueObject> extends ManagedPanel
{
	private final ClassInfo displayedClassInfo;
	private final SwingxClassInfoTableModel<TEditObj> tableModel;
	private final PanelManager panelManager;
	private final JXTable objectTbl; 
	private EditObjectHandler<TEditObj> editHandler;
	private final Action editAction;
	private JToolBar toolBar; 
	final private JXFindBar searchPanel;
	private final ObjectListModel<TEditObj> objectList;
	private ClassInfoAttrSource attrSource;
	
	/**
	 * 
	 */
	public SwingxTablePanel(ClassInfo aDisplayedClassInfo
					, PanelManager aPanelManger
					, ClassInfoAttrSource aAttrSource
					, ObjectListModel<TEditObj> aQueryList
					) throws JdyPersistentException
	{
		super();
	
		this.panelManager = aPanelManger;
		this.displayedClassInfo = aDisplayedClassInfo;
		this.attrSource = aAttrSource;
		this.setName(aDisplayedClassInfo.getInternalName());

		
		SwingxClassInfoColumnModel colModel = new SwingxClassInfoColumnModel(this.displayedClassInfo,this.attrSource, aPanelManger);
//		colModel.restoreColumnStateFromConfig(this, aPanelManger.getPropertyManager());
//		colModel.writeColumnChangesToConfig(this, aPanelManger.getPropertyManager());

		this.tableModel = new SwingxClassInfoTableModel<TEditObj>(colModel);	
		this.objectList = aQueryList;
		this.tableModel.setListModel(aQueryList);
		
		this.objectTbl = new JXTable( tableModel,colModel);
		this.objectTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.objectTbl.setRowHeight(20);
		this.objectTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.objectTbl.setColumnControlVisible(true);
		this.objectTbl.addMouseListener(	new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent event) {
	            mouseClickInTable(event);
            }
    	});
		searchPanel = new SwingxFindBar(aPanelManger.res());
		searchPanel.setSearchable(objectTbl.getSearchable());
		
		
		Highlighter rolloverHigh =   new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,new Color(0, 180, 0, 80), null);
		this.objectTbl.setHighlighters(createAlternateStriping(), rolloverHigh);

		objectTbl.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
		Color matchColor = HighlighterFactory.LINE_PRINTER;
		((AbstractSearchable) objectTbl.getSearchable()).setMatchHighlighter(new ColorHighlighter(matchColor, null, matchColor, Color.BLACK));		
		
		this.editAction = createEditObjectAction(aPanelManger.res());

		this.objectTbl.getSelectionModel().addListSelectionListener(createSelectionListener());
		this.toolBar = createToolBar();
		
		initUi();	
		enableActions();		
	}

	public ClassInfo getDisplayedClassInfo() 
	{
		return displayedClassInfo;
	}
	
	public ObjectListModel<TEditObj> getObjectList() 
	{
		return objectList;
	}
	
	/**
	 * 
	 * @param isMulteSelection
	 */
	public void setMultiSelection(boolean isMulteSelection)
	{
		if( isMulteSelection ) {
			this.objectTbl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			this.objectTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}
	
	public void setVisibilityDef(ColumnVisibilityDef visibilityDef) 
	{
		RendererCreationStrategy renderStrat = null;
		if( this.objectTbl.getColumnModel() instanceof SwingxClassInfoColumnModel) {
			renderStrat = ((SwingxClassInfoColumnModel) this.objectTbl.getColumnModel()).getRendererCreateStrategy();
		}
		SwingxClassInfoColumnModel colModel = new SwingxClassInfoColumnModel(this.displayedClassInfo,visibilityDef,attrSource, panelManager, renderStrat);
		colModel.restoreColumnStateFromConfig(this, panelManager.getPropertyManager());
		colModel.writeColumnChangesToConfig(this, panelManager.getPropertyManager());
		this.objectTbl.setColumnModel(colModel);
	}
	
	public void setRendererCreationStrategy( RendererCreationStrategy aRenderStrat ) 
	{
		ColumnVisibilityDef visibilityDef = null;
		if( this.objectTbl.getColumnModel() instanceof SwingxClassInfoColumnModel) {
			visibilityDef = ((SwingxClassInfoColumnModel) this.objectTbl.getColumnModel()).getVisibilityDef();
		}
		SwingxClassInfoColumnModel colModel = new SwingxClassInfoColumnModel(this.displayedClassInfo,visibilityDef,attrSource, panelManager, aRenderStrat);
		colModel.restoreColumnStateFromConfig(this, panelManager.getPropertyManager());
		colModel.writeColumnChangesToConfig(this, panelManager.getPropertyManager());
		this.objectTbl.setColumnModel(colModel);
	}
	
	
	/**
	 * Enable/Disable actions depending on the selection state
	 *
	 */
	private void enableActions() 
	{
		if( editHandler != null) {
			editAction.setEnabled( (getSelectedObjectCount() ==  1 && editHandler.isObjectEditable(getSelectedObject())));
		} else {
			editAction.setEnabled(getSelectedObjectCount() ==  1);
		}
	}
	
    private void mouseClickInTable(MouseEvent event) {

    	// edit object on double click
        if ( event.getClickCount() > 1 ) {
        	this.editSelectedObject();
        }
    }

	private ListSelectionListener createSelectionListener()
	{
		return new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				if( !event.getValueIsAdjusting() ) {
					
					enableActions();
					if( editHandler != null) {
						editHandler.selectionChanged();
					} 
				}
			}
		};
	}
	
	protected boolean isSelectedObjectEditable(TEditObj aSelectedObj)
	{
		return aSelectedObj != null;
	}
	
	private Action createEditObjectAction( JdyResourceLoader aResourceLoader)
	{
		AbstractAction tmpEditAction = new AbstractAction(aResourceLoader.getString("common.action.edit_selected"), aResourceLoader.getIcon(ApplicationIcons.EDIT_ICON))
		{
			public void actionPerformed(ActionEvent aE)
			{
				editSelectedObject();
			}
		};
		
		ActionUtil.addDescriptionsToAction(tmpEditAction, aResourceLoader.getString("common.action.edit_selected.short")
												, aResourceLoader.getString("common.action.edit_selected.long"));		
		return tmpEditAction;
	}
	
	public void setEditObjectActionName( String aName, Icon icon,  String aShortDesc, String aLongDesc)
	{
		ActionUtil.setActionName(this.editAction, aName, icon,  aShortDesc, aLongDesc);		

	}

	
	/**
	 * Get first selected Object 
	 * @return
	 * @throws ProxyResolveException
	 */
	public TEditObj getSelectedObject() throws ProxyResolveException
	{
		return (this.objectTbl.getSelectedRow() >=0) ? this.tableModel.getObjectAt(this.objectTbl.getSelectedRow()) : null; 
	}
	
	/**
	 * Get all Selected Object. Set Table to multi selection mode to get more than one Object
	 * @return
	 * @throws ProxyResolveException
	 */
	public List<TEditObj> getAllSelectedObject() throws ProxyResolveException
	{
		ArrayList<TEditObj> selObjectColl = new ArrayList<TEditObj>();
		int[] selectedIdx = this.objectTbl.getSelectedRows();
		
		for (int i : selectedIdx) {
			selObjectColl.add(this.tableModel.getObjectAt(i));
		}
		return selObjectColl; 
	}
	
	public TEditObj getObjectAt(int aIndex)
	{
		return this.tableModel.getObjectAt(aIndex);
	}
	
	public int getObjectCount()
	{
		return this.tableModel.getRowCount();
	}
	
	
	public int getSelectedObjectCount()
	{
		return this.objectTbl.getSelectedRowCount();
	}
	
	public int getObjectIndex(TEditObj objToSearch)
	{
		int result = -1;
		for( int i = this.tableModel.getRowCount()-1; i >=0; i--) {

			if ( this.tableModel.getObjectAt(i).equals(objToSearch) ) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	public void setEditObjectHandler(EditObjectHandler<TEditObj> aEditObjectHandler)
	{
		this.editHandler = aEditObjectHandler;
		enableActions();
	}

	public Action getEditObjectAction()
	{
		return this.editAction;
	}
	
	
	private void editSelectedObject()
	{
		if( editHandler != null) { 
			TEditObj modelFromDb =  getSelectedObject();
			if( modelFromDb != null) {
				editHandler.editObject(modelFromDb);
			}
		}
	}
	
	private void initUi()
	{
		this.setLayout(new BorderLayout());

		JPanel headerPnl = new JPanel(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1,1
										,0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE
										, new Insets(0,0,0,0), 0,0);
		
		constr.weightx = 1.0; constr.fill = GridBagConstraints.BOTH;
		headerPnl.add(this.toolBar,constr);
		constr.weightx = 0.0; constr.fill = GridBagConstraints.NONE;
		headerPnl.add(searchPanel,constr);

		
		this.add(headerPnl, BorderLayout.PAGE_START);
		this.add(new JScrollPane(this.objectTbl), BorderLayout.CENTER);
	}

	
	private JToolBar createToolBar()
	{
		JToolBar editToolbar = new JToolBar();
		editToolbar.setFloatable(false);
		editToolbar.setVisible(true);
		return editToolbar;
	}
	
	public void showToolbar(boolean isVisible)
	{
		this.toolBar.setVisible(isVisible);
	}

	public void addActionToToolbar(Action anActionToAdd)
	{
		this.toolBar.add(anActionToAdd);
	}
	
	public void addSeparatorToToolbar()
	{
		this.toolBar.addSeparator();
	}
	
	
	/**
	 * Set the title of the panel 
	 * @subclass use this Method to set the title of the Panel
	 */
	@Override
	public void setTitle(String aNewTitle) 
	{
		super.setTitle(aNewTitle);
	}

	public void selectRow(int rowNo) 
	{
		if (0 <= rowNo && rowNo < objectTbl.getRowCount()) {
			this.objectTbl.setRowSelectionInterval(rowNo, rowNo);

			final Rectangle selectedRect = objectTbl.getCellRect(rowNo, 0, true);
			if( selectedRect != null) {
				objectTbl.scrollRectToVisible(selectedRect);
			}

		} 
	}

    public static Highlighter createAlternateStriping() 
    {
    	NotHighlightPredicate markerPredicate = new NotHighlightPredicate(new IdentifierHighlightPredicate(SwingxClassInfoColumnModel.MARKER_IDENTIFIER));
        ColorHighlighter first = new ColorHighlighter(new HighlightPredicate.AndHighlightPredicate (markerPredicate,HighlightPredicate.EVEN), Color.WHITE, null);
        ColorHighlighter hl = new UIColorHighlighter(HighlightPredicate.ODD);
        return new CompoundHighlighter(first, hl);
    }
	
	
	
	public static interface EditObjectHandler<TEditObj extends ValueObject>
	{
		public void editObject(TEditObj anObject);
		public void selectionChanged();
		public boolean isObjectEditable(TEditObj anObject);
	}
	
	/**
	 * 
	 * @author rs
	 *
	 */
	public  static class SwingxFindBar extends JXFindBar
	{
		JdyResourceLoader res;
		
		public SwingxFindBar(JdyResourceLoader jdyResourceLoader) 
		{
			super();
			res = jdyResourceLoader;
		}

		@Override
		protected void bind() 
		{
			super.bind();
			
		}
		
	    @Override
	    protected void build() {
	        setLayout(new BorderLayout());
	        JToolBar searchToolBar = new JToolBar();
	        searchToolBar.setFloatable(false);
	        add(searchToolBar, BorderLayout.CENTER);
	        
	        searchToolBar.add(searchLabel);
	        searchToolBar.add(new JLabel(": "));
	        searchToolBar.add(searchField);
	        JButton preBtn = searchToolBar.add(getAction(FIND_PREVIOUS_ACTION_COMMAND));
	        preBtn.setText("");
	        preBtn.setIcon(res.getIcon(ApplicationIcons.FIND_PREVIOUS_ICON));
	        JButton nextBtn = searchToolBar.add(getAction(FIND_NEXT_ACTION_COMMAND));
	        nextBtn.setText("");
	        nextBtn.setIcon(res.getIcon(ApplicationIcons.FIND_NEXT_ICON));
	    }
		
	}
	
}



