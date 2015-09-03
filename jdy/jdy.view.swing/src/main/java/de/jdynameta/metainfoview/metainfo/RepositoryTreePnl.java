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
package de.jdynameta.metainfoview.metainfo;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.view.base.PanelEnvironment;
import de.jdynameta.view.base.PanelManager;

@SuppressWarnings("serial")
public class RepositoryTreePnl extends ManagedTreePanel<ClassInfo>
{
	private final ClassRepository repositoryToDisplay;
	
	public RepositoryTreePnl(ClassRepository aRepositoryToDisplay, PanelManager aPanelManger) 
	{
		super(aPanelManger);
		this.repositoryToDisplay = aRepositoryToDisplay;
		
	}
	
	@Override
	public void setEnvironment(PanelEnvironment aEnvironment) 
	{
		super.setEnvironment(aEnvironment);
		this.getRepositoryTree().setModel(new DefaultTreeModel(createTreeModel(repositoryToDisplay), true));
		
		DefaultTreeCellRenderer renderer = 	new DefaultTreeCellRenderer()
		{
			@Override
			public Component getTreeCellRendererComponent(JTree tree,Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) 
			{
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,	row, hasFocus);
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				Object nodeObject =node.getUserObject();
				
				if( nodeObject instanceof ClassInfo ) {
					setText( ((ClassInfo) nodeObject).getInternalName());
				}
				
				return this;

			}			
		};
		this.getRepositoryTree().setCellRenderer(renderer);

		

	}
	
    private DefaultMutableTreeNode createTreeModel(ClassRepository aRepositoryToDisplay)
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Repository");
		
		Map<String, DefaultMutableTreeNode> namespace2Nodes = new HashMap<String, DefaultMutableTreeNode>();
		
		// create leaf node for every class nfo group by namespace nodes
		for (ClassInfo classInfo : aRepositoryToDisplay.getAllClassInfosIter()) {
			
			DefaultMutableTreeNode nameSpaceNode =  namespace2Nodes.get(classInfo.getNameSpace());
			if( nameSpaceNode == null) {
				nameSpaceNode = new DefaultMutableTreeNode(classInfo.getNameSpace());
				root.add(nameSpaceNode);
				namespace2Nodes.put(classInfo.getNameSpace(), nameSpaceNode);
			}
			
			DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(classInfo);
			classNode.setAllowsChildren(false);
			nameSpaceNode.add(classNode);
		}
			
		return root;
	}
    
    @Override
	public ClassInfo getSelectedObject() throws ProxyResolveException 
	{
		TreePath path = this.getRepositoryTree().getSelectionPath();
		DefaultMutableTreeNode selectedComponent = (DefaultMutableTreeNode) path.getLastPathComponent();
		
		return ((selectedComponent != null &&  selectedComponent.getUserObject() instanceof ClassInfo) ? (ClassInfo) selectedComponent.getUserObject() : null); 
	}
    
	
}
