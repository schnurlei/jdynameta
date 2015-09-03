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
/*
 * Created on 03.07.2003
 *
 */
package de.jdynameta.base.creation.db;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Handler which handles only the Key Attributes of an Class Delegates the realy
 * handlign of the Key attributes to a given handler
 *
 * @author rsc
 */
public class KeyAttributeHandler
        implements AttributeHandler
{

    private final AttributeHandler attributeHandler;

    /**
     *
     * @param aAttributeHandler
     */
    public KeyAttributeHandler(AttributeHandler aAttributeHandler)
    {
        super();
        this.attributeHandler = aAttributeHandler;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.AttributeHandler#handleObjectReference(de.comafra.model.metainfo.ObjectReferenceAttributeInfo, java.lang.Object)
     */
    @Override
    public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
    {
        if (aInfo.isKey())
        {
            this.attributeHandler.handleObjectReference(aInfo, objToHandle);
        }
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.AttributeHandler#handlePrimitiveAttribute(de.comafra.model.metainfo.PrimitiveAttributeInfo, java.lang.Object)
     */
    @Override
    public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
    {
        if (aInfo.isKey())
        {
            this.attributeHandler.handlePrimitiveAttribute(aInfo, objToHandle);
        }
    }

}
