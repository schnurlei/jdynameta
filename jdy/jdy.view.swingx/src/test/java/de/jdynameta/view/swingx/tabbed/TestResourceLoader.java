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

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import de.jdynameta.view.IconKeyToPathMapper;
import de.jdynameta.view.JdyResourceLoader;


public class TestResourceLoader implements JdyResourceLoader
{
	private static Logger logger = Logger.getLogger(TestResourceLoader.class.getName());
	
	private final List<ResourceBundle> resourceBundelColl;
	private IconKeyToPathMapper iconMapper;
	private Class<? extends Object> iconLoaderClass;
	private Map<String, ImageIcon> iconMap;
	
	/**
	 * @param aResourceBundel
	 * @param aIconKey2IconPathMap
	 * @param aIconLoaderClass
	 */
	public TestResourceLoader(IconKeyToPathMapper aIconMapper, Class<? extends Object> aIconLoaderClass, ResourceBundle ... aResourceBundels )
	{
		super();
		assert(aResourceBundels.length > 0);
		this.resourceBundelColl = new ArrayList<ResourceBundle>();
		for (ResourceBundle resourceBundle : aResourceBundels)
		{
			this.resourceBundelColl.add(resourceBundle);
		}
		this.iconMapper = aIconMapper;
		this.iconLoaderClass = aIconLoaderClass;
		this.iconMap = new HashMap<String, ImageIcon>();
	}

	/**
	 * Gets a string for the given key 
	 */  
	public String getString(String aKey)
	{
		String result = null;
		for (ResourceBundle curBundle : resourceBundelColl)
		{
			try {
				result = curBundle.getString(aKey);
				break; 
			} catch (MissingResourceException excp)	{
			}
		}
		
		if( result == null) {
	    	logger.log(Level.INFO, "resource not found: " + aKey);
			return createKeyNotFoundSubstitute(aKey);
		}
		
		return result;
	}

	private String createKeyNotFoundSubstitute(String aKey)
	{
		int lastDot = aKey.lastIndexOf(".") == -1 ? 0 : aKey.lastIndexOf(".");
		return aKey.substring(lastDot+1);
	}

	/**
	 * Gets a Icon for the given iconname 
	 */
	public ImageIcon getIcon(String aIconKey)
	{
		ImageIcon result = iconMap.get(aIconKey);
		
		if( result == null) {
			
			String iconPath = this.iconMapper.getIconPathForKey(aIconKey);
			
			URL iconUrl = this.iconLoaderClass.getResource(iconPath);
			result =  iconUrl == null ? null : new ImageIcon(iconUrl);
			iconMap.put(aIconKey, result);
		}
		
		return result;
	}

	
    public String getMessage(String aKey, Object ... messageArguments)
    {
            return MessageFormat.format(getString( aKey),messageArguments);

    }

    public static ResourceBundle loadBundle(String aBundleName, Locale aFallBackLocale)
    {
    	ResourceBundle loadedBundele = null; 
        try {
        	loadedBundele = ResourceBundle.getBundle(aBundleName);
	    } catch (RuntimeException ex) {
	    	logger.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            loadedBundele = ResourceBundle.getBundle(aBundleName, aFallBackLocale);
	    }
    	
	    return loadedBundele;
    }
	
}
