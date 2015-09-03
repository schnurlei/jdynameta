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
package de.jdynameta.metainfoview.tree;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.attribute.model.PersListenerQueryObjectListModel;
import de.jdynameta.metainfoview.metainfo.ValueModeSavePanel;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

/**
 * 
 * @author rainer
 *
 * @param <TEditObj>
 */
@SuppressWarnings("serial")
public class ClassInfoNode<TEditObj extends ApplicationObj> extends DefaultMutableTreeNode implements ValueObjectTreePanel.TreeElement<TEditObj>
{
	private static final PropertyNameCreator propertyCreator = new DefaultPropertyNameCreator();
	private ObjectListModelListener<TEditObj> objListListener;
	private PersListenerQueryObjectListModel<TEditObj> allObjects = null;
	private final ApplicationManager<TEditObj>	appMngr;
	private final ClassInfo	classInfo;
	private PanelManager pnlMngr;
	private JComponent	parentPnl;    	
	private ValueObjectTreeModel<TEditObj> treeModel;

	public ClassInfoNode(ClassInfo aClassInfo, ApplicationManager<TEditObj> anAppMngr, PanelManager aPnlMngr, ValueObjectTreeModel<TEditObj> aTreeModel, JComponent aParentPnl) throws JdyPersistentException
	{
		super(aClassInfo, true);
		this.appMngr = anAppMngr;
		this.classInfo = aClassInfo;	
		this.pnlMngr = aPnlMngr;
		this.parentPnl = aParentPnl;
		this.treeModel = aTreeModel;
		createObjectListListener();			
	}

	/**
	 * 
	 */
	protected void createObjectListListener() 
	{
		this.objListListener = new ObjectListModelListener<TEditObj>()
		{
			@Override
			public void intervalAdded(ObjectListModelEvent<TEditObj> aEvent)
			{
				for( int start = aEvent.getLowerIndex(); start <= aEvent.getLowerIndex(); start++) {
					treeModel.insertNodeInto(new AppObjNode<TEditObj>(allObjects.get(start), appMngr, pnlMngr, treeModel),ClassInfoNode.this,start);
				}
			}

			@Override
			public void intervalRemoved(ObjectListModelEvent<TEditObj> aEvent)
			{
				if( aEvent.getRemovedObjects() != null ) {
					for (TEditObj removedObj : aEvent.getRemovedObjects()) {
						Object objectToRemove = null;
						for (Object childNode : children) {
							if ( removedObj.equals(((ValueObjectTreePanel.TreeElement) childNode).getValueObject()) ) {
								objectToRemove = childNode;
							}
							if( objectToRemove != null) {
								treeModel.removeNodeFromParent((MutableTreeNode) objectToRemove);
								break;
							}
						}
					}
				}
			}

			@Override
			public void intervalUpdated(ObjectListModelEvent<TEditObj> aEvent)
			{
				for( int start = aEvent.getLowerIndex(); start <= aEvent.getLowerIndex(); start++) {
					TEditObj changedObj = allObjects.get(start);
					for (Object childNode : children) {
						if ( changedObj.equals(((ValueObjectTreePanel.TreeElement) childNode).getValueObject()) ) {
							treeModel.nodeChanged((TreeNode) childNode);
						}
					}
					
				}
				
			}

			@Override
			public void contentsChanged(ObjectListModelEvent<TEditObj> aEvent)
			{
				Runnable doWorkRunnable = new Runnable() 
				{
				    public void run() 
				    { 
				    }
				};

				SwingUtilities.invokeLater(doWorkRunnable);
		    	if( children != null) {
					int[] childIndices = new int[children.size()];
					for(int i =0; i < children.size(); i++) {
						childIndices[i] = i;
					}
					Object[] removedChildren = children.toArray();
					children.clear();
					treeModel.nodesWereRemoved(ClassInfoNode.this,childIndices , removedChildren);
					createChildNodes();						
		    	}
				
			}
		};
	}

	public boolean isLeaf()
	{
		return false;
	}

	public int getChildCount()
	{
		if (children == null) {
			children = new Vector();
			createChildNodes();	
		}
		return (super.getChildCount());
	}

	/**
	 * 
	 */
	protected void createChildNodes() {
		try {
			if( allObjects == null) {
				allObjects = new PersListenerQueryObjectListModel<TEditObj>(appMngr, new  DefaultClassInfoQuery(classInfo));
				allObjects.refresh();
				allObjects.addObjectListModelListener(objListListener);
			}
			int i = 0;
			for (Iterator<TEditObj> iterator = allObjects.iterator(); iterator.hasNext();) {
				TEditObj curAppObj = iterator.next();
				treeModel.insertNodeInto(new AppObjNode<TEditObj>(curAppObj, appMngr,pnlMngr, treeModel),ClassInfoNode.this, i++);
			}
		} catch (JdyPersistentException ex) {
			ex.printStackTrace();
		}
	}

	public String toString()
	{
		return classInfo.getInternalName();
	}
	
	@Override
	public List<Action> getNodeActions()
	{
		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(createRefreshObjectsAction());
		actions.add(createNewObjectAction());
		return actions;
	}
	
	
	@Override
	public ClassInfo getClassInfo() 
	{
		return this.classInfo;
	}
	
	@Override
	public TEditObj getValueObject() 
	{
		return null;
	}
	
	private Action createNewObjectAction()
	{
		String classNameProperty = propertyCreator.getPropertyNameFor(classInfo);
		
		AbstractAction newAction = new AbstractAction(pnlMngr.res().getMessage("common.action.create", pnlMngr.res().getString(classNameProperty) ),
				pnlMngr.res().getIcon(ApplicationIcons.NEW_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					TEditObj newObject = appMngr.createObject(classInfo, null);
					if (newObject != null)
					{
						showEditObjectPanel(newObject);
					}
				} catch (JdyPersistentException ex)
				{
					pnlMngr.displayErrorDialog(parentPnl, ex.getLocalizedMessage(), ex);
				} catch (ObjectCreationException ex)
				{
					pnlMngr.displayErrorDialog(parentPnl, ex.getLocalizedMessage(), ex);
				}
			}
		};

		ActionUtil.addDescriptionsToAction(newAction, pnlMngr.res().getString(
				"common.action.new.short"), pnlMngr.res().getString("common.action.new.short"));

		return newAction;
	}
	
	private Action createRefreshObjectsAction()
	{
		AbstractAction tmpRefreshAction = new AbstractAction(pnlMngr.res().getString("common.action.refresh"), pnlMngr.res().getIcon(ApplicationIcons.REFRESH_ICON))
		{
			public void actionPerformed(ActionEvent aE)
			{
				try {
					if( allObjects != null) {
						allObjects.refresh();
					}
				} catch (JdyPersistentException ex) {
					pnlMngr.displayErrorDialog(parentPnl, ex.getLocalizedMessage(), ex);
				}
			}
		};
		
		ActionUtil.addDescriptionsToAction(tmpRefreshAction, pnlMngr.res().getString("common.action.refresh.short")
												, pnlMngr.res().getString("common.action.refresh.long"));		
		return tmpRefreshAction;
	}
	
	
	private void showEditObjectPanel(TEditObj objectObj) throws JdyPersistentException
	{
		ColumnVisibilityDef visibility = new ColumnVisibilityDef()
		{
			public boolean isAttributeVisible(AttributeInfo aAttrInfo)
			{
				return true;
			}
			
			@Override
			public boolean isAssociationVisible(AssociationInfo aAttrInfo) 
			{
				return false;
			}
		};
			
		ValueModeSavePanel< TEditObj> newObjectEditPnl = new ValueModeSavePanel<TEditObj>(appMngr, classInfo, pnlMngr,	null);
		newObjectEditPnl.setCreateComponentStrategy(treeModel.getCreationStrat(objectObj));

		newObjectEditPnl.getPersValueHolder().setEditedObject(objectObj);
		newObjectEditPnl.setColumnVisibility(visibility);
		
		pnlMngr.displayDialog( newObjectEditPnl,"Create", new Dimension(400,300), false, parentPnl);
	}
	
}