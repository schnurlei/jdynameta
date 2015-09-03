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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public class ActionUtil
{

	/**
	 * 
	 * @param aAction
	 * @param aShortDesc
	 * @param aLongDesc
	 */
	public static void setActionName(Action aAction, String aName, Icon icon,  String aShortDesc, String aLongDesc)
	{
		aAction.putValue(Action.NAME, aName);
		aAction.putValue(Action.SMALL_ICON, icon);
		aAction.putValue(AbstractAction.SHORT_DESCRIPTION, aShortDesc);
		aAction.putValue(AbstractAction.LONG_DESCRIPTION, aLongDesc);
	}
	
	/**
	 * 
	 * @param aAction
	 * @param aShortDesc
	 * @param aLongDesc
	 */
	public static void addDescriptionsToAction(Action aAction, String aShortDesc, String aLongDesc)
	{
		aAction.putValue(AbstractAction.SHORT_DESCRIPTION, aShortDesc);
		aAction.putValue(AbstractAction.LONG_DESCRIPTION, aLongDesc);
	}
}
