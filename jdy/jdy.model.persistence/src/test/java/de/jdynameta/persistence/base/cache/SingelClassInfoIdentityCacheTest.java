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
package de.jdynameta.persistence.base.cache;

import java.sql.SQLException;

import junit.framework.TestCase;
import de.jdynameta.base.cache.SingelClassInfoIdentityCache;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.metainfo.impl.ReferenceInPrimaryKeyImpl;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;


/** 
 *
 */
public class SingelClassInfoIdentityCacheTest extends TestCase 
{
    /**
     * Creates a new Test object.
     * 
     * @param name DOCUMENT ME!
     */
   	public SingelClassInfoIdentityCacheTest(String name) 
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
	public void testCacheInsert() throws Exception 
	{
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		
	
		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCompanyId(new Integer(100));		
		testCompany.setCompanyName("Wurstfabrik");		
		testCompany.setCity("Roggenburg");

		ReferenceInPrimaryKeyImpl referenceObj = new ReferenceInPrimaryKeyImpl();
		referenceObj.setCompany(testCompany);
		referenceObj.setValue1("testValue");

		CompanyImpl equalCompany = new CompanyImpl();
		equalCompany.setCompanyId(new Integer(100));		
		equalCompany.setCompanyName("Wurstfabrik");		
		
		ReferenceInPrimaryKeyImpl equalRefObj = new ReferenceInPrimaryKeyImpl();
		equalRefObj.setCompany(equalCompany);
		equalRefObj.setValue1("testValue");
		
		SingelClassInfoIdentityCache cache = new SingelClassInfoIdentityCache(repository.getReferenceInPrimaryKeyClassInfo(), 10 );
		
		cache.insertObjectForValueModel(referenceObj, referenceObj);

		assertTrue(cache.isObjectInCache(equalRefObj));

	}
}
