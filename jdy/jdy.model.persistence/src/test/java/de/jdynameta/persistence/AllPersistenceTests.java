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
package de.jdynameta.persistence;

import de.jdynameta.persistence.base.cache.ObjectCreatorTest;
import de.jdynameta.persistence.base.cache.SingelClassInfoIdentityCacheTest;
import de.jdynameta.persistence.base.creator.NameClassObjectCreatorTest;
import de.jdynameta.persistence.impl.persistentobject.ValueModelObjectCreatorTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author rsc
 */
public class AllPersistenceTests {

	public static Test suite() 
	{
		TestSuite suite =
			new TestSuite("Test for de.comafra.model.creator");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(NameClassObjectCreatorTest.class));
		suite.addTest(new TestSuite(ObjectCreatorTest.class));
		suite.addTest(new TestSuite(SingelClassInfoIdentityCacheTest.class));
		suite.addTest(new TestSuite(ValueModelObjectCreatorTest.class));
		//$JUnit-END$
		return suite;
	}
}
