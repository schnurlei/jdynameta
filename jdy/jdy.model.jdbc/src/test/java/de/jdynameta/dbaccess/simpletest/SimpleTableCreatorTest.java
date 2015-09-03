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
package de.jdynameta.dbaccess.simpletest;

import java.sql.Connection;
import java.sql.SQLException;

import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;


/** 
 *
 * @author  Rainer Schneider
 *@version $Id: MetaInfoTableCreatorTest.java,v 1.7 2010-06-19 10:50:29 rainer Exp $
 */
public class SimpleTableCreatorTest extends SimpleBaseTestCase {

    /**
     * Creates a new NumbersTest object.
     * 
     * @param name DOCUMENT ME!
     */
   	public SimpleTableCreatorTest(String name) 
   	{
        super(name);
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws Exception DOCUMENT ME!
     */
    @Override
	public void setUp() throws Exception 
    {
        super.setUp();
    }

    /**
     * 
     * @throws JdyPersistentException
     */
    public void testCreateTables() throws JdyPersistentException 
    {
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		
		createTables(this.baseConnection.getConnection(), repository);
    }

	public void createTables(Connection aConnection, SimpleMetaInfoRepository aRepository) throws JdyPersistentException 
	{
		SqlTableCreator creator = new JdyJdbcTableCreator(aConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());

		deleteTableIfExist(creator, aRepository.getCompanyClassInfo());
        deleteTableIfExist(creator, aRepository.getContactClassInfo());
        deleteTableIfExist(creator, aRepository.getNoteClassInfo());
        deleteTableIfExist(creator, aRepository.getEmployeeClassInfo());
        deleteTableIfExist(creator, aRepository.getChiefEmployeeClassInfo());
		creator.buildTableForClassInfo(aRepository.getCompanyClassInfo());
		creator.buildTableForClassInfo(aRepository.getContactClassInfo());
		creator.buildTableForClassInfo(aRepository.getNoteClassInfo());
		creator.buildTableForClassInfo(aRepository.getEmployeeClassInfo());
		creator.buildTableForClassInfo(aRepository.getChiefEmployeeClassInfo());
	}

	private void deleteTableIfExist(SqlTableCreator creator, ClassInfo aInfo)
	{
		try
		{
			creator.deleteTableForClassInfo(aInfo);
		} catch (JdyPersistentException e)
		{
			//Ignore
		}
	}
}
