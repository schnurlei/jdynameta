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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.persistence.state.PersistentValueObject;
import de.jdynameta.view.panel.SaveException;
import de.jdynameta.view.panel.SavePanel;

@SuppressWarnings("serial")
public abstract class ClassInfoEditPanel<TEditObj extends PersistentValueObject> extends SavePanel 
{

	public abstract void setPanelReadOnly(boolean b);

	public abstract void writeChangedValuesIntoObject() throws SaveException;

	public abstract void setBaseClassInfo(ClassInfo concreteClass) throws JdyPersistentException;

	public abstract TEditObj getObjectToEdit();
	
	public abstract void setObjectToEdit(TEditObj editObj);

	public abstract ClassInfo getDisplayedClassInfo();

}
