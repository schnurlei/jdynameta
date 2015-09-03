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
package de.jdynameta.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.hsqldb.HSqlSchemaHandler;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.metamodel.application.ApplicationRepository;

/**
 * Servel that provieds a REST/JSON access to Metadata
 *
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class JDynametaHsqldbServlet extends HttpServlet
{

    public JDynametaHsqldbServlet()
    {

    }

    @Override
    public void init() throws ServletException
    {
        super.init();

        File databasePath = new File(System.getProperty("user.home"), ".jDyAppeditor");
        getServletContext().log("+++++++++++Home Path " + databasePath.getAbsolutePath());
        databasePath = new File(getServletContext().getRealPath("/") + File.separator + ".jDyAppeditor");
        getServletContext().log("+++++++++++Context Path " + databasePath.getAbsolutePath());

        getServletContext().log("+++++++++++++++++++++++++++Init");
        HSqlSchemaHandler hsqlConnection;
        try
        {
            startDatabaseServer(new File(databasePath, "TestDbRsc"), "JDynaMeta");
            String schema = "JDynaMeta";
            DataSource datasource = HsqlUtil.createDatasource("jdbc:hsqldb:hsql://localhost/JDynaMeta", "sa", "sa");
            hsqlConnection = new HSqlSchemaHandler(datasource);

//			hsqlConnection.deleteSchema();
            if (!hsqlConnection.existsSchema(schema))
            {
                getServletContext().log("++++Create Schema");
                hsqlConnection.createSchema(schema, ApplicationRepository.getSingleton());
            } else
            {
                hsqlConnection.validateSchema(schema, ApplicationRepository.getSingleton());
            }

        } catch (JdyPersistentException | JdbcSchemaHandler.SchemaValidationException | IOException | AclFormatException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void startDatabaseServer(final File aDbFile, String aSchemaName) throws IOException, AclFormatException
    {
        HsqlProperties p = new HsqlProperties();
        p.setProperty("server.database.0", "file:" + aDbFile.getAbsolutePath());
        p.setProperty("server.dbname.0", aSchemaName);
        // set up the rest of properties
        Server server = new Server();
        server.setProperties(p);
        server.setLogWriter(new PrintWriter(System.out)); // can use custom writer
        server.setErrWriter(new PrintWriter(System.out)); // can use custom writer

        System.out.println(server.getAddress());
        System.out.println(server.getPort());
        System.out.println(server.getProductName());
        System.out.println(server.getProductVersion());
        System.out.println(server.getProtocol());
        System.out.println(server.getServerId());
        server.start();

    }

}
