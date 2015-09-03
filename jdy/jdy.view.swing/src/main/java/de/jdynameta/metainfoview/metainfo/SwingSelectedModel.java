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

import java.beans.PropertyChangeListener;


/**	* A standard interface for list selection retrieval.
	*
	* @version 05/2002
	*/
public interface SwingSelectedModel
{
	public static final String SELECTION_CHANGE_PROPERTY = "selectionChange";


/**	* Retrieves the currently selected object.
	*
	* @return			The selected object or <tt>null</tt> if no object is
	* 					selected.
	*/
    public Object getSelectedObject();


/**	* Retrieves an array of currently selected objects.
	* The returned values are sorted in increasing index order.
	* 
	* @return			The selected values.
	*/
    public Object[] getAllSelectedObjects();


	public void addSelectionChangeListener(PropertyChangeListener selectionChangeListener);


	public void removeSelectionChangeListener(PropertyChangeListener selectionChangeListener);
}
