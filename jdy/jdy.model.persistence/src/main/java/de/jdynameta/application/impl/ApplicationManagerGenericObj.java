/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.application.impl;

import de.jdynameta.application.ApplicationHook;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.ApplicationManagerImpl;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowManager;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 *
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class ApplicationManagerGenericObj
        extends ApplicationManagerImpl<ChangeableValueObject, GenericValueObjectImpl, AppPersistentGenericObj>
{
    private WorkflowManager<AppPersistentGenericObj> workflowManager;
    private final WorkflowApplicationHook<ApplicationObj> appHook;

    public ApplicationManagerGenericObj(PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persistentObjectManager)
    {
        super(persistentObjectManager);
        this.workflowManager = null;
        this.appHook = new WorkflowApplicationHook<>();
    }

    public ApplicationManagerGenericObj(PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persistentObjectManager, WorkflowApplicationHook<ApplicationObj> anAppHook)
    {
        super(persistentObjectManager);
        this.workflowManager = null;
        this.appHook = anAppHook;
    }

    @Override
    public WorkflowManager<AppPersistentGenericObj> getWorkflowManager()
    {
        return workflowManager;
    }

    public void setWorkflowManager(WorkflowManager<AppPersistentGenericObj> workflowManager)
    {
        this.workflowManager = workflowManager;
    }

    @Override
    protected GenericValueObjectImpl insertInPersistentManager(final AppPersistentGenericObj editedObject, ClassInfo aClassInfo)
            throws JdyPersistentException
    {
        if (existsObjectAlready(editedObject, aClassInfo))
        {
            throw new JdyPersistentException("Es existiert bereits ein Objekt mit gleichen Schluessel werten");
        }

        GenericValueObjectImpl result = super.insertInPersistentManager(editedObject, aClassInfo);
        editedObject.setWrappedValueObject(result);
        return result;
    }

    @Override
    public void saveObject(AppPersistentGenericObj editedObject, WorkflowCtxt aContext) throws JdyPersistentException
    {
        super.saveObject(editedObject, aContext);
        editedObject.clearCachedData();
    }

    @Override
    public void refreshObject(AppPersistentGenericObj editedObject) throws JdyPersistentException
    {
        getPersistenceManager().refreshObject(editedObject.getWrappedValueObject(), editedObject.getClassInfo());
        editedObject.clearCachedData();
    }

    @Override
    public boolean isWritingDependentObject()
    {
        return false;
    }

    @Override
    protected PersistToApplicationConverter<ChangeableValueObject, GenericValueObjectImpl, AppPersistentGenericObj> createConverter()
    {
        return new Converter(this);
    }

    @Override
    protected ApplicationHook<ApplicationObj> getApplicationHook()
    {
        return this.appHook;
    }

    public static class Converter implements PersistToApplicationConverter<ChangeableValueObject, GenericValueObjectImpl, AppPersistentGenericObj>
    {
        private final ApplicationManagerGenericObj appManager;

        public Converter(ApplicationManagerGenericObj anAppManager)
        {
            this.appManager = anAppManager;
        }

        @Override
        public ClassInfo getClassForPersObj(GenericValueObjectImpl aPersObj)
        {
            return aPersObj.getClassInfo();
        }

        @Override
        public AppPersistentGenericObj createNewAppEditObj(GenericValueObjectImpl persistentObj, ClassInfo aClassInfo, boolean isNew, ApplicationManager<AppPersistentGenericObj> anAppManager)
        {
            return new AppPersistentGenericObj(aClassInfo, persistentObj, isNew, appManager);
        }

        @Override
        public void setObjFromPersistInAppObj(GenericValueObjectImpl persistentObj, AppPersistentGenericObj anAppObj)
        {
            anAppObj.setWrappedValueObject(persistentObj);
        }

        @Override
        public ChangeableValueObject convertAppEditObjToObjToPersist(final AppPersistentGenericObj aPersistentObj, ClassInfo aClassInfo)
        {
            HashedValueObject objToPersist = new HashedValueObject();
            if (aPersistentObj.getPersistentState().isNew())
            {
                // keep wrapped object because there may be references on it
                objToPersist = aPersistentObj.getWrappedValueObject();
            }
            // replace AppPersistentGenericObj object referenced with wrapped object 
            replaceReferences(aPersistentObj, aClassInfo, objToPersist);

            return objToPersist;
        }

        private void replaceReferences(final AppPersistentGenericObj aPersistentObj, ClassInfo aClassInfo, final HashedValueObject objToPersist)
        {
            try
            {
                aClassInfo.handleAttributes(new AttributeHandler()
                {
                    @Override
                    public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                            throws JdyPersistentException
                    {
                        if (objToHandle instanceof AppPersistentGenericObj)
                        {
                            objToPersist.setValue(aInfo, ((AppPersistentGenericObj) objToHandle).getWrappedValueObject());
                        } else
                        {
                            objToPersist.setValue(aInfo, objToHandle);
                        }
                    }

                    @Override
                    public void handlePrimitiveAttribute(PrimitiveAttributeInfo info, Object objToHandle)
                            throws JdyPersistentException
                    {
                        objToPersist.setValue(info, objToHandle);
                    }
                }, aPersistentObj);
            } catch (JdyPersistentException e)
            {
                // ignore not persistent opeations in handler
                e.printStackTrace();
            }
        }

    }

}
