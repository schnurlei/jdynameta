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
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.jdynameta.application.ApplicationManager;
import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.metainfo.ValueModeSavePanel;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.PersistentObjectList;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

/**
 * 
 * @author rainer
 *
 * @param <TEditObj>
 */
@SuppressWarnings("serial")
public class AppObjNode<TEditObj extends ApplicationObj> extends DefaultMutableTreeNode implements ValueObjectTreePanel.TreeElement<TEditObj>
{
	private static final PropertyNameCreator propertyCreator = new DefaultPropertyNameCreator();
	private final TEditObj displayedObj;
	private final ApplicationManager<TEditObj> appMngr;
	private boolean showAssocDirect = false; 
	private PanelManager pnlMngr;
	private JComponent	parentPnl;    	
	private ValueObjectTreeModel<TEditObj> treeModel;

	public AppObjNode(TEditObj aCurAppObj, ApplicationManager<TEditObj> anAppMngr, PanelManager aPnlMngr, ValueObjectTreeModel<TEditObj> aTreeModel)
	{
		super(aCurAppObj);
		this.displayedObj = aCurAppObj;
		this.appMngr = anAppMngr;
		this.pnlMngr = aPnlMngr;
		this.parentPnl = null;
		this.treeModel = aTreeModel;
	}

	@Override
	public int getChildCount()
	{
		if( children == null) {
			
			super.children = new Vector();

			int i= 0;
			for( AssociationInfo curAssoc : displayedObj.getClassInfo().getAssociationInfoIterator()) {
				
				if( showAssocDirect) {
					final PersistentObjectList<TEditObj> allAssocObjects  = (PersistentObjectList<TEditObj>) displayedObj.getValue(curAssoc);

					for (Iterator<TEditObj> iterator = allAssocObjects.iterator(); iterator.hasNext();) {
						TEditObj curAppObj = iterator.next();
						treeModel.insertNodeInto(new AppObjNode<TEditObj>(curAppObj, appMngr, pnlMngr, treeModel), AppObjNode.this, i++);
					}	
					
					allAssocObjects.addObjectListModelListener(new ObjectListModelListener<TEditObj>()
					{
						@Override
						public void intervalAdded(ObjectListModelEvent<TEditObj> aEvent)
						{
							for( int start = aEvent.getLowerIndex(); start <= aEvent.getLowerIndex(); start++) {
								treeModel.insertNodeInto(new AppObjNode<TEditObj>(allAssocObjects.get(start), appMngr, pnlMngr, treeModel),AppObjNode.this, AppObjNode.this.getChildCount());
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
								TEditObj changedObj = allAssocObjects.get(start);
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
						}
					});
					
				} else {
					try {
						insert(new AssociationNode<TEditObj>(displayedObj, curAssoc, appMngr, pnlMngr, treeModel), i++);
					} catch (JdyPersistentException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			}
			
		}
		return (super.getChildCount());
	}
	
	@Override
	public boolean isLeaf()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return ValueObjectTreePanel.getTextForAttribute(displayedObj, this.appMngr, displayedObj.getClassInfo());
	}
	
	@Override
	public List<Action> getNodeActions()
	{
		List<Action> actionList = new ArrayList<Action>();
		if( showAssocDirect) {
			for( AssociationInfo curAssoc : displayedObj.getClassInfo().getAssociationInfoIterator()) {
				
				addClassAndSubclassCreateActions(actionList, curAssoc, curAssoc.getDetailClass());
			}
		}
		actionList.add(createEditAction(pnlMngr.res()));
		actionList.add(createDeleteAction(pnlMngr.res()));
		
		return actionList;
	}
	
	@Override
	public ClassInfo getClassInfo() 
	{
		return this.displayedObj.getClassInfo();
	}

   	@Override
	public TEditObj getValueObject() 
	{
		return this.displayedObj;
	}

	
	private void addClassAndSubclassCreateActions(List<Action> aListToAddActions, final AssociationInfo anAssoc, final ClassInfo aSubClass)
	{
		if( !aSubClass.isAbstract()) {
			aListToAddActions.add(createNewObjectAction(aSubClass, anAssoc));
		}
		for (ClassInfo subclass : aSubClass.getAllSubclasses()) {
			addClassAndSubclassCreateActions(aListToAddActions, anAssoc, subclass);
		}
	}
	
	@SuppressWarnings("serial")
	private Action createDeleteAction(final JdyResourceLoader aResourceLoader)
	{
		return  new AbstractAction( aResourceLoader.getString("common.action.delete"), aResourceLoader.getIcon(ApplicationIcons.DELETE_ICON))
		{
			public void actionPerformed(ActionEvent event) 
			{
				if ( JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(parentPnl, pnlMngr.res().getString("common.confirm.delete"), pnlMngr.res().getString("common.confirm.delete.title"), JOptionPane.YES_NO_OPTION))  
				{
					
					try
					{
						appMngr.deleteObject(displayedObj, null);
						
					} catch (JdyPersistentException ex)
					{
						pnlMngr.displayErrorDialog(parentPnl, ex.getLocalizedMessage(), ex);
					}
				}
			}

		};
	}
	
	
	private Action createEditAction(final JdyResourceLoader aResourceLoader)
	{

		return  new AbstractAction(aResourceLoader.getString("common.action.edit_selected"), aResourceLoader.getIcon(ApplicationIcons.EDIT_ICON))
		{
			public void actionPerformed(ActionEvent event) 
			{
				try {
					showEditObjectPanel( displayedObj, null);
				} catch (JdyPersistentException ex) {
					pnlMngr.displayErrorDialog(parentPnl, ex.getLocalizedMessage(), ex);
				}
			}
		};
	}
	
	
	private Action createNewObjectAction(final ClassInfo subclass, final AssociationInfo anAssoc)
	{
		String classNameProperty = propertyCreator.getPropertyNameFor(subclass);
		
		AbstractAction newAction = new AbstractAction(pnlMngr.res().getMessage("common.action.create",  pnlMngr.res().getString(classNameProperty)),
				pnlMngr.res().getIcon(ApplicationIcons.NEW_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					TEditObj newObject = appMngr.createObject(subclass, null);
					newObject.setValue(anAssoc.getMasterClassReference(), displayedObj);
					showEditObjectPanel(newObject, anAssoc);
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

	private void showEditObjectPanel(TEditObj objectObj, final AssociationInfo anAssoc) throws JdyPersistentException
	{
		ColumnVisibilityDef visibility = new ColumnVisibilityDef()
		{
			public boolean isAttributeVisible(AttributeInfo aAttrInfo)
			{
				return ( anAssoc == null || !anAssoc.getMasterClassReference().equals(aAttrInfo));
			}
			
			@Override
			public boolean isAssociationVisible(AssociationInfo aAttrInfo) 
			{
				return false;
			}			
		};
		
		
		ValueModeSavePanel< TEditObj> newObjectEditPnl = new ValueModeSavePanel<TEditObj>(appMngr, objectObj.getClassInfo(), pnlMngr,	null);
		newObjectEditPnl.setCreateComponentStrategy(treeModel.getCreationStrat(objectObj));

		newObjectEditPnl.getPersValueHolder().setEditedObject(objectObj);
		newObjectEditPnl.setColumnVisibility(visibility);
		
		pnlMngr.displayDialog( newObjectEditPnl,"Create", new Dimension(400,300), false, parentPnl);
	}
	
}