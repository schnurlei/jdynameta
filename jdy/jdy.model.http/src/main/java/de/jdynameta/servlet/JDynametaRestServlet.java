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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.TransformerConfigurationException;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.FilterUtil;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedWrappedValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.dbaccess.jdbc.hsqldb.HSqlSchemaHandler;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.json.JsonFileReader;
import de.jdynameta.json.JsonFileWriter;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.MetaRepositoryCreator;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.persistence.manager.impl.ValueModelPersistenceObjectManager;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 * Servel that provieds a REST/JSON access to Metadata
 *
 * @author rs
 *
 */
public class JDynametaRestServlet extends HttpServlet
{
    private Map<String, PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl>> namespace2Manager;

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private MetaDataPersistenceManager metaPersManager;
    private ApplicationRepository metaRepository;
    private JsonFileWriter fileWriter;
    private final JsonFileReader fileReader;

    public JDynametaRestServlet()
    {
        this.fileReader = new JsonFileReader();

    }

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.namespace2Manager = new HashMap<>();
        fileWriter = new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true);
        getServletContext().log("+++++++++++++++++++++++++++Init JDynametaRestServlet");
        HSqlSchemaHandler hsqlConnection;
        try
        {
            String schema = "JDynaMeta";
            DataSource datasource = HsqlUtil.createDatasource("jdbc:hsqldb:hsql://localhost/JDynaMeta", "sa", "sa");
            hsqlConnection = new HSqlSchemaHandler(datasource);

            this.metaRepository = ApplicationRepository.getSingleton();
//			if( hsqlConnection.existsSchema()) {
//				hsqlConnection.deleteSchema();
//			}
            if (!hsqlConnection.existsSchema(schema))
            {
                getServletContext().log("++++Create Schema");
                hsqlConnection.createSchema(schema, this.metaRepository);
            } else
            {
                hsqlConnection.validateSchema(schema, this.metaRepository);
            }

            metaPersManager = new MetaDataPersistenceManager(hsqlConnection.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(schema));

        } catch (JdyPersistentException | JdbcSchemaHandler.SchemaValidationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> getPersistentManagerFor(String nameSpace) throws JdyPersistentException
    {
        PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> manager = this.namespace2Manager.get(nameSpace);

        if (manager == null)
        {
            manager = createPersistenceManagerForNamespace(nameSpace);
            this.namespace2Manager.put(nameSpace, manager);
        }
        return manager;
    }

    private PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> createPersistenceManagerForNamespace(String nameSpace) throws JdyPersistentException
    {
        DataSource datasource = HsqlUtil.createDatasource("jdbc:hsqldb:hsql://localhost/JDynaMeta", "sa", "sa");
        HSqlSchemaHandler hsqlConnection2 = new HSqlSchemaHandler(datasource);
        hsqlConnection2.existsSchema(nameSpace);
        PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persmanager
                = new ValueModelPersistenceObjectManager(hsqlConnection2.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(nameSpace));
//		Application2PersistenceBridgeGeneric genericAppManager = new Application2PersistenceBridgeGeneric(persmanager);

        return persmanager;
    }

    @Override
    /**
     * do a query on the defined ClassInfo defined by the path information
     *
     * the response is a Json String with the read objects
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (metaPersManager == null)
        {
            throw new ServletException("Database not initialized");
        }

        PathInfo pathInfo = new PathInfo(req, metaPersManager);
        Map paraMap = req.getParameterMap();

        try
        {
            ClassInfo classInfo = pathInfo.getClassInfo();
            if (classInfo == null)
            {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else
            {
                DefaultClassInfoQuery query = new DefaultClassInfoQuery(classInfo);
                if (!paraMap.isEmpty())
                {
                    getServletContext().log("+++++++++++++++++++++++++++Handle GET parameter");
                }

                try
                {
                    ObjectList<? extends TypedValueObject> objects = null;
                    if (pathInfo.isMetaDataNamespace())
                    {
                        objects = metaPersManager.loadObjectsFromDb(query);
                    } else
                    {
                        PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persistentManager
                                = getPersistentManagerFor(pathInfo.getNamespace());
                        objects = persistentManager.loadObjectsFromDb(query);
                    }

                    try
                    {
                        fileWriter.writeObjectList(resp.getWriter(), classInfo, objects, Operation.READ);
                    } catch (TransformerConfigurationException e)
                    {
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } catch (JdyPersistentException e)
                {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (ParseException | JdyPersistentException ex)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No valid class in namespace: " + ex.getLocalizedMessage());
        }

    }

    @Override
    /**
     * convert the request content into a ValueObject and update the object into
     * the database the request path is interpreted as ClassInfo the request
     * content is a JSON Object of the ClssInfo
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (metaPersManager == null)
        {
            throw new ServletException("Database not initialized");
        }

        PathInfo pathInfo = new PathInfo(req, metaPersManager);

        try
        {
            ClassInfo classInfo = pathInfo.getClassInfo();
            try
            {
                String content = readFromStream(req.getInputStream());
                ObjectList<ApplicationObj> objectList = fileReader.readObjectList(new StringReader(content), classInfo);
                ApplicationObj objectToUpdate = objectList.get(0);

                TypedHashedWrappedValueObject<TypedValueObject> wrappedObj = null;
                if (pathInfo.isMetaDataNamespace())
                {
                    wrappedObj = resolveObjectReferences(objectToUpdate, classInfo, metaPersManager);
                    metaPersManager.updateObject(wrappedObj, classInfo);
                } else
                {
                    PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persistentManager = getPersistentManagerFor(pathInfo.getNamespace());
                    wrappedObj = resolveObjectReferences(objectToUpdate, classInfo, persistentManager);
                    persistentManager.updateObject(wrappedObj, classInfo);
                }

                try
                {
                    DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(wrappedObj);
                    fileWriter.writeObjectList(resp.getWriter(), classInfo, singleElementList, Operation.READ);
                } catch (TransformerConfigurationException e)
                {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }

            } catch (JdyPersistentException e)
            {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ParseException | JdyPersistentException ex)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No valid class in namespace: " + ex.getLocalizedMessage());
        }

    }

    @Override
    /**
     * convert the request content into a ValueObject and insert the object into
     * the database the request path is interpreted as ClassInfo the request
     * content is a JSON Object of the ClassInfo defined by the path
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (metaPersManager == null)
        {
            throw new ServletException("Database not initialized");
        }

        PathInfo pathInfo = new PathInfo(req, metaPersManager);

        try
        {
            ClassInfo classInfo = pathInfo.getClassInfo();
            try
            {
                String content = readFromStream(req.getInputStream());
                ObjectList<ApplicationObj> allObjects = fileReader.readObjectList(new StringReader(content), classInfo);
                TypedValueObject objectToInsert = allObjects.get(0);

                TypedValueObject newObject;
                if (pathInfo.isMetaDataNamespace())
                {
                    TypedHashedWrappedValueObject<TypedValueObject> wrappedObj = resolveObjectReferences(objectToInsert, classInfo, metaPersManager);
                    newObject = metaPersManager.insertObject(wrappedObj, classInfo);
                } else
                {
                    PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persistentManager = getPersistentManagerFor(pathInfo.getNamespace());
                    TypedHashedWrappedValueObject<TypedValueObject> wrappedObj = resolveObjectReferences(objectToInsert, classInfo, persistentManager);
                    newObject = persistentManager.insertObject(wrappedObj, classInfo);
                }

                try
                {
                    resp.setBufferSize(16 * 1024);
                    DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(new TypedWrappedValueObject(newObject, classInfo));
                    StringWriter writer = new StringWriter();
                    fileWriter.writeObjectList(writer, classInfo, singleElementList, Operation.READ);
                    resp.getWriter().print(writer.toString());

                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } catch (TransformerConfigurationException ex)
                {
                    ex.printStackTrace();
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error coverting request: " + ex.getLocalizedMessage());
                }

            } catch (JdyPersistentException ex)
            {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Error writign to database: " + ex.getLocalizedMessage());
            }
        } catch (ParseException | JdyPersistentException ex)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No valid class in namespace: " + ex.getLocalizedMessage());
        }

    }

    @Override
    /**
     * convert the request content into a ValueObject and delete the object from
     * the database
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        if (metaPersManager == null)
        {
            throw new ServletException("Database not initialized");
        }

        Map paraMap = req.getParameterMap();
        PathInfo pathInfo = new PathInfo(req, metaPersManager);

        try
        {
            ClassInfo classInfo = pathInfo.getClassInfo();

            if (!paraMap.isEmpty())
            {
                getServletContext().log("+++++++++++++++++++++++++++Handle DELETE parameter");

                RequestParameterHandler handler = new RequestParameterHandler(paraMap, classInfo, null);
                classInfo.handleAttributes(handler, null);
                TypedValueObject objToDelete = handler.result;

                TypedHashedWrappedValueObject<TypedValueObject> wrappedObj;
                if (pathInfo.isMetaDataNamespace())
                {
                    wrappedObj = resolveObjectReferences(objToDelete, classInfo, metaPersManager);
                    metaPersManager.deleteObject(wrappedObj, classInfo);
                } else
                {
                    PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persistentManager = getPersistentManagerFor(pathInfo.getNamespace());
                    wrappedObj = resolveObjectReferences(objToDelete, classInfo, persistentManager);
                    metaPersManager.deleteObject(wrappedObj, classInfo);
                }

                resp.setContentType("application/json;charset=utf-8");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else
            {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Parameter in request empty . ");
            }
        } catch (ParseException ex)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No valid class in namespace: " + ex.getLocalizedMessage());
        } catch (JdyPersistentException ex)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getLocalizedMessage());
        }

    }

    private TypedHashedWrappedValueObject<TypedValueObject> resolveObjectReferences(final TypedValueObject typedValueObject, ClassInfo aClassInfo, final PersistentObjectManager<? extends ValueObject, ? extends TypedValueObject> aMetaPersManager) throws JdyPersistentException
    {
        final TypedHashedWrappedValueObject<TypedValueObject> wrapper = new TypedHashedWrappedValueObject<>(aClassInfo, typedValueObject);
        for (AttributeInfo attribute : aClassInfo.getAttributeInfoIterator())
        {
            attribute.handleAttribute(new AttributeHandler()
            {

                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
                {
                    // Ignorem only replace references
                }

                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                        throws JdyPersistentException
                {
                    ValueObject referencedObj = (ValueObject) typedValueObject.getValue(aInfo);
                    if (referencedObj != null)
                    {
                        ObjectList<? extends TypedValueObject> resolvedObj = aMetaPersManager.loadObjectsFromDb(FilterUtil.createSearchEqualObjectFilter(aInfo.getReferencedClass(), referencedObj));
                        wrapper.setValue(aInfo, resolvedObj.get(0));
                    }
                }

            }, typedValueObject);

        }

        return wrapper;
    }

    public ClassInfo getClassFromRepository(ClassRepository repository, String internalClassName)
    {
        ClassInfo resultClass = null;

        for (ClassInfo curInfo : repository.getAllClassInfosIter())
        {
            if (curInfo.getInternalName().equals(internalClassName))
            {
                resultClass = curInfo;
                break;
            }
        }

        return resultClass;
    }

    private void analyseRequest(HttpServletRequest req, PrintWriter out) throws IOException
    {
        BufferedReader reader = req.getReader();

        String line;
        do
        {
            line = reader.readLine();
            if (line != null)
            {
                out.println("<p>" + line + "</p>");
            } else
            {
                out.println("<p>" + null + "</p>");
            }
        } while (line != null);
    }

    /**
     * Convert input stream to String
     * @param aInpuStream
     * @return 
     */
    protected static String readFromStream(InputStream aInpuStream)
    {

        BufferedInputStream bufferedInput = null;
        byte[] buffer = new byte[1024];

        StringBuilder stringbuffer = new StringBuilder(5000);
        try
        {

            bufferedInput = new BufferedInputStream(aInpuStream);

            int bytesRead;

            //Keep reading from the file while there is any content
            //when the end of the stream has been reached, -1 is returned
            while ((bytesRead = bufferedInput.read(buffer)) != -1)
            {

                //Process the chunk of bytes read
                //in this case we just construct a String and print it out
                String chunk = new String(buffer, 0, bytesRead);
                stringbuffer.append(chunk);
            }

        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            //Close the BufferedInputStream
            try
            {
                if (bufferedInput != null)
                {
                    bufferedInput.close();
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return stringbuffer.toString();

    }

    public void showInHtmlBody(String text, PrintWriter out)
    {
        out.println("<HTML>");
        out.println("<HEAD><TITLE>Hello World</TITLE></HEAD>");
        out.println("<BODY>");
        out.println("<p>" + text + "</p>");

        out.println("</BODY></HTML>");

    }

    /**
     * Split the path info of the request into namespace, classinfo, filter
     *
     * @author rainer
     *
     */
    public static class PathInfo
    {
        private final String namespace;
        private String className;
        private String filter;
        private final ApplicationRepository metaRepository;
        private final MetaDataPersistenceManager metaPersManager;

        /**
         *
         * @param req
         * @param aMetaPersManager
         */
        public PathInfo(HttpServletRequest req, MetaDataPersistenceManager aMetaPersManager)
        {
            this.metaRepository = ApplicationRepository.getSingleton();
            this.metaPersManager = aMetaPersManager;
            String localName = req.getPathInfo();

            String[] callPath = localName.split("/");
            if (callPath.length < 2)
            {
                this.namespace = null;
            } else
            {
                this.namespace = callPath[1];
                if (callPath.length >= 3)
                {
                    this.className = callPath[2];
                }
            }
        }

        public boolean isMetaDataNamespace()
        {
            return getNamespace().equals("@jdy");
        }

        public String getNamespace()
        {
            return namespace;
        }

        public String getClassName()
        {
            return className;
        }

        public String getFilter()
        {
            return filter;
        }

        public ClassInfo getClassInfo() throws ParseException, JdyPersistentException
        {
            if (getNamespace() != null)
            {
                if (isMetaDataNamespace())
                {
                    if (getClassName() == null)
                    {
                        throw new ParseException("No valid Classname in namespace: " + getNamespace(), 0);
                    }
                    return getClassFromRepository(metaRepository, getClassName());
                } else
                {
                    return readClassInfoFromMetadata();
                }
            } else
            {
                throw new ParseException("No valid namespace: " + getNamespace(), 0);
            }
        }

        private ClassInfo readClassInfoFromMetadata() throws JdyPersistentException
        {
            ClassInfo classInfoResult = null;
            DefaultClassInfoQuery query = new DefaultClassInfoQuery(metaRepository.getRepositoryModel());
            query.setFilterExpression(DefaultOperatorEqual.createEqualExpr("applicationName", getNamespace(), metaRepository.getRepositoryModel()));

            ObjectList<GenericValueObjectImpl> readList = this.metaPersManager.loadObjectsFromDb(query);
            if (readList.size() > 0)
            {
                AppRepository appRepository = (AppRepository) readList.get(0);
                MetaRepositoryCreator repCreator = new MetaRepositoryCreator(null);
                ClassRepository metaRep = repCreator.createMetaRepository(appRepository);

                for (ClassInfo classInfo : metaRep.getAllClassInfosIter())
                {
                    if (classInfo.getInternalName().equals(getClassName()))
                    {
                        classInfoResult = classInfo;
                        break;
                    }
                }
            }

            return classInfoResult;
        }

        public ClassInfo getClassFromRepository(ClassRepository repository, String internalClassName)
        {
            ClassInfo resultClass = null;

            for (ClassInfo curInfo : repository.getAllClassInfosIter())
            {
                if (curInfo.getInternalName().equals(internalClassName))
                {
                    resultClass = curInfo;
                    break;
                }
            }

            return resultClass;
        }

    }

    public class RequestParameterHandler implements AttributeHandler
    {
        private final Map paraMap;
        private TypedHashedValueObject result;
        private final List<AttributeInfo> aspectPath;

        public RequestParameterHandler(Map aRequestParaMap, ClassInfo aConcreteClass, List<AttributeInfo> anAspectPath)
        {
            super();
            this.result = new TypedHashedValueObject();
            this.result.setClassInfo(aConcreteClass);
            this.paraMap = aRequestParaMap;
            this.aspectPath = anAspectPath;
        }

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                throws JdyPersistentException
        {

            if (aInfo.isKey())
            {
                List<AttributeInfo> refAspectPath = new ArrayList<>();
                if (this.aspectPath != null)
                {
                    refAspectPath.addAll(this.aspectPath);
                }
                refAspectPath.add(aInfo);
                RequestParameterHandler refHandler = new RequestParameterHandler(paraMap, aInfo.getReferencedClass(), refAspectPath);
                aInfo.getReferencedClass().handleAttributes(refHandler, null);
                result.setValue(aInfo, refHandler.result);
            }
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                throws JdyPersistentException
        {
            String parameterName = createParameterName(this.aspectPath, aInfo);
            if (aInfo.isKey())
            {
                String[] values = (String[]) this.paraMap.get(parameterName);

                if (values == null || values.length == 0)
                {
                    throw new JdyPersistentException("Missing value for type in attr value: " + aInfo.getInternalName());
                }
                if (values[0] == null || values[0].trim().length() == 0)
                {
                    result.setValue(aInfo, null);
                } else
                {
                    this.result.setValue(aInfo, aInfo.getType().handlePrimitiveKey(new StringValueGetVisitor(values[0])));

                }
            }
        }

        private String createParameterName(List<AttributeInfo> aAspectPath, PrimitiveAttributeInfo aInfo)
        {
            String result = "";
            if (this.aspectPath != null)
            {
                for (AttributeInfo attributeInfo : aAspectPath)
                {
                    result += attributeInfo.getInternalName() + ".";
                }
            }

            result += aInfo.getInternalName();
            return result;
        }
    }

    public static class StringValueGetVisitor implements PrimitiveTypeGetVisitor
    {
        private final String attrValue;

        /**
         *
         */
        public StringValueGetVisitor(String anAttrValue)
        {
            super();
            this.attrValue = anAttrValue;
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.BooleanType)
         */
        public Boolean handleValue(BooleanType aType) throws JdyPersistentException
        {
            return new Boolean(attrValue);
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.CurrencyType)
         */
        public BigDecimal handleValue(CurrencyType aType)
                throws JdyPersistentException
        {
            return new BigDecimal(attrValue);
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.DateType)
         */
        public Date handleValue(TimeStampType aType) throws JdyPersistentException
        {
            System.out.println(attrValue);
            return ISO8601Utils.parse(attrValue);
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.FloatType)
         */
        public Double handleValue(FloatType aType) throws JdyPersistentException
        {
            return new Double(attrValue);
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.LongType)
         */
        public Long handleValue(LongType aType) throws JdyPersistentException
        {
            return new Long(attrValue);
        }
        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.TextType)
         */

        public String handleValue(TextType aType) throws JdyPersistentException
        {
            return attrValue;
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.VarCharType)
         */
        public String handleValue(VarCharType aType) throws JdyPersistentException
        {
            return attrValue;
        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.primitive.PrimitiveTypeGetVisitor#handleValue(de.comafra.model.metainfo.primitive.BlobType)
         */
        public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
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
        server.start();

    }

}
