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
package de.jdynameta.metainfoview;

import de.jdynameta.view.IconKeyToPathMapper;

public class ApplicationIcons implements IconKeyToPathMapper
{
	public static final String CUT_ICON = "Cut";
	public static final String NEW_ICON = "New";
	public static final String EDIT_ICON = "Edit";
	public static final String SAVE_ICON = "Save";
	public static final String SAVE_ALL_ICON = "SaveAll";
	public static final String DELETE_ICON = "Delete";
	public static final String REFRESH_ICON = "Refresh";
	public static final String OPEN_ICON = "Open";
	public static final String COPY_ICON = "Copy";
	public static final String ADD_FILTER_ICON = "AddFilter";
	public static final String RESET_FILTER_ICON = "ResetFilter";
	public static final String FIND_NEXT_ICON = "Forward";
	public static final String FIND_PREVIOUS_ICON = "Back";

	public static final String JDY_TYPE_REPOSITORY = "typeRepository";
	public static final String JDY_TYPE_CLASS = "typeClass";
	public static final String JDY_TYPE_ASSOCIATION = "typeAssociation";
	public static final String JDY_TYPE_TIMESTAMP = "typeTimestamp";
	public static final String JDY_TYPE_VARCHAR = "typeVarchar";
	public static final String JDY_TYPE_BOOLEAN = "typeBoolean";
	public static final String JDY_TYPE_TEXT = "typeText";
	public static final String JDY_TYPE_REFERENCE = "typeReference";
	public static final String JDY_TREE_FOLDER = "treeFolder";
	
	
	
	public String getIconPathForKey(String aIconKey)
	{
		return "" + aIconKey +"16.gif";
	}
	
	
}
