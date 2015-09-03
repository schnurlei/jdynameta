package de.jdynameta.dbaccess.jdbc.connection;

import java.sql.SQLException;

import org.hsqldb.jdbc.JDBCDataSource;

import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.ReflectionValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.dbaccess.simpletest.SimpleBaseTestCase;
import de.jdynameta.dbaccess.simpletest.SimpleTableCreatorTest;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;

public class JdbcConnectionTest extends SimpleBaseTestCase
{
    public JdbcConnectionTest(String aName)
    {
        super(aName);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        SimpleTableCreatorTest creator = new SimpleTableCreatorTest("insert");
        try
        {
            creator.createTables(this.baseConnection.getConnection(), new SimpleMetaInfoRepository());
        } catch (Exception e)
        {
            // ignore
        }
    }

    public JdbcConnection<ValueObject, ReflectionValueObject> createBaseConnection() throws SQLException
    {

        JdbcConnection<ValueObject, ReflectionValueObject> newBaseConnection
                = new JdbcConnection<>(createDatasource(dbUrl));

        return newBaseConnection;
    }

    private JDBCDataSource createDatasource(final String aDbUrl) throws SQLException
    {
        JDBCDataSource datasource = new JDBCDataSource();
        datasource.setDatabase(aDbUrl);
        datasource.setUser("sa");
        datasource.setPassword("");
        datasource.setLoginTimeout(5);
//		 Context ctx = new InitialContext();
//		 ctx.bind("jdbc/dsName", datasource);
        return datasource;
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testLoadValuesCompany() throws Exception
    {
        JdbcConnection<ValueObject, ReflectionValueObject> connection = createBaseConnection();
        connection.setObjectTransformator(new ValueModelObjectCreator(new NameCreator()));

        SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();

        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(100);
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");

        connection.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        testCompany.setCompanyId(101);
        connection.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        try
        {
            testCompany.setCompanyId(null);
            connection.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        } catch (Exception e)
        {
            // should fail, invalid key
        }
        testCompany.setCompanyId(104);
        connection.insertObjectInDb(testCompany, repository.getCompanyClassInfo());

        //		
//		JdbcObjectReader reader = new JdbcObjectReader(this.baseConnection, new JDyDefaultRepositoryTableMapping());
//		ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
//		
//		DefaultClassInfoQuery filter = new DefaultClassInfoQuery(repository.getCompanyClassInfo());
//		System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator ).size());
//		
//		DefaultOperatorExpression idExpr = new DefaultOperatorExpression();
//		idExpr.setAttributeInfo((PrimitiveAttributeInfo)repository.getCompanyClassInfo().getAttributeInfoForExternalName("CompanyId"));
//		idExpr.setCompareValue(new Integer(101));
//		idExpr.setMyOperator(new DefaultOperatorEqual());
//		filter.setFilterExpression(idExpr);
//		System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator ).size() );
//
//		DefaultOperatorExpression nameExpr = new DefaultOperatorExpression();
//		nameExpr.setAttributeInfo((PrimitiveAttributeInfo)repository.getCompanyClassInfo().getAttributeInfoForExternalName("CompanyName"));
//		nameExpr.setCompareValue("Wurstfabrik");
//		nameExpr.setMyOperator(new DefaultOperatorEqual());
//		filter.setFilterExpression(nameExpr);
//		System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator ).size() );
//
//		ArrayList<ObjectFilterExpression> exprList = new ArrayList<ObjectFilterExpression>();
//		exprList.add(idExpr);
//		exprList.add(nameExpr);
//		DefaultExpressionAnd andExpr = new DefaultExpressionAnd(exprList);
//		filter.setFilterExpression(andExpr);
//		System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator ).size() );
    }

    @SuppressWarnings("serial")
    private class ValueModelObjectCreator<TCreatedObjFromValueObj> extends AbstractReflectionCreator<TCreatedObjFromValueObj>
            implements ObjectTransformator<ValueObject, TCreatedObjFromValueObj>
    {

        public ValueModelObjectCreator(ClassNameCreator aNameCreator)
        {
            super(aNameCreator);
        }

        @Override
        public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,
                ValueObject aObjectToTransform)
        {
            return new TypedWrappedValueObject(aObjectToTransform, aClassinfo);
        }

        @Override
        protected TCreatedObjFromValueObj createProxyObjectFor(
                TypedValueObject aObjToHandle)
        {
            return null;
        }

        @Override
        protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
                TCreatedObjFromValueObj aObjoSetVals,
                TypedValueObject aObjToGetVals) throws ObjectCreationException
        {

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
