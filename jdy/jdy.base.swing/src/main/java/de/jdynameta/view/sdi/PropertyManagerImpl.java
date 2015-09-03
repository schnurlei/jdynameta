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
package de.jdynameta.view.sdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PropertyManager;

public class PropertyManagerImpl implements PropertyManager
{
	private static final Logger logger = Logger.getLogger(PropertyManagerImpl.class.getName());
	private final Properties panelProperties;
	private final File propertyFile;
	
	/**
	 * 
	 * @param aPropertyHome directory to write the PanelManager properties. Use user.dir if null
	 */
	public PropertyManagerImpl(File aPropertyHome)
	{
		if( aPropertyHome != null && !aPropertyHome.exists() ) {
			aPropertyHome.mkdirs();
		}
		if(  aPropertyHome == null || !aPropertyHome.exists() ||  !aPropertyHome.isDirectory() ) {
			aPropertyHome = new File(System.getProperty("user.dir"));
		}
		
	   	this.panelProperties = new Properties();
    	this.propertyFile =  new File(aPropertyHome,"PanelManager.properties.xml");
        try {
			FileInputStream fileIn = new FileInputStream(propertyFile);
			panelProperties.loadFromXML(fileIn);
		} catch (FileNotFoundException e) {
			logger.log(Level.INFO, e.getLocalizedMessage(), e);
		} catch (InvalidPropertiesFormatException e) {
			logger.log(Level.INFO, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.log(Level.INFO, e.getLocalizedMessage(), e);
		}
	}
	
	public void saveProperties()
	{
        try {
	        FileOutputStream fileOut = new FileOutputStream(propertyFile);
	        panelProperties.storeToXML(fileOut, "Panel Manager Configuration");
		} catch (FileNotFoundException e) {
			logger.log(Level.INFO, e.getLocalizedMessage(), e);
		} catch (InvalidPropertiesFormatException e) {
			logger.log(Level.INFO, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.log(Level.INFO, e.getLocalizedMessage(), e);
		}
	}
	
	public String getPropertyFor(ManagedPanel aManagedPnl, String aSubProperty)
	{
		String propertyName = getRealPropertyName(aManagedPnl, aSubProperty);
		
		return panelProperties.getProperty(propertyName);
	}


	public void setPropertyFor(ManagedPanel aManagedPnl, String aSubProperty, String aValue )
	{
		String propertyName = getRealPropertyName(aManagedPnl, aSubProperty);
		panelProperties.setProperty(propertyName, aValue);
	}
	
	public Integer getIntPropertyFor(ManagedPanel aManagedPnl, String aSubProperty)
	{
		String stringValue = getPropertyFor(aManagedPnl,   aSubProperty);
		Integer result = null;
		if( stringValue != null ) {
			try
			{
				result  = Integer.parseInt(stringValue);
			} catch (NumberFormatException ex)
			{
				ex.printStackTrace();
			}
		}
		
		return result;
	}
	
	public void setIntPropertyFor(ManagedPanel aManagedPnl, String aSubProperty, int aValue)
	{
		setPropertyFor(aManagedPnl,  aSubProperty, ""+aValue);
	}
	
	private String getRealPropertyName(ManagedPanel aManagedPnl, String aSubProperty)
	{
		String panelPrefix = aManagedPnl.getName();
		 String propertyPath = "";
		if ( panelPrefix == null) {
			panelPrefix = aManagedPnl.getClass().getName();
		} 
		
		String propertyName = null;
		if ( panelPrefix != null) {
			propertyName = propertyPath + "." + panelPrefix + "." + aSubProperty;
		}
		return propertyName;
	}
	
}