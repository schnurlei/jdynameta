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
package de.jdynameta.metamodel.application;

import java.sql.Connection;

import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;

/**
 * Generate the Class Files from the Repository Info
 * @author Rainer Schneider
 *
 */
public class MetaModelTableGenerator 
{

	/**
	 * @param string
	 */
	public MetaModelTableGenerator() 
	{
		
	}


	public void createTables(Connection baseConnection, ClassRepository aRepository) throws JdyPersistentException 
	{
		SqlTableCreator tableCreator = new JdyJdbcTableCreator(baseConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());

		for( ClassInfo curInfo :  aRepository.getAllClassInfosIter())
		{
			try
			{
				deleteTable(curInfo, tableCreator);
			} catch (JdyPersistentException excp)
			{
				// excp.printStackTrace();
			}
			tableCreator.buildTableForClassInfo(curInfo);
			
		}
		

		
		createViews(tableCreator);

	}

	private void  deleteTable(ClassInfo curInfo, SqlTableCreator tableCreator) throws JdyPersistentException 
	{
		tableCreator.deleteTableForClassInfo(curInfo);
	}

	private void createViews(SqlTableCreator tableCreator) 
	{
		
//		tableCreator.createView( "ContentCountrySubaspectVIEW" 
//			, "select 	COC.CON_CTY_ID, con.con_id, coc.CTY_ID, con.cmp_id, SAS.sas_id, sas.asp_id " +//				" from CONTENT con, SUBASPECT SAS, CONTENTCOUNTRY COC " +//				" where  CON.CON_ID = COC.CON_ID " +//				"and CON.SAS_ID = SAS.SAS_ID ");
	}

//	/**
//	 * 
//	 * @param args DOCUMENT ME!
//	 * @throws Exception DOCUMENT ME!
//	 */
//	public static void main(String[] args) throws Exception {
//		try {
//
//			Connection baseConnection = null;
//			final String dbUrl = "jdbc:mysql:///test";
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//			baseConnection = DriverManager.getConnection(dbUrl);
//			
//
//			
//			new MetaModelTableGenerator().createTables( baseConnection, new MetaModelRepository());
//
//			if (baseConnection != null) {
//
//				try {
//					baseConnection.close();
//				} catch (SQLException SQLE) {
//					
//				}
//			}
//
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	 }

}
