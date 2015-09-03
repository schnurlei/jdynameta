package de.jdynameta.dbaccess.jdbc.hsqldb;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.sql.DataSource;

import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JDyDefaultClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnection;


public class HsqlUtil
{
	public static final String DEFAULT_USER = "sa";
	public static final String DEFAULT_PASSWD = "sa";

	public static <TObjToWrite, TReadedObj> JdbcConnection<TObjToWrite, TReadedObj> createBaseConnection(DataSource datasource,  String aSchemaName)
	{
		JdbcConnection<TObjToWrite, TReadedObj> newBaseConnection 
			= new JdbcConnection<>(datasource, null, new HSqlDbClassInfoToTableMapping(aSchemaName));
		return newBaseConnection;
	}
	
	public static JDBCDataSource createDatasource(final String aDbUrl, String aUser, String aPasswd) {
		JDBCDataSource datasource = new JDBCDataSource();
		 datasource.setDatabase(aDbUrl);
		 datasource.setUser(aUser);
		 datasource.setPassword(aPasswd);
//		 Context ctx = new InitialContext();
//		 ctx.bind("jdbc/dsName", datasource);
		return datasource;
	}
	
	public static void startServer(final File aDbFile, String aSchemaName) throws IOException, AclFormatException 
	{
		HsqlProperties p = new HsqlProperties();
	    p.setProperty("server.database.0","file:" +aDbFile.getAbsolutePath());
		p.setProperty("server.dbname.0",aSchemaName);
		// set up the rest of properties
		Server server = new Server();
		server.setProperties(p);
		server.setLogWriter(new PrintWriter(System.out)); // can use custom writer
		server.setErrWriter(new PrintWriter(System.out)); // can use custom writer
		server.start();
	
	}
	
	public static class HSqlDbClassInfoToTableMapping extends JDyDefaultRepositoryTableMapping 
	{
		private static final long serialVersionUID = 1L;
		private final String schemaName;
		
		public HSqlDbClassInfoToTableMapping(String aSchemaName)
		{
			this.schemaName = aSchemaName;
			
		}
		
                @Override
		public JDyClassInfoToTableMapping getTableMappingFor(final ClassInfo aClassInfo)
		{
			return new JDyDefaultClassInfoToTableMapping(aClassInfo)
			{
				@Override
				protected String getQualifiedTableName(ClassInfo curInfo)
				{
					return schemaName +"." + curInfo.getExternalName();
				}
			};
		}

	}	

}
