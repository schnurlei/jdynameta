package de.jdynameta.dbaccess.jdbc.mysql;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MysqlUtil
{

	public static DataSource createDatasource(final String aUser, String aPassword) {

	  // ConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		
        String dbUrl = System.getProperty("de.jdynameta.base.dbaccess.url");

        if (dbUrl == null || dbUrl.trim().length() == 0) {
            dbUrl = "jdbc:mysql:///jdynameta";
        }        
		
	   MysqlDataSource datasource = new MysqlDataSource();
	   datasource.setUser(aUser);
	   datasource.setPassword(aPassword);
	   datasource.setServerName("localhost");
	   datasource.setPort(3306);
//	   datasource.setDatabaseName("databasename");		
		
//		 Context ctx = new InitialContext();
//		 ctx.bind("jdbc/dsName", datasource);
		 
//		 com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory.
		return datasource;
	}
	
//	source = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource()
//	source.database = 'jdbc:database:mydatabase'
//	url="jdbc:mysql://127.0.0.1:3306/mydb?autoReconnect=true"/>
//	<Resource name="jdbc/MyDatabase" auth="Container" type="javax.sql.DataSource"
//		    maxActive="100" maxIdle="30" maxWait="10000"
//		    username="username" password="password" driverClassName="com.mysql.jdbc.Driver"
//		    url="jdbc:mysql://127.0.0.1:3306/mydb?autoReconnect=true"/>

}
