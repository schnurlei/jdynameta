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
package de.jdynameta.persistence.base.creator;

import java.sql.SQLException;

import junit.framework.TestCase;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;
import de.jdynameta.persistence.cache.CachedObjectTransformator;
import de.jdynameta.persistence.impl.proxy.ReflectionObjectCreator;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.simple.Company;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;

/**
 *
 */
public class NameClassObjectCreatorTest extends TestCase
{
    /**
     * Creates a new Test object.
     *
     * @param name DOCUMENT ME!
     */
    public NameClassObjectCreatorTest(String name)
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
    public void testProxyResolver() throws Exception
    {
        TypedHashedValueObject testCompany = new TypedHashedValueObject(SimpleMetaInfoRepository.getSingleton().getCompanyClassInfo());
        testCompany.setValue(SimpleMetaInfoRepository.getSingleton().getCompanyClassInfo().getAttributeInfoForExternalName("CompanyId"), 100);
        testCompany.setValue(SimpleMetaInfoRepository.getSingleton().getCompanyClassInfo().getAttributeInfoForExternalName("CompanyName"), "Wurstfabrik");
        testCompany.setValue(SimpleMetaInfoRepository.getSingleton().getCompanyClassInfo().getAttributeInfoForExternalName("City"), "Roggenburg");

        CachedObjectTransformator<Company, TypedHashedValueObject> cachedObjCreator = new CachedObjectTransformator<>();
        MarkingProxyResolver proxyResolver = new MarkingProxyResolver(cachedObjCreator);
        ReflectionObjectCreator proxyCreator = new ReflectionObjectCreator(proxyResolver, new NameCreator(), null);
        cachedObjCreator.setNewObjectCreator(proxyCreator);

        Object testCompObj = cachedObjCreator.createObjectFor(testCompany);

        assertFalse(proxyResolver.proxyResolved);
        ((Company) testCompObj).getCompanyId();
        assertFalse(proxyResolver.proxyResolved);
        ((Company) testCompObj).getStreet();
        assertTrue(proxyResolver.proxyResolved);

    }

    /**
     * Marks the cal of the changes and the update Method in a flag
     *
     * @author rsc
     */
    private static class MarkingProxyResolver implements ProxyResolver
    {
        private boolean proxyResolved = false;
        private final CachedObjectTransformator cachedObjCreator;

        public MarkingProxyResolver(CachedObjectTransformator aCachedObjCreator)
        {
            this.cachedObjCreator = aCachedObjCreator;
        }

        @Override
        public void resolveProxy(TypedValueObject aValueModel) throws ObjectCreationException
        {
            this.proxyResolved = true;

            // create Value model with all values set
            CompanyImpl testCompany = new CompanyImpl();
            testCompany.setCompanyId(100);
            testCompany.setCompanyName("Wurstfabrik");
            testCompany.setCity("Rottenberg");
            testCompany.setStreet("Tassenweg");

            cachedObjCreator.createObjectFor(testCompany);
        }

    }

    @SuppressWarnings("serial")
    public static class NameCreator extends DefaultClassNameCreator
    {
        @Override
        public String getPackageNameFor(ClassInfo aInfo)
        {

            return "de.jdynameta.testcommon.model.metainfo.impl";
        }
    }

}
