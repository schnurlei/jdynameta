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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

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
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

@SuppressWarnings("serial")
public class AssociationNode<TEditObj extends ApplicationObj> extends DefaultMutableTreeNode implements ValueObjectTreePanel.TreeElement<TEditObj>
{
	private static final PropertyNameCreator propertyCreator = new DefaultPropertyNameCreator();
	private ObjectListModelListener<TEditObj> objListListener;
	private PersistentObjectList<TEditObj> allObjects = null;
	private final ApplicationManager<TEditObj>	appMngr;
	private final AssociationInfo displayedAssocInfo;
	private final TEditObj masterobject;
	private PanelManager pnlMngr;
	private JComponent	parentPnl;    	
	private ValueObjectTreeModel<TEditObj> treeModel;


	public AssociationNode(TEditObj aMasterobject, AssociationInfo aAssocInfo, ApplicationManager<TEditObj> anAppMngr, PanelManager aPnlMngr, ValueObjectTreeModel<TEditObj> aTreeModel) throws JdyPersistentException
	{
		super(aAssocInfo, true);
		this.appMngr = anAppMngr;
		this.displayedAssocInfo = aAssocInfo;	
		this.masterobject = aMasterobject;
		this.pnlMngr = aPnlMngr;
		this.parentPnl = null;
		this.treeModel = aTreeModel;
		this.objListListener = createObjectListListener();
	}
	
	/**
	 * @return 
	 * 
	 */
	protected ObjectListModelListener<TEditObj> createObjectListListener() 
	{
		return new ObjectListModelListener<TEditObj>()
		{
			@Override
			public void intervalAdded(ObjectListModelEvent<TEditObj> aEvent)
			{
				for( int start = aEvent.getLowerIndex(); start <= aEvent.getLowerIndex(); start++) {
					treeModel.insertNodeInto(new AppObjNode<TEditObj>(allObjects.get(start), appMngr, pnlMngr, treeModel),AssociationNode.this,AssociationNode.this.getChildCount());
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
				for( int start = aEvent.getLowerIndex(); start <= aEvent.getUpperIndex(); start++) {
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
				System.out.println("contents");
			}
		};
	}
	

	public boolean isLeaf()
	{
		return false;
	}

	public int getChildCount()
	{
		if (allObjects == null) {
			
			allObjects = (PersistentObjectList<TEditObj>) this.masterobject.getValue(this.displayedAssocInfo);
			int i = 0;
			for (Iterator<TEditObj> iterator = allObjects.iterator(); iterator.hasNext();) {
				TEditObj curAppObj = iterator.next();
				treeModel.insertNodeInto(new AppObjNode<TEditObj>(curAppObj, appMngr, pnlMngr, treeModel), AssociationNode.this, i++);
			}	
			allObjects.addObjectListModelListener(objListListener);
		}
		return (super.getChildCount());
	}

	public String toString()
	{
		String classNameProperty = propertyCreator.getPropertyNameFor(displayedAssocInfo.getMasterClassReference().getReferencedClass() , displayedAssocInfo);
		return pnlMngr.res().getString(classNameProperty);
	}
	
	public List<Action> getNodeActions()
	{
		List<Action> actionList = new ArrayList<Action>();
		addClassAndSubclassCreateActions(actionList, displayedAssocInfo, displayedAssocInfo.getDetailClass());
		
		return actionList;
	}

	@Override
	public ClassInfo getClassInfo() 
	{
		return null;
	}
	
   	@Override
	public TEditObj getValueObject() 
	{
		return null;
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
					newObject.setValue(anAssoc.getMasterClassReference(), masterobject);
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