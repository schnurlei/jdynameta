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
package de.jdynameta.persistence.base.cache;

import java.sql.SQLException;

import de.jdynameta.base.cache.MultibleClassInfoIdentityCache;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.persistence.cache.CachedObjectTransformator;
import de.jdynameta.persistence.cache.UpdatableObjectCreator;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import junit.framework.TestCase;

/**
 *
 */

public class ObjectCreatorTest extends TestCase
{
    /**
     * Creates a new Test object.
     *
     * @param name DOCUMENT ME!
     */
    public ObjectCreatorTest(String name)
    {
        super(name);
    }

    /**
     * @throws Exception DOCUMENT ME!
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testNonCachingCreator() throws Exception
    {
        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(new Integer(100));
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");

        CompanyImpl equalCompany = new CompanyImpl();
        equalCompany.setCompanyId(new Integer(100));
        equalCompany.setCompanyName("Wurstfabrik");
        equalCompany.setCity("Roggenburg");

        assertTrue(testCompany != equalCompany);

        MarkingObjectCreator tmpObjCreator = new MarkingObjectCreator();

        Object testCompObj = tmpObjCreator.createObjectFor(testCompany);
        Object equalCompObj = tmpObjCreator.createObjectFor(equalCompany);

        assertTrue(testCompObj != equalCompObj);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testCachedObjectCreator() throws Exception
    {
        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(new Integer(100));
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");

        CompanyImpl equalCompany = new CompanyImpl();
        equalCompany.setCompanyId(new Integer(100));
        equalCompany.setCompanyName("Wurstfabrik");
        equalCompany.setCity("Roggenburg");

        assertTrue(testCompany != equalCompany);

        MarkingObjectCreator creator = new MarkingObjectCreator();

        CachedObjectTransformator cachedObjCreator = new CachedObjectTransformator(new MultibleClassInfoIdentityCache(), creator)
        {
            public TypedValueObject getValueObjectFor(ClassInfo aClassinfo, Object aObjectToTransform)
            {
                return null;
            }

            public Object createNewObjectFor(ClassInfo aClassinfo) throws ObjectCreationException
            {
                // TODO Auto-generated method stub
                return null;
            }

        };

        assertFalse(creator.objectCreated);
        Object testCompObj = cachedObjCreator.createObjectFor(testCompany);
        assertTrue(creator.objectCreated);

        assertFalse(creator.objectUpdated);
        Object equalCompObj = cachedObjCreator.createObjectFor(equalCompany);
        assertTrue(creator.objectUpdated);

        assertTrue(testCompObj == equalCompObj);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args)
    {
        new ObjectCreatorTest("Create Tables").run();
    }

    /**
     * Marks the cal of the changes and the update Method in a flag
     *
     * @author rsc
     */
    @SuppressWarnings("serial")
    private static class MarkingObjectCreator implements UpdatableObjectCreator
    {
        private boolean objectCreated = false;
        private boolean objectUpdated = false;

        @Override
        public Object createObjectFor(TypedValueObject aTypedValueObject)
        {
            this.objectCreated = true;
            return aTypedValueObject;
        }

        @Override
        public void updateValuesInObject(TypedValueObject aValueModel, Object aObjectToUpdate) throws ObjectCreationException
        {
            this.objectUpdated = true;
        }

        @Override
        public Object createNewObjectFor(ClassInfo aClassinfo)
                throws ObjectCreationException
        {
            return null;
        }

        @Override
        public TypedValueObject getValueObjectFor(ClassInfo aClassinfo, Object aObjectToTransform)
        {
            return null;
        }
    }

}
