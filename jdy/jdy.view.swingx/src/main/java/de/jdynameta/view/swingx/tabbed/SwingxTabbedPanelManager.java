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
package de.jdynameta.view.swingx.tabbed;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.action.AdminActionBar;
import de.jdynameta.view.base.DescriptivePanelDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.base.PanelEnvironment;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.base.PropertyManager;
import de.jdynameta.view.panel.CloseVetoException;
import de.jdynameta.view.panel.OkCancelDialog;
import de.jdynameta.view.panel.OkCancelSaveDialog;
import de.jdynameta.view.panel.OkPanel;
import de.jdynameta.view.panel.SavePanel;
import de.jdynameta.view.sdi.AbstractSdiFrame;
import de.jdynameta.view.sdi.DefaultSdiFrame;
import de.jdynameta.view.sdi.OkSaveSdiFrame;
import de.jdynameta.view.sdi.OkSdiFrame;
import de.jdynameta.view.sdi.PropertyManagerImpl;

/**
 * Implements a PanelManager for a Single Document Interface.
 * Every Panel has its own Frame (Window) 
 * @author  Rainer
 * @version 
 */
public class SwingxTabbedPanelManager implements PanelManager 
{
	private final JdyResourceLoader resourceLoader;
	private final HashMap<JPanel,AbstractSdiFrame> panel2FrameMap;
	private final PropertyManagerImpl propertyManager;
	private Image defaultIcon;
	private SwingxTabbedPanel mainPanel;
	private boolean showSavePanelInOwnwindow;
	
	/** Creates new SdiPanelManager */
    public SwingxTabbedPanelManager(JdyResourceLoader aResourceLoader, File aPropertyHome, boolean isShowTabCloseBtn, boolean isOpenSavePnlInOwnWindow) 
    {	
    	this.resourceLoader = aResourceLoader;
    	this.showSavePanelInOwnwindow = isOpenSavePnlInOwnWindow;
    	this.panel2FrameMap = new HashMap<JPanel,AbstractSdiFrame>();
    	this.propertyManager = new PropertyManagerImpl(aPropertyHome);
    	this.mainPanel = new SwingxTabbedPanel(this, isShowTabCloseBtn);

     }

    public void closeAllTabbs()
    {
    	this.mainPanel.removeAll();
    }
    
	public JdyResourceLoader res()
	{
		return getResourceLoader();
	}
    
    public JdyResourceLoader getResourceLoader()
	{
		return resourceLoader;
	}
    
    public PropertyManager getPropertyManager()
	{
		return propertyManager;
	}

    public void setDefaultFrameIcon(Image aImage) 
    {
    	this.defaultIcon = aImage;
    }
    
	public void setNavigationPanel(JPanel aNavigationPanel)
	{
		this.mainPanel.setNavigationPanel(aNavigationPanel);
		PanelDecorator mainDecorator = new PanelDecorator(defaultIcon, mainPanel, null);
		displayPanelInternal(mainDecorator, null);
	}

	
	public void closePanel(ManagedPanel aPnl) throws CloseVetoException 
	{

		mainPanel.closePanel(aPnl);
	}
	
	
	public void displayPanel(DescriptivePanelDecorator aPanelDecorator, ManagedPanel parentPnl) 
	{
		
		if( showSavePanelInOwnwindow && ( !(aPanelDecorator.getManagedPanel() instanceof SavePanel) 
				&& !( aPanelDecorator.getManagedPanel() instanceof OkPanel)) ) {

			mainPanel.displayPanel(aPanelDecorator, parentPnl);
			PanelDecorator mainDecorator = new PanelDecorator(defaultIcon, mainPanel, null);
			displayPanelInternal(mainDecorator, null);
		} else {
			displayPanelInternal(aPanelDecorator, parentPnl);
		}
		
	}
    
    
	/**
	 * Display the managed panel in a Frame. Create a new Frame,
	 * if the PanelDescription is displayed for the first time,
	 * otherwise show the existing frame.
	 * 
	 * @param aPanel the to show
	 * @param aAllActionsColl panel dependent actions displayed in the Frame 
	 */
	public void displayPanelInternal(DescriptivePanelDecorator aPanelDecorator, ManagedPanel parentPnl) 
	{
		AbstractSdiFrame frameToDisplay;
		boolean wasInvisible = false;
		if ( panel2FrameMap.containsKey(aPanelDecorator.getManagedPanel())) {
			frameToDisplay = panel2FrameMap.get(aPanelDecorator.getManagedPanel());
			wasInvisible = !frameToDisplay.isVisible() &&  frameToDisplay.getState() != Frame.ICONIFIED ;
		} else {

			wasInvisible = true;
			frameToDisplay = createFrameForPanel(aPanelDecorator);
			addDependentFrames(parentPnl, frameToDisplay);
			addActionsToFrame(aPanelDecorator, frameToDisplay);
			setFrameIcon(aPanelDecorator, frameToDisplay);
			
			SdiPanelEnvironment environment = new SdiPanelEnvironment(frameToDisplay, aPanelDecorator );
			aPanelDecorator.getManagedPanel().setEnvironment(environment);
			environment.panelTitleChanged();
			
			displayPanelInFrame(aPanelDecorator, frameToDisplay);

			this.panel2FrameMap.put(aPanelDecorator.getManagedPanel(), frameToDisplay);

			WindowAdapter windowListenr = createFrameListener(aPanelDecorator.getManagedPanel());
			frameToDisplay.addWindowListener(windowListenr);
			frameToDisplay.addWindowStateListener(windowListenr);
			frameToDisplay.addComponentListener(createFrameComponentListener(aPanelDecorator.getManagedPanel()));
		}
		
		if( frameToDisplay.getState() == Frame.ICONIFIED ) {
			frameToDisplay.setState(Frame.NORMAL);
		}

		if( wasInvisible) {
			aPanelDecorator.getManagedPanel().makeVisible();
		}
		frameToDisplay.setVisible(true);
	}


	protected void displayPanelInFrame(DescriptivePanelDecorator aPanelDecorator,	AbstractSdiFrame frameToDisplay) 
	{
		if ( frameToDisplay instanceof OkSaveSdiFrame) {
			((OkSaveSdiFrame)frameToDisplay).displayPanel((SavePanel)aPanelDecorator.getManagedPanel());
		} else if ( frameToDisplay instanceof OkSdiFrame) 
		{
			((OkSdiFrame)frameToDisplay).displayPanel((OkPanel)aPanelDecorator.getManagedPanel());
		} else 
		{
			frameToDisplay.displayPanel(aPanelDecorator.getManagedPanel());
		}
		frameToDisplay.pack();
		restoreFrameBounds(frameToDisplay, aPanelDecorator);
	}

	/** 
	 * Get Icon from decorator ore use default icon
	 * @param aPanelDecorator
	 * @param frameToDisplay
	 */
	protected void setFrameIcon(DescriptivePanelDecorator aPanelDecorator,AbstractSdiFrame frameToDisplay) 
	{
		if( aPanelDecorator.getPanelImage() != null) {
			frameToDisplay.setIconImage(aPanelDecorator.getPanelImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if ( defaultIcon != null ) {
			frameToDisplay.setIconImage(defaultIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		}
	}


	/**
	 * Add the actions defined in the Panel decorator to the frame
	 * @param aPanelDecorator
	 * @param frameToDisplay
	 */
	protected void addActionsToFrame(DescriptivePanelDecorator aPanelDecorator,	AbstractSdiFrame frameToDisplay) 
	{
		if(aPanelDecorator.getActionColl() != null) {
			// menu for close should be first action
			if(  aPanelDecorator.getCloseActionDecorator() != null && aPanelDecorator.getCloseActionDecorator().isShowInMenubar() ) {
				frameToDisplay.addActionToMenuBar(null, aPanelDecorator.getCloseActionDecorator().getMenuBarPath(), false);
			}
			// add actions to frame
			for (ActionDecorator curWrapper : aPanelDecorator.getActionColl())
			{
				frameToDisplay.addAction(curWrapper);
			}
			frameToDisplay.addCloseAction();
		}
	}

	/**
	 * add a dependent frames to the Frame of the parent panel, which will be closed when the parent frame
	 * is closed 
	 * @param parentPnl
	 * @param frameToDisplay
	 */
	protected void addDependentFrames(ManagedPanel parentPnl, AbstractSdiFrame frameToDisplay) 
	{
		if( parentPnl != null) {
			// add dependent frame to parent
			AbstractSdiFrame parentFrame = panel2FrameMap.get(parentPnl);
			if( parentFrame != null) {
				parentFrame.addDependentFrame(frameToDisplay);
			}
		}
	}

	/**
	 * Create an Frame for the panel decorator
	 * @param aPanelDecorator
	 * @return
	 */
	protected AbstractSdiFrame createFrameForPanel(DescriptivePanelDecorator aPanelDecorator) 
	{
		AbstractSdiFrame frameToDisplay;
		if( aPanelDecorator.getManagedPanel() instanceof SavePanel) {
			frameToDisplay = new OkSaveSdiFrame(this, aPanelDecorator.getCloseActionDecorator());
		} else if ( aPanelDecorator.getManagedPanel() instanceof OkPanel) {
			frameToDisplay = new OkSdiFrame(this, aPanelDecorator.getCloseActionDecorator());
		} else {
			frameToDisplay = new DefaultSdiFrame(this, aPanelDecorator.getCloseActionDecorator());
		}
		return frameToDisplay;
	}
	
	public boolean displayDialog(OkPanel aPanel, String aTitle, Dimension aDefaultSize, JComponent parent) 
	{
		OkCancelDialog newDialog = OkCancelDialog.createDialog(getResourceLoader(), parent, aTitle, true);
		newDialog.setPanel(aPanel);
		restoreDialogBounds(newDialog, aPanel, aDefaultSize);
		newDialog.setVisible(true);
		FrameBoundProperties.setBounds(propertyManager, aPanel, "", newDialog.getBounds());
		propertyManager.saveProperties();
		
		return newDialog.getResult() == JOptionPane.OK_OPTION;

	}
	
	public boolean displayDialog(SavePanel aPanel, String aTitle, Dimension aDefaultSize, boolean askForSave, JComponent parent) 
	{
		OkCancelSaveDialog newDialog = OkCancelSaveDialog.createDialog(this, parent, aTitle, true);
		newDialog.setAskForSave(askForSave);
		newDialog.setPanel(aPanel);
		restoreDialogBounds(newDialog, aPanel, aDefaultSize);
		newDialog.setVisible(true);
		FrameBoundProperties.setBounds(propertyManager, aPanel, "", newDialog.getBounds());
		propertyManager.saveProperties();
		
		return newDialog.getResult() == JOptionPane.OK_OPTION;

	}
	
	public void displayErrorDialog( JComponent parentComponent, String aMessage, Throwable exception)
	{
		ErrorInfo info = new ErrorInfo("Error", aMessage, null, "category", 
				exception, Level.ALL, null);
		JXErrorPane.showDialog(parentComponent, info); 		
	}
	
	
	protected void restoreFrameBounds( AbstractSdiFrame frameToDisplayPnl, DescriptivePanelDecorator aPanelDecorator)
	{
		Rectangle bounds = FrameBoundProperties.getBounds(propertyManager, aPanelDecorator.getManagedPanel());
		if( bounds != null ) {
			frameToDisplayPnl.setBounds(bounds);
		} else if (  aPanelDecorator.getDefaultSize() != null){
			frameToDisplayPnl.setSize( aPanelDecorator.getDefaultSize());
		} 
		// frameToDisplayPnl.setExtendedState(state);
	}

	protected void restoreDialogBounds( JDialog dialog, ManagedPanel aPanel, Dimension aDefaultSize)
	{
		Rectangle bounds = FrameBoundProperties.getBounds(propertyManager, aPanel);
		if( bounds != null ) {
			dialog.setBounds(bounds);
		} else {
			dialog.setSize(aDefaultSize);
		}
	}
	
	
	protected WindowAdapter createFrameListener(final ManagedPanel aManagedPnl)
	{
		return new WindowAdapter()
		{
			
			@Override
			public void windowActivated(WindowEvent e) 
			{
				super.windowActivated(e);
				aManagedPnl.activated();
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) 
			{
				super.windowDeactivated(e);
				aManagedPnl.deActivated();
			}
		};
	}

	protected ComponentAdapter createFrameComponentListener(final ManagedPanel aManagedPnl)
	{
		return new ComponentAdapter()
		{
			@Override
			public void componentMoved(ComponentEvent e)
			{
				AbstractSdiFrame frameToDisplayPnl = panel2FrameMap.get(aManagedPnl);
				FrameBoundProperties.setBounds(propertyManager, aManagedPnl, "", frameToDisplayPnl.getBounds());
				propertyManager.saveProperties();
			}
			
			@Override
			public void componentResized(ComponentEvent e)
			{
				AbstractSdiFrame frameToDisplayPnl = panel2FrameMap.get(aManagedPnl);
				FrameBoundProperties.setBounds(propertyManager, aManagedPnl, "", frameToDisplayPnl.getBounds());
				propertyManager.saveProperties();
			}			
		};
	}
	
	/**
	 * Create a list of action to switch the lookAndFeel for a all installed lookAndFeels
	 * @return
	 */
	public List<Action> createChangeLookAndFeelActions()
	{
        List<Action> lafActions = new ArrayList<Action>();
        ButtonGroup lafButtonGroup = new ButtonGroup();
        for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) 
        {
        	Action newAction = createChangeLookAndFeelAction(lafInfo);
        	newAction.putValue(AdminActionBar.BTN_PROPERTY_BTN_GROUP, lafButtonGroup);
        	if( UIManager.getLookAndFeel().getName().equals(lafInfo.getName()) ) {
            	newAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
        	} else {
            	newAction.putValue(Action.SELECTED_KEY, Boolean.FALSE);
        	}
        	
        	lafActions.add(newAction);
        }

        return lafActions;
	}
	

	@SuppressWarnings("serial")
	public AbstractAction createChangeLookAndFeelAction( final LookAndFeelInfo lafInfo)
	{
		return new AbstractAction(lafInfo.getName())
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!lafInfo.equals(UIManager.getLookAndFeel())) {
					try {
						UIManager.setLookAndFeel(lafInfo.getClassName());
						for (JFrame curFrame : panel2FrameMap.values()) {
							SwingUtilities.updateComponentTreeUI(curFrame);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};
	}
	
	
	private static class SdiPanelEnvironment implements PanelEnvironment
	{
		private final AbstractSdiFrame frame;
		private final DescriptivePanelDecorator panelDesc;
		
		public SdiPanelEnvironment(AbstractSdiFrame aFrame, DescriptivePanelDecorator aPanelDesc)
		{
			this.frame = aFrame;
			this.panelDesc = aPanelDesc;
		}
		
		/* (non-Javadoc)
		 * @see de.comafra.view.base.PanelEnvironment#displayPanelAtFront()
		 */
		public void displayPanelAtFront()
		{
			this.frame.toFront();
		}
		
		/* (non-Javadoc)
		 * @see de.comafra.view.base.PanelEnvironment#panelTitleChanged()
		 */
		public void panelTitleChanged()
		{
			this.frame.setTitle(panelDesc.getManagedPanel().getTitle());
		}		

		/* (non-Javadoc)
		 * @see de.comafra.view.base.PanelEnvironment#panelWantsToClose()
		 */
		public void panelWantsToClose() throws CloseVetoException
		{
			this.frame.closeAllFrames();
		}	

		/* (non-Javadoc)
		 * @see de.comafra.view.base.PanelEnvironment#showStatusMessage(java.lang.String)
		 */
		public void showStatusMessage(String aMessage)
		{
			this.frame.showStatusMessage(aMessage);
		}		
	}
	
	
	private static class FrameBoundProperties
	{
		private static final String PROPERTY_TOP = "top";
		private static final String PROPERTY_LEFT = "left";
		private static final String PROPERTY_WIDTH = "width";
		private static final String PROPERTY_HEIGHT = "height";

		public static Rectangle getBounds(PropertyManagerImpl aPropertyManager, ManagedPanel aManagedPnl )
		{
			Integer x = aPropertyManager.getIntPropertyFor(aManagedPnl, PROPERTY_TOP);
			Integer y = aPropertyManager.getIntPropertyFor(aManagedPnl, PROPERTY_LEFT);
			Integer widht = aPropertyManager.getIntPropertyFor(aManagedPnl, PROPERTY_WIDTH);
			Integer height = aPropertyManager.getIntPropertyFor(aManagedPnl, PROPERTY_HEIGHT);

			Rectangle result = null;
			if ( x != null && y != null && widht != null && height != null) {
				result = new Rectangle(x,y,widht, height); 
			}
			
			return result;
		}

		public static void  setBounds(PropertyManagerImpl aPropertyManager, ManagedPanel aManagedPnl, String propertyPath, Rectangle bounds )
		{
			aPropertyManager.setIntPropertyFor(aManagedPnl, PROPERTY_TOP, bounds.x);
			aPropertyManager.setIntPropertyFor(aManagedPnl, PROPERTY_LEFT, bounds.y);
			aPropertyManager.setIntPropertyFor(aManagedPnl, PROPERTY_WIDTH, bounds.width);
			aPropertyManager.setIntPropertyFor(aManagedPnl, PROPERTY_HEIGHT , bounds.height);
		
		}
	}
	
}
