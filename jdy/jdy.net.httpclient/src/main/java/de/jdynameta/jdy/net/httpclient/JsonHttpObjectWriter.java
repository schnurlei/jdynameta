/**
 *
 * Copyright 2010 (C) rs <rainer@schnurlei.de>
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
 *
 */
package de.jdynameta.jdy.net.httpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.jdy.model.json.JsonFileReader;
import de.jdynameta.jdy.model.json.JsonFileWriter;
import de.jdynameta.metamodel.application.AppClassInfo;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 * Write /read Json Objects over HTTP Request to/from a server
 *
 * @author rainer
 *
 */
@SuppressWarnings("serial")
public class JsonHttpObjectWriter extends JsonHttpObjectHandler implements ObjectWriter
{
    private JsonFileWriter fileWriter;
    private JsonFileReader fileReader;

    public JsonHttpObjectWriter(String aMetaModelNamespace)
    {
        this("localhost", 8080, "servlet", aMetaModelNamespace);
    }

    public JsonHttpObjectWriter(final String aHost, int aPort, final String aBasePath, String aMetaModelNamespace)
    {
        super(aHost, aPort, aBasePath, aMetaModelNamespace);
        this.fileReader = new JsonFileReader();
        this.fileWriter = new JsonFileWriter(new JsonFileWriter.WriteDependentAsProxyStrategy(), true);
    }

    @Override
    public TypedValueObject insertObjectInDb(ValueObject aObjToInsert, ClassInfo aInfo) throws JdyPersistentException
    {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(new TypedWrappedValueObject(aObjToInsert, aInfo));

        try
        {
            fileWriter.writeObjectList(printWriter, aInfo, singleElementList, PersistentOperation.Operation.INSERT);
        } catch (TransformerConfigurationException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }
        String content = writer.toString();
        ObjectList<ApplicationObj> result;
        try
        {
            String response = sendJsonPostRequest(createUrlForClassInfo(aInfo), content);
            result = fileReader.readObjectList(new StringReader(response), aInfo);
        } catch (URISyntaxException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

        return result.get(0);
    }

    @Override
    public void updateObjectToDb(ValueObject aObjToUpdate, ClassInfo aInfo) throws JdyPersistentException
    {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(new TypedWrappedValueObject(aObjToUpdate, aInfo));

        try
        {
            fileWriter.writeObjectList(printWriter, aInfo, singleElementList, PersistentOperation.Operation.UPDATE);
        } catch (TransformerConfigurationException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }
        String content = writer.toString();
        ObjectList<ApplicationObj> result;
        try
        {
            String response = sendJsonPutRequest(createUrlForClassInfo(aInfo), content);
            result = fileReader.readObjectList(new StringReader(response), aInfo);
        } catch (URISyntaxException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

    }

    public void executeWorkflowAction(String actionName, TypedValueObject aValueObjectFor, ClassInfo aInfo) throws JdyPersistentException
    {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(new TypedWrappedValueObject(aValueObjectFor, aInfo));

        try
        {
            fileWriter.writeObjectList(printWriter, aInfo, singleElementList, PersistentOperation.Operation.UPDATE);
        } catch (TransformerConfigurationException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }
        String content = writer.toString();
        ObjectList<ApplicationObj> result;
        try
        {
            String response = sendJsonPutRequest(createUrlForClassInfo(aInfo, actionName), content);
            result = fileReader.readObjectList(new StringReader(response), aInfo);
        } catch (URISyntaxException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

    }

    @Override
    public void deleteObjectInDb(ValueObject aObjToDelete, ClassInfo aInfo) throws JdyPersistentException
    {
        try
        {
            sendJsonDeleteRequest(createUrlForClassInfoAndValueObject(aInfo, aObjToDelete));
        } catch (URISyntaxException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

    }

    protected URI createUrlForClassInfo(ClassInfo aInfo) throws URISyntaxException
    {
        return createUrlForClassInfo(aInfo, null);
    }

    protected URI createUrlForClassInfo(ClassInfo aInfo, String query) throws URISyntaxException
    {
        String infoPath = createInfoPath(aInfo, this.metaModelNamespace, this.basePath);

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(this.host);
        builder.setPort(port);
        builder.setPath(infoPath);
        if (query != null)
        {
            builder.setQuery(query);
        }
        return builder.build();
    }

    protected static String createInfoPath(ClassInfo aInfo, String aMetaModelNamespace, String aBasePath)
    {
        // pseudo namespace for meta information 
        String nameSpacePart = "@jdy";
        if (!aInfo.getRepoName().equals(aMetaModelNamespace))
        {
            nameSpacePart = aInfo.getRepoName();
            //@TODO : switch from namespace to repository 
            if (aInfo instanceof AppClassInfo)
            {
                nameSpacePart = ((AppClassInfo) aInfo).getRepository().getApplicationName();
            }
        }
        String className = aInfo.getInternalName();
        // check basepath is null
        String infoPath = (aBasePath == null) ? "" : aBasePath;
        // check whether path ends with /
        infoPath = (infoPath.endsWith("/")) ? infoPath : infoPath + "/";
        infoPath += nameSpacePart + "/" + className;

        return infoPath;
    }

    protected URI createUrlForClassInfoAndValueObject(ClassInfo aInfo, ValueObject aValueObject) throws URISyntaxException, JdyPersistentException
    {
        String infoPath = createInfoPath(aInfo, this.metaModelNamespace, this.basePath);

//		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
//		qparams.add(new BasicNameValuePair("keyvalue", "1000"));
        HttpParameterHandler paraHandler = new HttpParameterHandler();
        aInfo.handleAttributes(paraHandler, aValueObject);

        return URIUtils.createURI("http", this.host, port, infoPath, URLEncodedUtils.format(paraHandler.qparams, "UTF-8"), null);

    }

    /**
     *
     * @param aUri
     * @return
     * @throws JdyPersistentException
     */
    protected String sendJsonGetRequest(URI aUri) throws JdyPersistentException
    {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 500);
        //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 500);

        HttpGet httpget = new HttpGet(aUri);

//		get.setHeader("Content-Type", "application/json");
//		get.setHeader("Accept", "application/json");
        HttpResponse response = null;
        try
        {
            response = httpclient.execute(httpget);

        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        String responseString = "";
        try
        {
            HttpEntity entity = response.getEntity();
            long len = entity.getContentLength();
            if (len != -1 && len < 10000)
            {
                responseString = EntityUtils.toString(entity);
            } else
            {
                // Stream content out
            }
        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        // Check response code
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
        {
            throw new JdyPersistentException("Received error status " + response.getStatusLine().getStatusCode());
        }

        return responseString;
    }

    /**
     *
     * @param aUri
     * @param sendString
     * @return
     * @throws JdyPersistentException
     */
    protected String sendJsonPostRequest(URI aUri, String sendString) throws JdyPersistentException
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(aUri);

        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
        httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

        httpPost.setEntity(new StringEntity(sendString, "UTF-8"));

        HttpResponse response = null;
        try
        {
            response = httpclient.execute(httpPost);
        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        String responseString = "";
        try
        {
            HttpEntity entity = response.getEntity();
            long len = entity.getContentLength();
            if (len != -1 && len < 10000)
            {
                responseString = EntityUtils.toString(entity);
            } else
            {
                // Stream content out
            }
        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        // Check response code
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED)
        {
            throw new JdyPersistentException("Received error status " + response.getStatusLine().getStatusCode());
        }

        return responseString;
    }

    /**
     *
     * @param aUri
     * @param sendString
     * @return
     * @throws JdyPersistentException
     */
    protected String sendJsonPutRequest(URI aUri, String sendString) throws JdyPersistentException
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(aUri);

        httpPut.setEntity(new StringEntity(sendString, "UTF-8"));

        HttpResponse response = null;
        try
        {
            response = httpclient.execute(httpPut);
        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        String responseString = "";
        try
        {
            HttpEntity entity = response.getEntity();
            long len = entity.getContentLength();
            responseString = EntityUtils.toString(entity);

        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        // Check response code
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED)
        {
            throw new JdyPersistentException("Received error status " + response.getStatusLine().getStatusCode());
        }

        return responseString;
    }

    /**
     *
     * @param uri
     * @return
     * @throws JdyPersistentException
     */
    protected void sendJsonDeleteRequest(URI uri) throws JdyPersistentException
    {
        HttpClient httpclient = new DefaultHttpClient();

        HttpDelete httpDelete;
        httpDelete = new HttpDelete(uri);

//		putRequest.setRequestHeader("Content-Type", "application/json");
//		putRequest.setRequestHeader("Accept", "application/json");
        HttpResponse response = null;
        try
        {
            response = httpclient.execute(httpDelete);
        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        // Check response code
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
        {
            throw new JdyPersistentException("Received error status " + response.getStatusLine().getStatusCode());
        }

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

//	public static void main(String[] args)
//	{
//		String host = "localhost";
//		int port = 8080;
//		String basePath = "restserver/JdyServlet";
//		
//		
//		try
//		{
//
//			MetaModelRepository rep = MetaModelRepository.getSingleton();
//			
//			NameSpaceInfoModelImpl newNameSpace = new NameSpaceInfoModelImpl();
//			newNameSpace.setNameResource("TestNameSpace1"+ System.currentTimeMillis());
//			ClassInfoModelImpl newClassInfo = new ClassInfoModelImpl();
//			newClassInfo.setExternalName("TestRscEx"+ System.currentTimeMillis());
//			newClassInfo.setInternalName("TestRscItoday"+ System.currentTimeMillis());
//			newClassInfo.setNameSpace(newNameSpace);
//			newClassInfo.setShortName("TRSH");
//			newClassInfo.setIsAbstract(new Boolean(false));
//
//			new JsonHttpObjectWriter(host, port, basePath).insertObjectInDb(newNameSpace, rep.getNameSpaceModelInfo());
//			new JsonHttpObjectWriter(host, port, basePath).insertObjectInDb(newClassInfo, rep.getClassInfoModelInfo());
//
//			newClassInfo.setShortName("chan");
//			new JsonHttpObjectWriter(host, port, basePath).updateObjectToDb(newClassInfo, rep.getClassInfoModelInfo());
//			
//			DefaultClassInfoQuery query = new DefaultClassInfoQuery(MetaModelRepository.getSingleton().getClassInfoModelInfo());
//			ObjectList<TypedValueObject> readObjects = new JsonHttpObjectWriter(host, port, basePath).loadValuesFromDb(query);
//			System.out.println(readObjects);
//			
//			new JsonHttpObjectWriter(host, port, basePath).deleteObjectInDb(readObjects.get(0), MetaModelRepository.getSingleton().getClassInfoModelInfo());
//		
//		} catch (NullPointerException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JdyPersistentException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.exit(0);
//	}	
    public static class HttpParameterHandler implements AttributeHandler
    {
        private final ArrayList<AttributeInfo> stack;
        private final List<NameValuePair> qparams;
        private final JsonPrimitiveHandler typeHandler;

        public HttpParameterHandler()
        {
            this.stack = new ArrayList<>();
            this.qparams = new ArrayList<>();
            this.typeHandler = new JsonPrimitiveHandler();
        }

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                throws JdyPersistentException
        {
            if (aInfo.isKey())
            {
                stack.add(aInfo);
                aInfo.getReferencedClass().handleAttributes(this, objToHandle);
                stack.remove(aInfo);
            }
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                throws JdyPersistentException
        {
            if (aInfo.isKey())
            {

                String value = "";
                if (objToHandle != null)
                {
                    aInfo.getType().handlePrimitiveKey(typeHandler, objToHandle);
                    value = typeHandler.value;
                }
                NameValuePair valuePair = new BasicNameValuePair(createName(stack, aInfo), value);
                qparams.add(valuePair);
            }
        }

        private String createName(ArrayList<AttributeInfo> aStack, PrimitiveAttributeInfo aInfo)
        {
            StringBuilder buffer = new StringBuilder();

            for (AttributeInfo attributeInfo : aStack)
            {
                buffer.append(attributeInfo.getInternalName()).append(".");
            }

            buffer.append(aInfo.getInternalName());

            return buffer.toString();
        }
    }

    private static class JsonPrimitiveHandler implements PrimitiveTypeVisitor
    {
        private String value;

        public JsonPrimitiveHandler()
        {
            super();
        }

        @Override
        public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
        {
            value = aValue.toPlainString();
        }

        @Override
        public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
        {
            value = aValue.toString();
        }

        @Override
        public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
        {
            value = toRFC3339(aValue, true);
        }

        @Override
        public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
        {
            value = aValue.toString();
        }

        @Override
        public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
        {
            value = aValue.toString();
        }

        @Override
        public void handleValue(String aValue, TextType aType) throws JdyPersistentException
        {
            value = aValue;
        }

        @Override
        public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
        {
            value = aValue;
        }

        @Override
        public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
        {
			// TODO Auto-generated method stub
        }
    }

    /**
     * Copyright (C) 2006-2008 S.D.I.-Consulting BVBA
     *
     * @param date
     * @param timezoneIgnored
     * @return
     */
    public static String toRFC3339(Date date, boolean timezoneIgnored)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setLenient(false);
        String dateString = dateFormat.format(date);
        int length = dateString.length();
        if (timezoneIgnored)
        {
            dateString = dateString.substring(0, length - 5);
        } else
        {
            dateString = dateString.substring(0, length - 2) + ":" + dateString.substring(length - 2);
        }
        return dateString;
    }

}
