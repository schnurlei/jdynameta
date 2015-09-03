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
package de.jdynameta.metainfoview.attribute.model;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.attribute.AbstractAttributeCombobox;
import de.jdynameta.metainfoview.metainfo.CreateObjectReferenceObjectPanel;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.OkCancelDialog;
import de.jdynameta.view.panel.SaveException;

/**
 * @author Rainer
 *

 */
public class ModelObjectReferenceCombobox<TEditObj extends ApplicationObj> extends AbstractAttributeCombobox<TEditObj>
	implements AttrInfoComponent
{
	private final ObjectReferenceAttributeInfo referenceInfo;
	private final ApplicationManager<TEditObj> appManager;
	private final PanelManager panelManager;
	private boolean showNewAction = false;

	/**
	 * 
	 */
	public ModelObjectReferenceCombobox( ObjectReferenceAttributeInfo aInfo
										,ObjectListModel<TEditObj> refrenceList
										, PanelManager aPanelManager
										, ApplicationManager<TEditObj> anAppMngr) throws JdyPersistentException, ProxyResolveException
	{
		super( refrenceList );
		this.appManager = anAppMngr;
		this.panelManager = aPanelManager;
		this.referenceInfo = aInfo;
	}

	/**
	 * 
	 */
	public ModelObjectReferenceCombobox( ObjectReferenceAttributeInfo aInfo
										, PanelManager aPanelManager
										, ApplicationManager<TEditObj> anAppMngr) throws JdyPersistentException, ProxyResolveException
	{
		super( createPersistentCollection(aInfo ,anAppMngr) );
		this.appManager = anAppMngr;
		this.referenceInfo = aInfo;
		this.panelManager = aPanelManager;
	}

	public void setShowNewAction(boolean showNewAction) 
	{
		this.showNewAction = showNewAction;
	}

	public boolean isShowNewAction() 
	{
		return showNewAction;
	}

	public ApplicationManager<TEditObj> getAppManager()
	{
		return appManager;
	}
	
	
	public AttributeInfo getAttributeInfo()
	{
		return this.referenceInfo;
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.view.attribute.AttributeCombobox#getAttributeFromObject(java.lang.Object)
	 */
	@Override
	protected TEditObj getAttributeFromObject(Object anObject)
	{
		return (TEditObj) ((ValueObject) anObject).getValue(referenceInfo);
	}

	/* (non-Javadoc)
	 * @see de.comafra.view.attribute.AttributeCombobox#setAttributeInObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setAttributeInObject(Object anObject, TEditObj selectedAttribute)
	{
		((ChangeableValueObject) anObject).setValue(referenceInfo, selectedAttribute);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.view.attribute.AbstractAttributeCombobox#getTextForAttribute(java.lang.Object)
	 */
	@Override
	protected String getTextForAttribute(Object aValue)
	{
		return getTextForAttribute((ValueObject)aValue, appManager, referenceInfo.getReferencedClass());
	}

	@Override
	public void addToContainer(Container aContainer, Object constraints)
	{
		JPanel comboboxPnl = new JPanel(new BorderLayout());
		comboboxPnl.add(getObjectCombo(), BorderLayout.CENTER);
		if( isShowNewAction()  ) {
			comboboxPnl.add(new JButton(createNewAction(this.panelManager.res())), BorderLayout.EAST);
		}
		aContainer.add(comboboxPnl, constraints);	
	}

	@SuppressWarnings("serial")
	private Action createNewAction(final JdyResourceLoader aResourceLoader)
	{
		return  new AbstractAction(aResourceLoader.getString("common.action.new"), aResourceLoader.getIcon(ApplicationIcons.NEW_ICON))
		{
			public void actionPerformed(ActionEvent event) 
			{
				
				try {
					CreateObjectReferenceObjectPanel<TEditObj> editPnl = new CreateObjectReferenceObjectPanel<TEditObj>(appManager
																	,  referenceInfo, panelManager);
					
					OkCancelDialog newDialog = OkCancelDialog.createDialog(aResourceLoader, getObjectCombo(), "Create new", true);
					newDialog.setPanel(editPnl);
					newDialog.pack();
					newDialog.setVisible(true); 
					
					if(newDialog.getResult() == JOptionPane.OK_OPTION) {
						
						try {
							editPnl.writeChangedValuesIntoObject();
							appManager.saveObject(editPnl.getObjectToEdit(), null);
							setSelectedObject(editPnl.getObjectToEdit());
						} catch (SaveException ex) {
							panelManager.displayErrorDialog(getObjectCombo(), ex.getLocalizedMessage(), ex);
						}
					}
					
				} catch (JdyPersistentException ex) {
					panelManager.displayErrorDialog(getObjectCombo(), ex.getLocalizedMessage(), ex);
				} catch (ObjectCreationException ex) {
					panelManager.displayErrorDialog(getObjectCombo(), ex.getLocalizedMessage(), ex);
				}
			}
		};
	}
	
	public static final class ShowTextAttributeHandler implements AttributeHandler 
	{
		private final StringBuffer text;
		private final List<AttributeInfo> attrList;

		public ShowTextAttributeHandler(StringBuffer text,	List<AttributeInfo> attrList) 
		{
			this.text = text;
			this.attrList = attrList;
		}

		public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,	ValueObject objToHandle)
			throws JdyPersistentException
		{
			if( aInfo.isKey() ) {
				aInfo.getReferencedClass().handleAttributes(this, objToHandle);
			}
		}

		public void handlePrimitiveAttribute(	PrimitiveAttributeInfo aInfo, Object objToHandle)
			throws JdyPersistentException
		{
			if(( attrList == null && aInfo.isKey()) || ( attrList != null && attrList.contains(aInfo))) {
				
				if( text.length() > 0 && objToHandle != null) {
					text.append(" - ");
				}
				text.append( (objToHandle == null) ? "" : objToHandle.toString() + "  ");
			}
		}
	}

	public static String getTextForAttribute(ValueObject aValue, ClassInfoAttrSource appManager, ClassInfo aClassInfo)
	{
		final StringBuffer textBuffer = new StringBuffer(); 
		final List<AttributeInfo> attrList = appManager.getDisplayAttributesFor(aClassInfo);
		AttributeHandler showTexthandler = new ShowTextAttributeHandler(textBuffer, attrList);
		
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
	
	
	public static <TEditObj extends ApplicationObj> PersListenerQueryObjectListModel<TEditObj> createPersistentCollection(ObjectReferenceAttributeInfo aInfo, ApplicationManager<TEditObj> anAppMngr ) throws JdyPersistentException
	{
		 PersListenerQueryObjectListModel<TEditObj> newList = new PersListenerQueryObjectListModel<TEditObj>(anAppMngr, new  DefaultClassInfoQuery(aInfo.getReferencedClass())) ;
		 newList.refresh();
		 return newList;
	}
}
