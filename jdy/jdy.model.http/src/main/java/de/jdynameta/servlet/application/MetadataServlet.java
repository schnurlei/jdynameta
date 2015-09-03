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
package de.jdynameta.servlet.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.TransformerConfigurationException;

import de.jdynameta.application.WorkflowException;
import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.json.JsonCompactFileReader;
import de.jdynameta.json.JsonCompactFileReader.GeneratedValueCreator;
import de.jdynameta.json.JsonFileReader;
import de.jdynameta.json.JsonFileWriter;
import de.jdynameta.json.client.JsonHttpObjectReader;
import de.jdynameta.metamodel.filter.AppFilterExpr;
import de.jdynameta.metamodel.filter.AppQuery;
import de.jdynameta.metamodel.filter.FilterCreator;
import de.jdynameta.metamodel.filter.FilterRepository;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 * Servlet that provides a HTTP/JSON access to Metadata
 *
 * @author rs
 *
 */
public class MetadataServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private JsonFileWriter fileWriter;
    private final JsonFileReader fileReader = new JsonFileReader();

    private MetadataManager metaManager;

    public MetadataServlet()
    {

    }

    @Override
    public void init() throws ServletException
    {
        super.init();
        InitialContext ic;
        DataSource ds;
        try
        {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("jdbc/jdynametadb");
        } catch (Exception e)
        {
            throw new ServletException(e);
        }
        fileWriter = new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true);
        getServletContext().log("+++++++++++++++++++++++++++Init JDynametaRestServlet");
        metaManager = new MetadataManager(ds);
        try
        {
            boolean deleteSchema = getBoolenConfigValue("deleteSchema", false);
            boolean createTestSchema = getBoolenConfigValue("createTestSchema", false);

            metaManager.setupApplicationManager(deleteSchema, createTestSchema);
        } catch (JdyPersistentException | InvalidClassInfoException | JdbcSchemaHandler.SchemaValidationException e)
        {
            getServletContext().log(e.getLocalizedMessage());
        }
    }

    private boolean getBoolenConfigValue(String configValueName, boolean defaultValue)
    {
        boolean result = defaultValue;
        Object deleteSchemaAttr = getServletContext().getAttribute(configValueName);
        if (deleteSchemaAttr instanceof Boolean)
        {
            result = (Boolean) deleteSchemaAttr;
            getServletContext().log("+++++++++++++ Config Value " + configValueName + ": " + result);
        }
        return result;
    }

    @Override
    /**
     * do a query on the defined ClassInfo defined by the path information
     *
     * the response is a Json String with the read objects
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (!metaManager.isDbInitialized())
        {
            throw new ServletException("Database not initialized");
        }

        MetadataPathInfo pathInfo = new MetadataPathInfo(req);
        Map paraMap = req.getParameterMap();

        try
        {
            ClassRepository repo = metaManager.getRepository(pathInfo);
            ClassInfo classInfo = repo.getClassForName(pathInfo.getClassName());
            if (classInfo == null)
            {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else
            {
                ClassInfoQuery query = createQueryForString(pathInfo.getFilter(), repo, pathInfo.getClassName());
                if (!paraMap.isEmpty())
                {
                    getServletContext().log("+++++++++++++++++++++++++++Handle GET parameter");
                }

                try
                {
                    ObjectList<? extends TypedValueObject> objects;
                    objects = metaManager.loadObjectsFromDb(query);

                    try
                    {
                        resp.setBufferSize(16 * 1024);
                        StringWriter writer = new StringWriter();
                        fileWriter.writeObjectList(writer, classInfo, objects, Operation.READ);
                        resp.getWriter().print(writer.toString());

                        resp.setContentType("application/json;charset=utf-8");
                        resp.setStatus(HttpServletResponse.SC_OK);
                    } catch (TransformerConfigurationException ex)
                    {
                        ex.printStackTrace();
                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error coverting request: " + ex.getLocalizedMessage());
                    }
                } catch (JdyPersistentException e)
                {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (ParseException | JdyPersistentException | ObjectCreationException ex)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No valid class in namespace: " + ex.getLocalizedMessage());
        }

    }

    public static ClassInfoQuery createQueryForString(String aFilterExpr, ClassRepository repo, String aClassName) throws JdyPersistentException, ObjectCreationException
    {
        if (aFilterExpr != null)
        {
            GeneratedValueCreator valueGenerator = new GeneratedValueCreator()
            {
                public long nextValue = 0;

                @Override
                public Object createValue(ClassInfo aClassInfo, AttributeInfo aAttrInfo)
                {
                    return nextValue++;
                }

                @Override
                public boolean canGenerateValue(ClassInfo aClassInfo, AttributeInfo aAttrInfo)
                {
                    return (aAttrInfo instanceof PrimitiveAttributeInfo)
                            && ((PrimitiveAttributeInfo) aAttrInfo).getType() instanceof LongType
                            && ((PrimitiveAttributeInfo) aAttrInfo).isGenerated();
                }
            };

            HashMap<String, String> att2AbbrMap = FilterCreator.createAbbreviationMap();

            JsonCompactFileReader reader = new JsonCompactFileReader(att2AbbrMap, FilterRepository.getSingleton().getRepoName(), valueGenerator);
            ObjectList<ApplicationObj> result = reader.readObjectList(new StringReader(aFilterExpr), FilterRepository.getSingleton().getInfoForType(FilterRepository.TypeName.AppFilterExpr));

            FilterTransformator transformator = new FilterTransformator(FilterRepository.NAME_CREATOR);
            ObjectList<ReflectionChangeableValueObject> convertedList = JsonHttpObjectReader.convertValObjList(result, transformator);

            AppFilterExpr expr = (AppFilterExpr) convertedList.get(0);
            AppQuery newAppQuery = new AppQuery();
            newAppQuery.setExpr(expr);
            newAppQuery.setRepoName(repo.getRepoName());
            newAppQuery.setClassName(aClassName);

            FilterCreator creator = new FilterCreator();
            return creator.createMetaFilter(newAppQuery, repo);
        } else
        {
            ClassInfo classInfo = repo.getClassForName(aClassName);
            if (classInfo == null)
            {
                throw new JdyPersistentException("Invlaid class name " + aClassName);
            }
            return new DefaultClassInfoQuery(classInfo);
        }

    }

    @SuppressWarnings("serial")
    private static class FilterTransformator extends AbstractReflectionCreator<ReflectionChangeableValueObject>
            implements ObjectTransformator<ValueObject, ReflectionChangeableValueObject>
    {

        public FilterTransformator(ClassNameCreator aNameCreator)
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
        protected ReflectionChangeableValueObject createProxyObjectFor(
                TypedValueObject aObjToHandle)
        {
            return null;
        }

        @Override
        protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
                ReflectionChangeableValueObject aObjoSetVals,
                TypedValueObject aObjToGetVals) throws ObjectCreationException
        {

        }
    }

    @Override
    /**
     * convert the request content into a ValueObject and update the object into
     * the database and execute the workflow action when the query part is not
     * null the request path is interpreted as ClassInfo the request content is
     * a JSON Object of the ClssInfo
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (!metaManager.isDbInitialized())
        {
            throw new ServletException("Database not initialized");
        }

        MetadataPathInfo pathInfo = new MetadataPathInfo(req);

        try
        {
            ClassInfo classInfo = metaManager.getClassInfo(pathInfo);
            try
            {
                String content = MetadataPathInfo.readFromStream(req.getInputStream());
                ObjectList<ApplicationObj> objectList = fileReader.readObjectList(new StringReader(content), classInfo);
                ApplicationObj objectToUpdate = objectList.get(0);

                TypedValueObject wrappedObj;
                if (pathInfo.getFilter() != null && pathInfo.getFilter().length() > 0)
                {
                    wrappedObj = this.metaManager.executeWorkFlowAction(pathInfo.getFilter(), objectToUpdate);
                } else
                {
                    wrappedObj = this.metaManager.updateObject(objectToUpdate);
                }

                try
                {
                    resp.setBufferSize(16 * 1024);
                    DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(new TypedWrappedValueObject(wrappedObj, wrappedObj.getClassInfo()));
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

            } catch (JdyPersistentException e)
            {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } catch (WorkflowException ex)
            {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No valid class in namespace: " + ex.getLocalizedMessage());
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
        if (!metaManager.isDbInitialized())
        {
            throw new ServletException("Database not initialized");
        }

        MetadataPathInfo pathInfo = new MetadataPathInfo(req);

        try
        {
            ClassInfo classInfo = metaManager.getClassInfo(pathInfo);
            try
            {
                String content = MetadataPathInfo.readFromStream(req.getInputStream());
                ObjectList<ApplicationObj> allObjects = fileReader.readObjectList(new StringReader(content), classInfo);
                ApplicationObj objectToInsert = allObjects.get(0);

                TypedValueObject newObject = this.metaManager.insertObject(objectToInsert);

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
                ex.printStackTrace();
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

        if (!metaManager.isDbInitialized())
        {
            throw new ServletException("Database not initialized");
        }

        Map paraMap = req.getParameterMap();
        MetadataPathInfo pathInfo = new MetadataPathInfo(req);

        try
        {
            ClassInfo classInfo = metaManager.getClassInfo(pathInfo);

            if (!paraMap.isEmpty())
            {
                getServletContext().log("+++++++++++++++++++++++++++Handle DELETE parameter");

                RequestParameterHandler handler = new RequestParameterHandler(paraMap, classInfo, null);
                classInfo.handleAttributes(handler, null);
                TypedValueObject objToDelete = handler.result;

                this.metaManager.deleteObject(objToDelete);

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

    public void showInHtmlBody(String text, PrintWriter out)
    {
        out.println("<HTML>");
        out.println("<HEAD><TITLE>Hello World</TITLE></HEAD>");
        out.println("<BODY>");
        out.println("<p>" + text + "</p>");

        out.println("</BODY></HTML>");

    }

}
