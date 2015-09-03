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
package de.jdynameta.view.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Utiliy class for Microsoft Windows 
 * @author rs
 *
 */
public class WindowUtil
{
	public static void openFile(File fileToOpen) throws IOException
	{
		Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "\"DummyTitle\"", fileToOpen.getAbsolutePath()});
		
	}
	
	public static void showFileInExplorer(File fileToOpen) throws IOException
	{
		Runtime.getRuntime().exec(new String[]{"explorer", fileToOpen.getParent()});
	}
	
	public static String getWindowsUser()
	{
		
		// Class Method return new NTSystem().getName(); by reflection to get it compiled under unix on hudson		
		
		try {
			Class ntSystem = Class.forName("com.sun.security.auth.module.NTSystem");
			Object ntSystemObj = ntSystem.newInstance();
			return 	(String) ntSystem.getMethod("getName").invoke(ntSystemObj);	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (SecurityException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (InstantiationException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		}
	}
	
	public static String getUnixUser()
	{
		try {
			Class systemClass = Class.forName("com.sun.security.auth.module.UnixSystem");
			Object ntSystemObj = systemClass.newInstance();
			return 	(String) systemClass.getMethod("getName").invoke(ntSystemObj);	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (SecurityException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		} catch (InstantiationException e) {
			e.printStackTrace();
			return System.getProperty("user.name");
		}
		
	}
	
	public static boolean isWindows(){
		 
		String os = System.getProperty("os.name").toLowerCase();
		//windows
	    return (os.indexOf( "win" ) >= 0); 
 
	}
 
	public static boolean isMac(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//Mac
	    return (os.indexOf( "mac" ) >= 0); 
 
	}
 
	public static boolean isUnix(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
	    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
 
	}
	
	
}
