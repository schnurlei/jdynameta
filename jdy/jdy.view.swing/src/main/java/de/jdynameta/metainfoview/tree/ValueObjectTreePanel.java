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

import java.awt.Component;
import java.awt.Font;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.metainfoview.attribute.model.ModelObjectReferenceCombobox;
import de.jdynameta.metainfoview.metainfo.ComponentCreationStrategy;
import de.jdynameta.metainfoview.metainfo.ManagedTreePanel;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.base.PanelEnvironment;
import de.jdynameta.view.base.PanelManager;

@SuppressWarnings("serial")
public abstract class ValueObjectTreePanel<TEditObj extends ApplicationObj> extends ManagedTreePanel<ValueObjectTreePanel.TreeElement<TEditObj>>
{
	private List<ClassInfo> rootClassInfos;
	private ApplicationManager<TEditObj> appMngr;
	
	
	public ValueObjectTreePanel(ApplicationManager<TEditObj> anAppMngr,  ClassRepository aRepositoryToDisplay, PanelManager aPanelManger, List<ClassInfo> allRootClassInfos) throws JdyPersistentException 
	{
		super(aPanelManger);
		this.rootClassInfos = allRootClassInfos;
		this.appMngr = anAppMngr;
		
	}
	
	public ApplicationManager<TEditObj> getAppMngr()
	{
		return this.appMngr;
	}
	

	
	@Override
	public void setEnvironment(PanelEnvironment aEnvironment) 
	{
		super.setEnvironment(aEnvironment);

		try {
			this.getRepositoryTree().setModel(createTreeModel(appMngr, rootClassInfos, getPnlMngr()));
			this.getRepositoryTree().setRootVisible(false);
			
			DefaultTreeCellRenderer renderer = createTreeCellRenderer();
			this.getRepositoryTree().setCellRenderer(renderer);
		} catch (JdyPersistentException ex) {
			getPnlMngr().displayErrorDialog(null, ex.getLocalizedMessage(), ex);
		}
	
	}

	private DefaultTreeCellRenderer createTreeCellRenderer()
	{
		DefaultTreeCellRenderer renderer = 	new DefaultTreeCellRenderer()
		{
			@Override
			public Component getTreeCellRendererComponent(JTree tree,Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) 
			{
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,	row, hasFocus);
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				
				String nodeText = getTextForTreeElement((TreeElement<TEditObj>)value);
				if( nodeText != null) {
					setText(nodeText);
				} else {
					nodeText = value.toString();
				}
				Font font = getDerivedFont(getFont(), (TreeElement<TEditObj>)value);
				if( font != null) {
					setFont(font);
				} else {
					setFont(getFont().deriveFont(Font.PLAIN));
				}
				
				setIcon( getIconForTreeElement((TreeElement<TEditObj>)value) );
				
				return this;

			}

			
		};
		return renderer;
	}
	
	
	protected Icon getIconForTreeElement(TreeElement<TEditObj> aTreeElement)
	{
		return null;
	}
	
	protected String getTextForTreeElement(TreeElement<TEditObj> aTreeElement) 
	{

		ClassInfo classInfo = aTreeElement.getClassInfo();
		TEditObj object = aTreeElement.getValueObject();

		String result = null;
		if (classInfo != null && object != null) {
		
			result = getTextForAttribute(object, appMngr, classInfo);
		} 

		return result;
	}

	protected Font getDerivedFont(Font aFont, TreeElement<TEditObj> aTreeElement)
	{
		return null;
	}

	
	
    protected ValueObjectTreeModel<TEditObj> createTreeModel(ApplicationManager<TEditObj> anAppMngr, List<ClassInfo> allRootClassInfos, PanelManager aPnlMngr) throws JdyPersistentException
    {
    	ValueObjectTreeModel<TEditObj> treeModel = new ValueObjectTreeModel<TEditObj>(null, true)
    	{
    		public ComponentCreationStrategy getCreationStrat(TEditObj aObjectToEdit) 
    		{
    			return createComponentStrategy(aObjectToEdit);
    		};
    	};
    	
    	RootNode<TEditObj> root = new RootNode<TEditObj>();
		
		for (ClassInfo classInfo : allRootClassInfos) {
			
			ClassInfoNode<TEditObj> classNode = new ClassInfoNode<TEditObj>(classInfo, anAppMngr,aPnlMngr, treeModel, this);
			root.add(classNode);
		}
		
		treeModel.setRoot(root);
		
		return treeModel;
    }

	protected abstract ComponentCreationStrategy createComponentStrategy(TEditObj aObjectToEdit);

		
    
    @Override
	public ValueObjectTreePanel.TreeElement<TEditObj> getSelectedObject() throws ProxyResolveException 
	{
		TreePath path = this.getRepositoryTree().getSelectionPath();
		ValueObjectTreePanel.TreeElement<TEditObj> selectedComponent = (path == null) ? null : (ValueObjectTreePanel.TreeElement<TEditObj>) path.getLastPathComponent();
		
		return selectedComponent; 
	}
    
    @Override
    protected void treeSelectionChanged() 
    {
    	super.treeSelectionChanged();
		if( getSelectedObject() != null) {
//			setActionsInToolbar(getSelectedObject().getNodeActions());
			setPopupActions(getSelectedObject().getNodeActions());
		} else {
			setActionsInToolbar(Collections.<Action>emptyList());
		}

	}
    
    public static interface TreeElement<TEditObj extends ApplicationObj> 
    {
		public List<Action> getNodeActions();
		public ClassInfo getClassInfo();
		public TEditObj getValueObject();
    }
    
    
    public static class RootNode<TEditObj extends ApplicationObj> extends DefaultMutableTreeNode implements TreeElement<TEditObj>
    {
    	public RootNode() 
    	{
			super("All Repositories", true);
		}
    	
    	@Override
    	public ClassInfo getClassInfo() 
    	{
    		return null;
    	}
    	
    	@Override
    	public List<Action> getNodeActions() {
    		return null;
    	}    	
    	
    	@Override
    	public TEditObj getValueObject() 
    	{
    		return null;
    	}
    	
    }
    
    public static String getTextForAttribute(ValueObject aValue, ClassInfoAttrSource appManager, ClassInfo aClassInfo) 
	{
		final StringBuffer textBuffer = new StringBuffer(); 
		final List<AttributeInfo> attrList = appManager.getDisplayAttributesFor(aClassInfo);
		AttributeHandler showTexthandler = new ModelObjectReferenceCombobox.ShowTextAttributeHandler(textBuffer, attrList);
		
		try
		{
			if( aValue == null) {
				textBuffer.append("-");
			} else {
				aClassInfo.handleAttributes(showTexthandler, aValue);
			}
		} catch (JdyPersistentException excp)
		{
			excp.printStackTrace();
		}
		
		return textBuffer.toString();
	}
    
	
	
}
