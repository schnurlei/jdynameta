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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelEnvironment;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

@SuppressWarnings("serial")
public abstract class ManagedTreePanel<TNodeObject> extends ManagedPanel 
{

	private JTree shownTree;
	private EditObjectHandler<TNodeObject> editHandler;
	private Action editAction;
	private JToolBar toolBar;
	private final JPopupMenu popup;
	private PanelManager pnlMngr;

	
	public ManagedTreePanel( PanelManager aPanelManger) 
	{
		super();
		this.popup = new JPopupMenu();
		this.pnlMngr = aPanelManger;
		this.editAction = createEditObjectAction(pnlMngr.res());
		this.toolBar = createToolBar();
		
	}
	
	protected PanelManager getPnlMngr() 
	{
		return pnlMngr;
	}
	
	
	@Override
	public void setEnvironment(PanelEnvironment aEnvironment) 
	{
		super.setEnvironment(aEnvironment);
		this.shownTree = new JTree();
		
		this.shownTree.setRootVisible(true);
		this.shownTree.setShowsRootHandles(true);
		this.shownTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		this.shownTree.getSelectionModel().addTreeSelectionListener(createSelectionListener());
		
		this.popup.setOpaque(true);
		this.popup.setLightWeightPopupEnabled(true);
		 
		this.shownTree. addMouseListener( createTreeMouseListener() );

		initUi();
		enableActions();		
	}

	private final MouseAdapter createTreeMouseListener() 
	{
		return new MouseAdapter() {
		    public void mouseReleased( MouseEvent event ) 
		    {
		        if ( event.isPopupTrigger()) {
		            popup.show( (JComponent)event.getSource(), event.getX(), event.getY() );
		        }
		    }
		    public void mousePressed(MouseEvent event) 
		    {
		        if ( event.isPopupTrigger()) {
		            popup.show( (JComponent)event.getSource(), event.getX(), event.getY() );
		        }
		    };
		    
            @Override
			public void mouseClicked(MouseEvent event) 
            {
	            mouseClickInTree(event);
            }
		};
	}
	
	protected void setPopupActions(List<Action> allActionsToShow)
	{
		popup.removeAll();
		for (Action action : allActionsToShow) {
			popup.add( new JMenuItem(action));
		}
	}
	
	protected JTree getRepositoryTree()
	{
		return shownTree;
	}
	
	protected void mouseClickInTree(MouseEvent event)
	{
		// edit object on double click
	    if ( event.getClickCount() > 1 ) {
	    	this.editSelectedObject();
	    }
	}

	protected TreeSelectionListener createSelectionListener() 
	{
		return new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent event) 
			{
				treeSelectionChanged(); 
			}
		};
	}

	protected void treeSelectionChanged() 
	{
		enableActions();
		if( editHandler != null) {
			editHandler.selectionChanged();
		}
	}
	
	public void setEditHandler(EditObjectHandler<TNodeObject> editHandler) 
	{
		this.editHandler = editHandler;
	}

	private final void initUi() 
	{
		this.setLayout(new BorderLayout());
		this.add(this.toolBar, BorderLayout.PAGE_START);
		this.add(new JScrollPane(shownTree), BorderLayout.CENTER);
	}

	private final JToolBar createToolBar() 
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

	public void setActionsInToolbar(List<Action> anActionToAdd) 
	{
		this.toolBar.removeAll();
		this.toolBar.addSeparator();
		for (Action action : anActionToAdd) {
			this.toolBar.add(action);
		}
		this.toolBar.invalidate();
		this.toolBar.repaint();
	}

	
	
	public void addSeparatorToToolbar() 
	{
		this.toolBar.addSeparator();
	}

	protected void enableActions()
	{
		if( editHandler != null) {
			editAction.setEnabled( (getSelectedObjectCount() ==  1 && editHandler.isObjectEditable(getSelectedObject())));
		} else {
			editAction.setEnabled(getSelectedObjectCount() ==  1);
		}
	}

	public int getSelectedObjectCount() 
	{
		return (this.shownTree.getSelectionRows() != null) ? this.shownTree.getSelectionRows().length : 0;
	}

	private final Action createEditObjectAction(JdyResourceLoader aResourceLoader) 
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

	public Action getEditObjectAction()
	{
		return this.editAction;
	}

	private void editSelectedObject() 
	{
		if( editHandler != null) { 
			TNodeObject modelFromDb =  getSelectedObject();
			if( modelFromDb != null) {
				editHandler.editObject(modelFromDb);
			}
		}
	}

	/**
	 * Get first selected Object 
	 * @return
	 * @throws ProxyResolveException
	 */
	public abstract TNodeObject getSelectedObject() throws ProxyResolveException;


	public static interface EditObjectHandler<TNodeObject>
	{
		public void editObject(TNodeObject anObject);
		public void selectionChanged();
		public boolean isObjectEditable(TNodeObject anObject);
	}

	
}