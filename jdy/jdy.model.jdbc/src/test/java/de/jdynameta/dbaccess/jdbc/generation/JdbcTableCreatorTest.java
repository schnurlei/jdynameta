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
package de.jdynameta.dbaccess.jdbc.generation;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyObjectReferenceModel;
import de.jdynameta.base.metainfo.impl.JdyPrimitiveAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyTextType;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;
import de.jdynameta.testcommon.util.sql.SqlUtil;

/**
 * Create Table in
 *
 * @author Rainer Schneider
 */
public abstract class JdbcTableCreatorTest extends TestCase
{
    private SqlTableCreator tableCreator;
    private Connection baseConnection;

    /**
     * Creates a new NumbersTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdbcTableCreatorTest(String name)
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
        this.baseConnection = createBaseConnection();
        this.tableCreator = createTableCreator(baseConnection);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        this.baseConnection.close();
    }

    protected SqlTableCreator getTableCreator()
    {
        return this.tableCreator;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws InvalidClassInfoException
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testCreateTables() throws Exception
    {
        for (ClassInfo curInfo : new ComplexTestDataMetaInfoRepository().getAllClassInfosIter())
        {
            try
            {
                tableCreator.deleteTableForClassInfo(curInfo);
            } catch (JdyPersistentException excp)
            {
                excp.printStackTrace();
            }
            tableCreator.buildTableForClassInfo(curInfo);
            assertTrue(new SqlUtil(baseConnection).existsTableInDatabase(curInfo.getExternalName()));

        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws JdyPersistentException
     */
    public void testCreateTablesForClassReferences() throws JdyPersistentException
    {
        SqlTableCreator creator = new JdyJdbcTableCreator(this.baseConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());

        JdyClassInfoModel aBaseClassInfo = new JdyClassInfoModel();
        aBaseClassInfo.setExternalName("BaseClass");
        aBaseClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "BaseClassKey", "BaseClassKey", true, true));
        aBaseClassInfo.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "BaseClassNonKey", "BaseClassNonKey", false, false));

        JdyClassInfoModel aRefClass1 = new JdyClassInfoModel();
        aRefClass1.setExternalName("RefClass1");
        aRefClass1.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref1Key", "Ref1Key", true, true));
        aRefClass1.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref1NonKey", "Ref1NonKey", false, false));

        JdyClassInfoModel aRefClass2 = new JdyClassInfoModel();
        aRefClass2.setExternalName("RefClass2");
        aRefClass2.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref2Key", "Ref2Key", true, true));
        aRefClass2.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref2NonKey", "Ref2NonKey", false, false));

        JdyClassInfoModel aRefClass11 = new JdyClassInfoModel();
        aRefClass11.setExternalName("RefClass11");
        aRefClass11.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref11Key", "Ref11Key", true, true));
        aRefClass11.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref11NonKey", "Ref11NonKey", false, false));

        JdyClassInfoModel aRefClass12 = new JdyClassInfoModel();
        aRefClass12.setExternalName("RefClass11");
        aRefClass12.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref12Key", "Ref12Key", true, true));
        aRefClass12.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref12NonKey", "Ref12NonKey", false, false));

        JdyClassInfoModel aRefClass21 = new JdyClassInfoModel();
        aRefClass21.setExternalName("RefClass21");
        aRefClass21.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref21Key", "Ref21Key", true, true));
        aRefClass21.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref21NonKey", "Ref21NonKey", false, false));

        JdyClassInfoModel aRefClass22 = new JdyClassInfoModel();
        aRefClass22.setExternalName("RefClass22");
        aRefClass22.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref22Key", "Ref22Key", true, true));
        aRefClass22.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(), "Ref22NonKey", "Ref22NonKey", false, false));

        aBaseClassInfo.addAttributeInfo(new JdyObjectReferenceModel(aRefClass1, "RefClass1", "RefClass1", true, true));
        aBaseClassInfo.addAttributeInfo(new JdyObjectReferenceModel(aRefClass2, "RefClass2", "RefClass2", false, false));

        aRefClass1.addAttributeInfo(new JdyObjectReferenceModel(aRefClass11, "RefClass11", "RefClass11", true, true));
        aRefClass1.addAttributeInfo(new JdyObjectReferenceModel(aRefClass12, "RefClass12", "RefClass12", false, false));

        aRefClass2.addAttributeInfo(new JdyObjectReferenceModel(aRefClass21, "RefClass21", "RefClass21", true, true));
        aRefClass2.addAttributeInfo(new JdyObjectReferenceModel(aRefClass22, "RefClass22", "RefClass22", false, false));

        try
        {
            creator.deleteTableForClassInfo(aBaseClassInfo);
        } catch (JdyPersistentException excp)
        {
            excp.printStackTrace();
        }
        creator.buildTableForClassInfo(aBaseClassInfo);

    }

    protected abstract Connection createBaseConnection() throws Exception;

    protected abstract SqlTableCreator createTableCreator(Connection aConnection) throws Exception;

}
